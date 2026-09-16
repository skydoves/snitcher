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
package com.skydoves.snitcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.skydoves.snitcher.extensions.findActivity

/**
 * Adjusts the system bar icons to the given [color], which is the color drawn behind the system
 * bars by the Snitcher screens. The previous appearance is restored when this leaves composition.
 */
@Composable
internal fun SnitcherStatusBarColor(color: Color = SnitcherTheme.colors.background) {
  val view = LocalView.current
  if (view.isInEditMode) {
    return
  }

  val darkIcons = color.luminance() > 0.5f
  DisposableEffect(view, darkIcons) {
    val window = view.context.findActivity()?.window
      ?: return@DisposableEffect onDispose { }
    val controller = WindowCompat.getInsetsController(window, view)
    val statusBars = controller.isAppearanceLightStatusBars
    val navigationBars = controller.isAppearanceLightNavigationBars

    controller.isAppearanceLightStatusBars = darkIcons
    controller.isAppearanceLightNavigationBars = darkIcons

    onDispose {
      controller.isAppearanceLightStatusBars = statusBars
      controller.isAppearanceLightNavigationBars = navigationBars
    }
  }
}
