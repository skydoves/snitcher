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

import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.storage.SNITCHER_STORE_FILE_NAME
import com.skydoves.snitcher.storage.SnitcherStore
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Installs Snitcher as the default uncaught exception handler of the JVM, which captures every
 * exception that is not handled by the application and persists it before the handler returns.
 *
 * Unlike Android, the desktop JVM keeps running after an uncaught exception on a background thread,
 * so the captured crash is published through [Snitcher.exception] right away and you can render it
 * with `SnitcherTraceWindow`.
 *
 * @param storageDirectory The directory that holds the persisted crash.
 * @param theme The theme that styles the pre-built screens.
 * @param strings The texts of the pre-built screens.
 * @param platformInfo The application and system description shown under the exception message.
 * @param isDebuggable Whether the full trace screen is displayed instead of the restore screen.
 * @param exceptionHandler An extra handler, such as a logger, which is called with the captured
 * exception when the application crashes.
 */
public fun Snitcher.install(
  storageDirectory: String = defaultStorageDirectory(),
  theme: SnitcherThemeConfig = SnitcherThemeConfig(),
  strings: SnitcherStrings = SnitcherStrings(),
  platformInfo: String = defaultPlatformInfo(),
  isDebuggable: Boolean = true,
  exceptionHandler: (SnitcherException) -> Unit = {},
) {
  this.theme = theme
  this.strings = strings
  this.platformInfo = platformInfo
  this.isDebuggable = isDebuggable

  bind(
    store = SnitcherStore(
      FileSystem.SYSTEM,
      storageDirectory.toPath().resolve(SNITCHER_STORE_FILE_NAME),
    ),
    launcher = null,
    exceptionHandler = exceptionHandler,
  )

  val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
  Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
    if (isRethrowing) {
      // the debug action threw the recorded crash again, so the record stays as it is.
      isRethrowing = false
    } else {
      capture(throwable = throwable, launcher = null)
    }

    // a JVM without a default handler prints the crash itself, and installing one silences that,
    // so the trace keeps reaching the console either way.
    val handler = defaultExceptionHandler
    if (handler != null) {
      handler.uncaughtException(thread, throwable)
    } else {
      throwable.printStackTrace()
    }
  }
}

private fun defaultStorageDirectory(): String = System.getProperty("user.home") + "/.snitcher"

private fun defaultPlatformInfo(): String = buildString {
  append(System.getProperty("os.name").orEmpty())
  append(" ")
  append(System.getProperty("os.version").orEmpty())
  append(" (JVM ")
  append(System.getProperty("java.version").orEmpty())
  append(")")
}
