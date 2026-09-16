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
package com.skydoves.snitcherdemo

import android.app.Application
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.install
import com.skydoves.snitcher.ui.theme.SnitcherColor
import com.skydoves.snitcher.ui.theme.SnitcherShapes
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import com.skydoves.snitcher.ui.theme.SnitcherTypography
import com.skydoves.snitcherdemo.ui.theme.Purple40
import com.skydoves.snitcherdemo.ui.theme.Purple80

class SnitcherDemoApp : Application() {

  override fun onCreate() {
    super.onCreate()

    Snitcher.install(
      application = this,
      theme = SnitcherThemeConfig(
        lightColors = SnitcherColor.defaultColors().copy(primary = Purple40),
        darkColors = SnitcherColor.defaultDarkColors().copy(primary = Purple80),
        typography = SnitcherTypography.defaultTypography().copy(
          title = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Black),
        ),
        shapes = SnitcherShapes(button = RoundedCornerShape(20.dp)),
      ),
      exceptionHandler = {
        // do something
      },
    )
  }
}
