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

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SnitcherColorTest {

  @Test
  fun buildsTheSameColorsFromArgbValues() {
    val colors = SnitcherColor.fromArgb(
      primary = 0xFF28A9F1,
      background = 0xFFFFFFFF,
      textHighEmphasis = 0xFF121212,
    )

    assertEquals(Color(0xFF28A9F1), colors.primary)
    assertEquals(Color.White, colors.background)
    assertEquals(Color(0xFF121212), colors.textHighEmphasis)
    assertEquals(Color.White, colors.onPrimary)
  }

  @Test
  fun keepsUnspecifiedColorsUnspecified() {
    val colors = SnitcherColor.fromArgb(
      primary = 0xFF28A9F1,
      background = 0xFFFFFFFF,
      textHighEmphasis = 0xFF121212,
    )

    assertEquals(Color.Unspecified, colors.outline)
    assertEquals(Color.Unspecified, colors.textLowEmphasis)
  }

  @Test
  fun resolvesUnspecifiedColorsToTheOnesTheyFollow() {
    val colors = SnitcherColor.defaultColors().copy(primary = Color.Red).resolveUnspecified()

    assertEquals(Color.Red, colors.outline)
    assertEquals(colors.textHighEmphasis.copy(alpha = 0.6f), colors.textLowEmphasis)
  }
}
