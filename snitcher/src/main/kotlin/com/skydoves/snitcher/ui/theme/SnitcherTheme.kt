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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.skydoves.snitcher.ui.ExceptionTraceScreen

/**
 * Local providers for various properties we connect to our components, for styling.
 */
private val LocalColors = compositionLocalOf<SnitcherColor> {
  error("No colors provided! Make sure to wrap all usages of Snitcher components in SnitcherTheme.")
}

/** Snitcher Themes to be used for customizing [ExceptionTraceScreen]. */
@Composable
public fun SnitcherTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  colors: SnitcherColor = if (darkTheme) {
    SnitcherColor.defaultDarkColors()
  } else {
    SnitcherColor.defaultColors()
  },
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalColors provides colors,
  ) {
    content()
  }
}

public object SnitcherTheme {
  /**
   * Retrieves the current [SnitcherColor] at the call site's position in the hierarchy.
   */
  public val colors: SnitcherColor
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current
}
