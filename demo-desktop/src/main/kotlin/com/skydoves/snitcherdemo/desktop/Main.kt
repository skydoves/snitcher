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
package com.skydoves.snitcherdemo.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.install
import com.skydoves.snitcher.ui.SnitcherTraceWindow
import com.skydoves.snitcher.ui.theme.SnitcherColor
import com.skydoves.snitcher.ui.theme.SnitcherShapes
import com.skydoves.snitcher.ui.theme.SnitcherTheme
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import com.skydoves.snitcher.ui.theme.SnitcherTypography

private val Purple = Color(0xFF6650a4)

public fun main() {
  Snitcher.install(
    theme = SnitcherThemeConfig(
      lightColors = SnitcherColor.defaultColors().copy(primary = Purple),
      darkColors = SnitcherColor.defaultDarkColors().copy(primary = Color(0xFFD0BCFF)),
      typography = SnitcherTypography.defaultTypography().copy(
        title = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Black),
      ),
      shapes = SnitcherShapes(button = RoundedCornerShape(20.dp)),
    ),
    exceptionHandler = {
      // do something
    },
  )

  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "Snitcher Desktop Demo",
    ) {
      DemoContent()
    }

    SnitcherTraceWindow()
  }
}

@Composable
private fun DemoContent() {
  SnitcherTheme {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SnitcherTheme.colors.background),
    ) {
      Button(
        modifier = Modifier.align(Alignment.Center),
        onClick = { throw RuntimeException("This is an intended runtime exception.") },
      ) {
        Text(text = "Throw an exception")
      }
    }
  }
}
