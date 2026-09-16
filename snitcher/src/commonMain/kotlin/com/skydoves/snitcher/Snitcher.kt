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

  private val _launcher: MutableStateFlow<String> = MutableStateFlow("")

  /**
   * Depicts the state flow of the launcher that will be started when your application is recovered
   * from the exception tracing screen. It is only meaningful on Android.
   */
  public val launcher: StateFlow<String> = _launcher.asStateFlow()

  /** The theme that styles the pre-built Snitcher screens. */
  public var theme: SnitcherThemeConfig by mutableStateOf(SnitcherThemeConfig())

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

  /** Whether [install] was already called on this platform. */
  public val isInstalled: Boolean
    get() = store != null

  internal var store: SnitcherStore? = null
    private set

  internal var exceptionHandler: (SnitcherException) -> Unit = {}
    private set

  /** Removes the persisted crash, so that it is not displayed again on the next launch. */
  public fun clear() {
    store?.clear()
    _exception.value = null
    _launcher.value = ""
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
      _launcher.value = preference.launcher.orEmpty()
    } else if (launcher != null) {
      _launcher.value = launcher
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
    val resolvedLauncher = launcher ?: _launcher.value.takeIf { it.isNotEmpty() }

    store?.write(SnitcherPreference(snitcherException = exception, launcher = resolvedLauncher))
    _exception.value = exception
    if (resolvedLauncher != null) {
      _launcher.value = resolvedLauncher
    }

    runCatching { exceptionHandler.invoke(exception) }
    return exception
  }
}
