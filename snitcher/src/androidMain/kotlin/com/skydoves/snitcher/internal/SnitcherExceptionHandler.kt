/*
 * Designed and developed by 2023 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skydoves.snitcher.internal

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.TraceStrategy
import com.skydoves.snitcher.ui.ExceptionTraceActivity
import kotlin.reflect.KClass
import kotlin.system.exitProcess

/**
 * A universal exception handler that captures every exception within the application and directs
 * it to the exception tracing screen.
 */
internal class SnitcherExceptionHandler(
  application: Application,
  private val launcher: String?,
  private val traceActivityClass: KClass<*>,
  private val traceStrategy: TraceStrategy,
  private val defaultExceptionHandler: Thread.UncaughtExceptionHandler,
) : Thread.UncaughtExceptionHandler {

  private var lastActivity: Activity? = null
  private var activityCount = 0

  init {
    application.registerActivityLifecycleCallbacks(
      object : Application.ActivityLifecycleCallbacks {

        override fun onActivityResumed(activity: Activity) = Unit

        override fun onActivityPaused(activity: Activity) = Unit

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

        override fun onActivityDestroyed(activity: Activity) = Unit

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
          if (isExceptionActivity(activity)) {
            return
          }

          lastActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
          if (isExceptionActivity(activity)) {
            return
          }
          activityCount++
          lastActivity = activity
        }

        override fun onActivityStopped(activity: Activity) {
          if (isExceptionActivity(activity)) {
            return
          }
          activityCount--
          if (activityCount < 0) {
            lastActivity = null
          }
        }
      },
    )
  }

  private fun isExceptionActivity(activity: Activity) = activity is ExceptionTraceActivity

  override fun uncaughtException(thread: Thread, throwable: Throwable) {
    val activity = lastActivity
    if (activity == null) {
      defaultExceptionHandler.uncaughtException(thread, throwable)
      return
    }

    // persists the crash synchronously, so that it survives the process death below.
    Snitcher.capture(throwable = throwable, launcher = launcher ?: activity.javaClass.name)

    // call the default exception handler for integrating with other libraries.
    callDefaultExceptionHandler(thread, throwable)

    if (traceStrategy == TraceStrategy.CO_WORK) {
      launchExceptionTracingActivity(activity)
    }

    // kill the current process.
    Process.killProcess(Process.myPid())
    exitProcess(EXIT_CODE)
  }

  private fun launchExceptionTracingActivity(activity: Activity) = activity.run {
    val traceActivity = Class.forName(traceActivityClass.java.name)

    startActivity(
      Intent().setClass(this, traceActivity).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      },
    )
    finish()
  }

  private fun callDefaultExceptionHandler(thread: Thread, throwable: Throwable) {
    try {
      val canonicalName = defaultExceptionHandler::class.java.canonicalName
      if (canonicalName?.startsWith("com.android.internal.os") != true &&
        canonicalName?.startsWith("com.google.firebase.crashlytics") != true
      ) {
        defaultExceptionHandler.uncaughtException(thread, throwable)
      }
    } catch (_: Exception) {
    }
  }

  private companion object {
    private const val EXIT_CODE = 10
  }
}
