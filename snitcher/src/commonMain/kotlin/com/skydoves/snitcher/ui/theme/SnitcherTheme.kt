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
import androidx.compose.runtime.remember
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.ui.ExceptionTraceScreen

/**
 * Local providers for various properties we connect to our components, for styling.
 *
 * They fall back to the installed [Snitcher.theme] so that a Snitcher screen also renders outside
 * of a [SnitcherTheme] block, rather than throwing.
 */
private val LocalColors = compositionLocalOf<SnitcherColor> {
  Snitcher.theme.lightColors.resolveUnspecified()
}

private val LocalTypography = compositionLocalOf<SnitcherTypography> {
  Snitcher.theme.typography
}

private val LocalShapes = compositionLocalOf<SnitcherShapes> {
  Snitcher.theme.shapes
}

/**
 * Snitcher Themes to be used for customizing [ExceptionTraceScreen].
 *
 * The default values are taken from the [SnitcherThemeConfig] that was given to
 * [Snitcher.install], so the pre-built screens and your own screens share the same theme.
 *
 * @param darkTheme Whether the dark colors should be used.
 * @param colors The colors to be used by the Snitcher components.
 * @param typography The text styles to be used by the Snitcher components.
 * @param shapes The shapes to be used by the Snitcher components.
 * @param content The content that will be styled with this theme.
 */
@Composable
public fun SnitcherTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  colors: SnitcherColor = if (darkTheme) {
    Snitcher.theme.darkColors
  } else {
    Snitcher.theme.lightColors
  },
  typography: SnitcherTypography = Snitcher.theme.typography,
  shapes: SnitcherShapes = Snitcher.theme.shapes,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalColors provides remember(colors) { colors.resolveUnspecified() },
    LocalTypography provides typography,
    LocalShapes provides shapes,
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

  /**
   * Retrieves the current [SnitcherTypography] at the call site's position in the hierarchy.
   */
  public val typography: SnitcherTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalTypography.current

  /**
   * Retrieves the current [SnitcherShapes] at the call site's position in the hierarchy.
   */
  public val shapes: SnitcherShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalShapes.current
}
