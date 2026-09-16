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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.skydoves.snitcher.internal.toSnitcherException
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.model.SnitcherPreference
import com.skydoves.snitcher.storage.SnitcherStore
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import com.skydoves.snitcher.ui.theme.resolved
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The shared state of Snitcher across every platform. It holds the most recent crash, the theme and
 * the texts of the pre-built screens, and it persists a crash so that it survives the process death
 * that follows it.
 *
 * Install Snitcher with the platform installer, `Snitcher.install(..)`, which is provided by the
 * Android, desktop, and iOS source sets.
 */
public object Snitcher {

  private val _exception: MutableStateFlow<SnitcherException?> = MutableStateFlow(null)

  /** Represents the state flow of the most recent [SnitcherException] encountered by an application. */
  public val exception: StateFlow<SnitcherException?> = _exception.asStateFlow()

  /**
   * The most recent [SnitcherException], or null when the application did not crash. It is the
   * typed counterpart of `exception.value`, which Objective-C and Swift only see as `Any?`.
   */
  public val latestException: SnitcherException?
    get() = _exception.value

  /**
   * The launcher that will be started when your application is recovered from the exception tracing
   * screen. It only exists on Android, which is the only platform that can relaunch an application,
   * and the Android source set exposes it as `Snitcher.launcher`.
   */
  internal val launcherFlow: MutableStateFlow<String> = MutableStateFlow("")

  private var themeConfig: SnitcherThemeConfig by mutableStateOf(SnitcherThemeConfig().resolved())

  /**
   * The theme that styles the pre-built Snitcher screens. Colors that were left unspecified are
   * resolved to the color they follow when the theme is assigned, so readers never meet
   * [androidx.compose.ui.graphics.Color.Unspecified].
   */
  public var theme: SnitcherThemeConfig
    get() = themeConfig
    set(value) {
      themeConfig = value.resolved()
    }

  /** The texts that are displayed by the pre-built Snitcher screens. */
  public var strings: SnitcherStrings by mutableStateOf(SnitcherStrings())

  /**
   * The application and device description that is rendered under the exception message, such as
   * `1.0.3 (4) Google Pixel 9`. Each platform installer fills it in.
   */
  public var platformInfo: String by mutableStateOf("")

  /**
   * Whether the full exception trace screen should be displayed instead of the restore screen.
   * The platform installers set it from the build type, and you can override it afterwards.
   */
  public var isDebuggable: Boolean by mutableStateOf(true)

  /** Whether the platform installer was already called. */
  public val isInstalled: Boolean
    get() = store != null

  internal var store: SnitcherStore? = null
    private set

  internal var exceptionHandler: (SnitcherException) -> Unit = {}
    private set

  /**
   * Whether the exception that is about to arrive was thrown again on purpose, by the debug action
   * of the trace screen, in which case it must not replace the crash that is already recorded.
   */
  internal var isRethrowing: Boolean = false

  /** Removes the persisted crash, so that it is not displayed again on the next launch. */
  public fun clear() {
    store?.clear()
    _exception.value = null
    launcherFlow.value = ""
  }

  internal fun bind(
    store: SnitcherStore,
    launcher: String?,
    exceptionHandler: (SnitcherException) -> Unit,
  ) {
    this.store = store
    this.exceptionHandler = exceptionHandler

    val preference = store.read()
    if (preference != null) {
      _exception.value = preference.snitcherException
      launcherFlow.value = preference.launcher.orEmpty()
    } else if (launcher != null) {
      launcherFlow.value = launcher
    }
  }

  /**
   * Captures the given [throwable], persists it synchronously, and runs the custom exception
   * handler. It is called by the platform installers and returns the captured model.
   */
  internal fun capture(throwable: Throwable, launcher: String?): SnitcherException =
    capture(throwable.toSnitcherException(), launcher)

  /**
   * Persists an already built [exception] synchronously and runs the custom exception handler.
   */
  internal fun capture(exception: SnitcherException, launcher: String?): SnitcherException {
    val resolvedLauncher = launcher ?: launcherFlow.value.takeIf { it.isNotEmpty() }

    store?.write(SnitcherPreference(snitcherException = exception, launcher = resolvedLauncher))
    _exception.value = exception
    if (resolvedLauncher != null) {
      launcherFlow.value = resolvedLauncher
    }

    runCatching { exceptionHandler.invoke(exception) }
    return exception
  }
}
