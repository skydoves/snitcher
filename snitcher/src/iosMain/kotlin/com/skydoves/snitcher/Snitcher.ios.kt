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
@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package com.skydoves.snitcher

import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.storage.SNITCHER_STORE_FILE_NAME
import com.skydoves.snitcher.storage.SnitcherStore
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.invoke
import kotlinx.cinterop.staticCFunction
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSException
import platform.Foundation.NSGetUncaughtExceptionHandler
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSSetUncaughtExceptionHandler
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import kotlin.native.setUnhandledExceptionHook
import kotlin.native.terminateWithUnhandledException

/**
 * Installs Snitcher on iOS, which records a crash and leaves the termination to the runtime.
 *
 * An unhandled Kotlin exception terminates the process, so the crash cannot be displayed while it
 * happens. Snitcher records it synchronously, leaves the termination to the runtime exactly as it
 * would happen without Snitcher, and publishes the crash through [Snitcher.exception] on the next
 * launch, where you can present `snitcherViewController` or your own screen.
 *
 * @param storageDirectory The directory that holds the persisted crash.
 * @param theme The theme that styles the pre-built screens.
 * @param strings The texts of the pre-built screens.
 * @param platformInfo The application and device description shown under the exception message.
 * @param catchObjectiveCExceptions Whether an uncaught `NSException` raised by Objective-C or Swift
 * should be recorded as well. Unix signals, such as the ones raised by a Swift runtime trap or by a
 * memory error, are never recorded.
 * @param exceptionHandler An extra handler, such as a logger, which is called with the captured
 * exception when the application crashes.
 */
public fun Snitcher.install(
  storageDirectory: String = defaultStorageDirectory(),
  theme: SnitcherThemeConfig = SnitcherThemeConfig(),
  strings: SnitcherStrings = SnitcherStrings(),
  platformInfo: String = defaultPlatformInfo(),
  catchObjectiveCExceptions: Boolean = true,
  exceptionHandler: (SnitcherException) -> Unit = {},
) {
  this.theme = theme
  this.strings = strings
  this.platformInfo = platformInfo
  this.isDebuggable = Platform.isDebugBinary

  bind(
    store = SnitcherStore(
      FileSystem.SYSTEM,
      storageDirectory.toPath().resolve(SNITCHER_STORE_FILE_NAME),
    ),
    launcher = null,
    exceptionHandler = exceptionHandler,
  )

  // installing the hook twice would chain Snitcher into itself and recurse until the stack dies,
  // so the handlers are installed once and the previous ones are kept.
  if (!hookInstalled) {
    hookInstalled = true
    previousHook = setUnhandledExceptionHook(snitcherHook)
  }

  if (catchObjectiveCExceptions && !objcHandlerInstalled) {
    objcHandlerInstalled = true
    previousObjcHandler = NSGetUncaughtExceptionHandler()
    NSSetUncaughtExceptionHandler(
      staticCFunction<NSException?, Unit> { exception ->
        if (exception != null) {
          Snitcher.capture(exception.toSnitcherException(), null)
        }
        previousObjcHandler?.let { handler ->
          exception?.let { handler.invoke(it) }
        }
      },
    )
  }
}

/**
 * Installs Snitcher with the default configuration.
 *
 * Kotlin default arguments do not cross the Objective-C boundary, so this is the entry point to
 * call from Swift when the defaults are enough: `Snitcher.shared.install()`.
 */
public fun Snitcher.install() {
  install(
    storageDirectory = defaultStorageDirectory(),
    theme = SnitcherThemeConfig(),
    strings = SnitcherStrings(),
    platformInfo = defaultPlatformInfo(),
    catchObjectiveCExceptions = true,
    exceptionHandler = {},
  )
}

private val snitcherHook: (Throwable) -> Unit = { throwable ->
  Snitcher.capture(throwable = throwable, launcher = null)

  val previous = previousHook
  if (previous != null) {
    previous.invoke(throwable)
  } else {
    // the runtime terminates the process and writes a crash report when no hook is installed, and
    // recording a crash must not take that away.
    terminateWithUnhandledException(throwable)
  }
}

private var hookInstalled = false
private var previousHook: ((Throwable) -> Unit)? = null
private var objcHandlerInstalled = false
private var previousObjcHandler: CPointer<CFunction<(NSException?) -> Unit>>? = null

internal fun NSException.toSnitcherException(): SnitcherException {
  // callStackSymbols is only filled in once the exception was raised, and Objective-C hands back a
  // nil where Kotlin expects a list, so it has to be read as nullable.
  val callStack: List<*>? = callStackSymbols
  val symbols = callStack?.joinToString(separator = "\n") { it.toString() }.orEmpty()
  val name = name.orEmpty()
  val reason = reason.orEmpty()
  return SnitcherException(
    threadId = 0L,
    threadName = "main",
    packageName = name,
    message = reason,
    stackTrace = buildString {
      append(name)
      if (reason.isNotEmpty()) {
        append(": ")
        append(reason)
      }
      if (symbols.isNotEmpty()) {
        append("\n")
        append(symbols)
      }
    },
  )
}

private fun defaultStorageDirectory(): String {
  val paths = NSSearchPathForDirectoriesInDomains(
    directory = NSCachesDirectory,
    domainMask = NSUserDomainMask,
    expandTilde = true,
  )
  return paths.firstOrNull() as? String ?: NSBundle.mainBundle.bundlePath
}

private fun defaultPlatformInfo(): String {
  val bundle = NSBundle.mainBundle
  val version = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
  val build = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String
  val device = UIDevice.currentDevice
  return buildString {
    if (version != null) {
      append(version)
      if (build != null) {
        append(" (")
        append(build)
        append(")")
      }
      append(" ")
    }
    append(device.model)
    append(" ")
    append(device.systemName)
    append(" ")
    append(device.systemVersion)
  }
}
