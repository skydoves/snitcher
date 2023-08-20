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
package com.skydoves.snitcher

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Process
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.skydoves.snitcher.Snitcher.Companion.install
import com.skydoves.snitcher.datastore.SnitcherDataStore
import com.skydoves.snitcher.datastore.SnitcherSerializer
import com.skydoves.snitcher.installer.SnitcherInstaller
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.model.SnitcherPreference
import com.skydoves.snitcher.model.toSnitcherElement
import com.skydoves.snitcher.ui.ExceptionTraceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.system.exitProcess

/**
 * A universal exception handler captures all exceptions within the application and directs them to
 * the exception tracing screen. This screen facilitates debugging error messages, and users
 * can either restart the crashed app or share the log messages.
 *
 * To set up the exception handler, use the [install] method.
 *
 * @property application The [Application] class to be registered for tracing activity lifecycles.
 * @property launcher A launcher that will be activated when you intend to restore from the exception tracing activity.
 * @property traceActivityClass An activity that will be initiated upon encountering a crash in your app.
 * @property traceStrategy The trace strategy determines the launch behaviors when your app experiences a crash.
 * @property defaultExceptionHandler The default exception handler initially provided by the Android OS.
 * @property exceptionHandler A custom exception handler to perform additional tasks in case of a crash.
 */
public class Snitcher(
  private val application: Application,
  private val launcher: String?,
  private val traceActivityClass: KClass<*>,
  private val traceStrategy: TraceStrategy,
  private val defaultExceptionHandler: Thread.UncaughtExceptionHandler,
  private inline val exceptionHandler: suspend (SnitcherException) -> Unit,
) : Thread.UncaughtExceptionHandler {

  private val dataStore: SnitcherDataStore = run {
    val dataStore = DataStoreFactory.create(SnitcherSerializer()) {
      application.dataStoreFile("proto_snitcher.pb")
    }
    SnitcherDataStore(dataStore)
  }

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
    val snitcherException = SnitcherException(
      threadId = thread.id,
      threadName = thread.name,
      packageName = throwable::class.java.simpleName,
      message = throwable.message.orEmpty(),
      stackTrace = throwable.stackTraceToString(),
      stackTraceElement = throwable.stackTrace.map { it.toSnitcherElement() },
    )

    // launch the exception tracing activity.
    lastActivity?.run {
      val launcher = this@Snitcher.launcher ?: this.javaClass.name

      // runs the custom exception handler.
      scope.launch {
        dataStore.updateSnitcherException(
          SnitcherPreference(
            snitcherException = snitcherException,
            launcher = launcher,
          ),
        )

        if (traceStrategy == TraceStrategy.CO_WORK) {
          exceptionHandler.invoke(snitcherException)
          launchExceptionTracingActivity(this@run)
        } else {
          exceptionHandler.invoke(snitcherException)
        }

        // kill the current process.
        Process.killProcess(Process.myPid())
        exitProcess(-1)
      }
    } ?: defaultExceptionHandler.uncaughtException(thread, throwable)
  }

  private fun launchExceptionTracingActivity(activity: Activity) =
    activity.run {
      val launcher = Class.forName(traceActivityClass.java.name)

      startActivity(
        Intent().setClass(this, launcher).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        },
      )
      finish()
    }

  public companion object {

    private val _exception: MutableStateFlow<SnitcherException?> = MutableStateFlow(null)

    /** Represents the state flow of the most recent [SnitcherException] encountered by an application. */
    public val exception: StateFlow<SnitcherException?> = _exception

    private val _launcher: MutableStateFlow<String> = MutableStateFlow("")

    /*
     * Depicts the state flow of the launcher package that will initiate when your application
     * is recovered within the exception tracing activity.
     */
    public val launcher: StateFlow<String> = _launcher

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
      scope.launch {
        SnitcherInstaller.internalSnitcher.collectLatest { snitcher ->
          snitcher?.dataStore?.data?.filterNotNull()?.collectLatest {
            _exception.value = it.snitcherException
            _launcher.value = it.launcher
              ?: throw IllegalArgumentException("launcher package name must not be null")
          }
        }
      }
    }

    /**
     * Represents if [Snitcher] is already installed or not. Lets you know if the
     * internal [Snitcher] instance is being used as the uncaught exception
     * handler when true or if it is using the default one if false.
     */
    public inline val isInstalled: Boolean
      get() = SnitcherInstaller.isInstalled

    @PublishedApi
    internal val Application.isDebuggableApp: Boolean
      get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    @PublishedApi
    internal var isDebuggable: Boolean = false

    /**
     * Install [Snitcher] into the installer.
     *
     * @param application Application.
     * @param traceActivity An Activity that will be launched when your app experiences a crash.
     * @param traceStrategy The trace strategy determines the launch behaviors when your app experiences a crash.
     * @param exceptionHandler You can manage extra exception handlers, like logging exceptions on Firebase, by providing this lambda function here.
     * This handler will be called with a given [SnitcherException] when your app encounters exceptions.
     */
    public fun install(
      application: Application,
      traceActivity: KClass<*> = ExceptionTraceActivity::class,
      traceStrategy: TraceStrategy = TraceStrategy.CO_WORK,
      exceptionHandler: (SnitcherException) -> Unit = {},
    ) {
      val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler() ?: return
      SnitcherInstaller.install(
        Snitcher(
          application = application,
          launcher = null,
          exceptionHandler = exceptionHandler,
          traceActivityClass = traceActivity,
          traceStrategy = traceStrategy,
          defaultExceptionHandler = defaultExceptionHandler,
        ),
      )
      isDebuggable = application.isDebuggableApp
    }

    /**
     * Install [Snitcher] into the installer.
     *
     * @param traceActivity An Activity that will be launched when your app experiences a crash.
     * @param traceStrategy The trace strategy determines the launch behaviors when your app experiences a crash.
     * @param launcher A launcher Activity that used to restore your application from a crash.
     * @param exceptionHandler You can manage extra exception handlers, like logging exceptions on Firebase, by providing this lambda function here.
     * This handler will be called with a given [SnitcherException] when your app encounters exceptions.
     */
    public inline fun <reified T : Activity> install(
      application: Application,
      traceActivity: KClass<*> = ExceptionTraceActivity::class,
      traceStrategy: TraceStrategy = TraceStrategy.CO_WORK,
      launcher: KClass<T> = T::class,
      noinline exceptionHandler: (SnitcherException) -> Unit = {},
    ) {
      val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler() ?: return
      SnitcherInstaller.install(
        Snitcher(
          application = application,
          launcher = launcher.java.name,
          exceptionHandler = exceptionHandler,
          traceActivityClass = traceActivity,
          traceStrategy = traceStrategy,
          defaultExceptionHandler = defaultExceptionHandler,
        ),
      )
      isDebuggable = application.isDebuggableApp
    }
  }
}
