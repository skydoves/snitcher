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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skydoves.snitcher.ui.ExceptionTraceScreen

/**
 * A collection of text styles, which are contained by [SnitcherTheme] to implement
 * [ExceptionTraceScreen].
 *
 * @property title The style of the screen titles, such as the exception name.
 * @property message The style of the exception message and the restore screen description.
 * @property deviceInfo The style of the package and device information.
 * @property sectionLabel The style of the section labels, such as the stack trace label.
 * @property stacktrace The style of the stack trace body.
 * @property button The style of the button labels.
 */
@Immutable
public data class SnitcherTypography(
  val title: TextStyle,
  val message: TextStyle,
  val deviceInfo: TextStyle,
  val sectionLabel: TextStyle,
  val stacktrace: TextStyle,
  val button: TextStyle,
) {
  public companion object {
    /**
     * Provides the default text styles of the Snitcher screens.
     *
     * @return A [SnitcherTypography] instance holding our text styles.
     */
    public fun defaultTypography(): SnitcherTypography = SnitcherTypography(
      title = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
      message = TextStyle(fontSize = 18.sp),
      deviceInfo = TextStyle(fontSize = 18.sp),
      sectionLabel = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
      stacktrace = TextStyle(fontSize = 14.sp),
      button = TextStyle(fontSize = 16.sp),
    )
  }
}
