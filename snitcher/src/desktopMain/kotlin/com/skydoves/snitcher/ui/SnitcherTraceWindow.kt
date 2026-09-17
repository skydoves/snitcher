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
package com.skydoves.snitcher.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.debug
import com.skydoves.snitcher.ui.theme.SnitcherTheme

/**
 * Opens a window with the most recent crash whenever there is one, and closes it when the crash is
 * dismissed. Place it inside your `application { }` block, next to your own window.
 *
 * @param onCloseRequest Called when the window is closed. It clears the persisted crash by default.
 * @param title The title of the window.
 */
@Composable
public fun ApplicationScope.SnitcherTraceWindow(
  onCloseRequest: () -> Unit = { Snitcher.clear() },
  title: String = "Crash report",
) {
  val exception by Snitcher.exception.collectAsState()
  val snitcherException = exception ?: return

  Window(
    onCloseRequest = onCloseRequest,
    state = rememberWindowState(),
    title = title,
  ) {
    SnitcherTheme {
      if (Snitcher.isDebuggable) {
        ExceptionTraceScreen(
          snitcherException = snitcherException,
          onRestore = onCloseRequest,
          onDebug = { Snitcher.debug(snitcherException) },
        )
      } else {
        AppRestoreScreen(onRestore = onCloseRequest)
      }
    }
  }
}
