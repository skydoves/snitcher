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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import com.skydoves.snitcher.ui.ExceptionTraceScreen

/**
 * A collection of colors, which are contained by [SnitcherTheme] to implement [ExceptionTraceScreen].
 *
 * @property primary The accent color, which is used for the title, the section labels, and the buttons.
 * @property onPrimary The content color that is drawn on top of [primary], such as the button labels.
 * @property background The background color of the Snitcher screens, which is also drawn behind the system bars.
 * @property textHighEmphasis The color of the primary text, such as the exception message and the stack trace.
 * @property textLowEmphasis The color of the secondary text, such as the package and device information.
 * When it is [Color.Unspecified], a dimmed [textHighEmphasis] is used.
 * @property outline The color of the outlines, such as the border of the stack trace container.
 * When it is [Color.Unspecified], [primary] is used.
 */
@Immutable
public data class SnitcherColor(
  val primary: Color,
  val background: Color,
  val textHighEmphasis: Color,
  val onPrimary: Color = Color.White,
  val textLowEmphasis: Color = Color.Unspecified,
  val outline: Color = Color.Unspecified,
) {
  public companion object {
    /**
     * Provides the default colors for the light mode of the app.
     *
     * @return A [SnitcherColor] instance holding our color palette.
     */
    public fun defaultColors(): SnitcherColor = SnitcherColor(
      primary = Color(0XFF28a9f1),
      background = Color.White,
      textHighEmphasis = Color(0XFF121212),
    )

    /**
     * Provides the default colors for the dark mode of the app.
     *
     * @return A [SnitcherColor] instance holding our color palette.
     */
    public fun defaultDarkColors(): SnitcherColor = SnitcherColor(
      primary = Color(0XFF28a9f1),
      background = Color.Black,
      textHighEmphasis = Color.White,
    )

    /**
     * Builds a palette out of ARGB values, such as `0xFF28A9F1`.
     *
     * [Color] is a Kotlin value class that Objective-C and Swift cannot construct, so this is the
     * entry point for them. Pass [UNSPECIFIED] to let a color follow the one it belongs to.
     *
     * @return A [SnitcherColor] instance holding the given colors.
     */
    public fun fromArgb(
      primary: Long,
      background: Long,
      textHighEmphasis: Long,
      onPrimary: Long = 0xFFFFFFFF,
      textLowEmphasis: Long = UNSPECIFIED,
      outline: Long = UNSPECIFIED,
    ): SnitcherColor = SnitcherColor(
      primary = Color(primary),
      background = Color(background),
      textHighEmphasis = Color(textHighEmphasis),
      onPrimary = Color(onPrimary),
      textLowEmphasis = textLowEmphasis.toColorOrUnspecified(),
      outline = outline.toColorOrUnspecified(),
    )

    /** Marks a color of [fromArgb] as unspecified, so that it follows the color it belongs to. */
    public const val UNSPECIFIED: Long = -1L

    private fun Long.toColorOrUnspecified(): Color =
      if (this == UNSPECIFIED) Color.Unspecified else Color(this)
  }
}

/**
 * Fills the colors that were left unspecified with the ones they follow, so that changing a single
 * color, such as `SnitcherColor.defaultColors().copy(primary = Color.Red)`, keeps the palette
 * consistent.
 */
internal fun SnitcherColor.resolveUnspecified(): SnitcherColor = copy(
  textLowEmphasis = textLowEmphasis.takeOrElse { textHighEmphasis.copy(alpha = 0.6f) },
  outline = outline.takeOrElse { primary },
)
