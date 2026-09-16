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
import android.content.pm.ApplicationInfo
import android.os.Build
import com.skydoves.snitcher.extensions.packageInfo
import com.skydoves.snitcher.extensions.versionCode
import com.skydoves.snitcher.internal.SnitcherExceptionHandler
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.storage.SNITCHER_STORE_FILE_NAME
import com.skydoves.snitcher.storage.SnitcherStore
import com.skydoves.snitcher.ui.ExceptionTraceActivity
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import kotlin.reflect.KClass

/**
 * Installs Snitcher as the default uncaught exception handler of the application, which captures
 * every crash, persists it, and launches the exception tracing activity.
 *
 * @param application The [Application] to register for tracing activity lifecycles.
 * @param traceActivity An Activity that will be launched when your app experiences a crash.
 * @param traceStrategy The trace strategy determines the launch behaviors when your app experiences a crash.
 * @param theme The theme that styles the pre-built exception tracing screens.
 * @param strings The texts of the pre-built exception tracing screens.
 * @param exceptionHandler You can manage extra exception handlers, like logging exceptions on Firebase, by providing this lambda function here.
 * This handler will be called with a given [SnitcherException] when your app encounters exceptions.
 * It runs on the crashing thread, before the process is killed, so keep it short and do not block
 * on the network.
 */
public fun Snitcher.install(
  application: Application,
  traceActivity: KClass<*> = ExceptionTraceActivity::class,
  traceStrategy: TraceStrategy = TraceStrategy.CO_WORK,
  theme: SnitcherThemeConfig = SnitcherThemeConfig(),
  strings: SnitcherStrings = SnitcherStrings(),
  exceptionHandler: (SnitcherException) -> Unit = {},
) {
  installInternal(
    application = application,
    launcher = null,
    traceActivity = traceActivity,
    traceStrategy = traceStrategy,
    theme = theme,
    strings = strings,
    exceptionHandler = exceptionHandler,
  )
}

/**
 * Installs Snitcher as the default uncaught exception handler of the application, and restores the
 * application from the given [launcher] activity instead of from the most recent one.
 *
 * @param application The [Application] to register for tracing activity lifecycles.
 * @param traceActivity An Activity that will be launched when your app experiences a crash.
 * @param traceStrategy The trace strategy determines the launch behaviors when your app experiences a crash.
 * @param launcher A launcher Activity that used to restore your application from a crash.
 * @param theme The theme that styles the pre-built exception tracing screens.
 * @param strings The texts of the pre-built exception tracing screens.
 * @param exceptionHandler You can manage extra exception handlers, like logging exceptions on Firebase, by providing this lambda function here.
 * This handler will be called with a given [SnitcherException] when your app encounters exceptions.
 */
public inline fun <reified T : Activity> Snitcher.install(
  application: Application,
  traceActivity: KClass<*> = ExceptionTraceActivity::class,
  traceStrategy: TraceStrategy = TraceStrategy.CO_WORK,
  launcher: KClass<T> = T::class,
  theme: SnitcherThemeConfig = SnitcherThemeConfig(),
  strings: SnitcherStrings = SnitcherStrings(),
  noinline exceptionHandler: (SnitcherException) -> Unit = {},
) {
  installInternal(
    application = application,
    launcher = launcher.java.name,
    traceActivity = traceActivity,
    traceStrategy = traceStrategy,
    theme = theme,
    strings = strings,
    exceptionHandler = exceptionHandler,
  )
}

@PublishedApi
internal fun Snitcher.installInternal(
  application: Application,
  launcher: String?,
  traceActivity: KClass<*>,
  traceStrategy: TraceStrategy,
  theme: SnitcherThemeConfig,
  strings: SnitcherStrings,
  exceptionHandler: (SnitcherException) -> Unit,
) {
  val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler() ?: return

  this.theme = theme
  this.strings = strings
  this.platformInfo = application.platformInfo()
  this.isDebuggable = application.isDebuggableApp

  bind(
    store = SnitcherStore(
      fileSystem = FileSystem.SYSTEM,
      path = application.filesDir.toOkioPath().resolve(SNITCHER_STORE_FILE_NAME),
    ),
    launcher = launcher,
    exceptionHandler = exceptionHandler,
  )

  Thread.setDefaultUncaughtExceptionHandler(
    SnitcherExceptionHandler(
      application = application,
      launcher = launcher,
      traceActivityClass = traceActivity,
      traceStrategy = traceStrategy,
      defaultExceptionHandler = defaultExceptionHandler,
    ),
  )
}

private val Application.isDebuggableApp: Boolean
  get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

private fun Application.platformInfo(): String {
  val packageInfo = packageInfo()
  return "${packageInfo?.versionName} (${packageInfo?.versionCode()}) " +
    "${Build.MANUFACTURER} ${Build.MODEL}"
}
