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
@file:OptIn(ExperimentalComposeUiApi::class)

package com.skydoves.snitcherdemo.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.ui.AppRestoreScreen
import com.skydoves.snitcher.ui.ExceptionTraceScreen
import com.skydoves.snitcher.ui.theme.SnitcherColor
import com.skydoves.snitcher.ui.theme.SnitcherShapes
import com.skydoves.snitcher.ui.theme.SnitcherTheme
import com.skydoves.snitcher.ui.theme.SnitcherThemeConfig
import com.skydoves.snitcher.ui.theme.SnitcherTypography
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/** Renders the pre-built screens off screen and writes them as PNG files for the documentation. */
public fun main(args: Array<String>) {
  val outputDirectory = File(args.firstOrNull() ?: "art")
  outputDirectory.mkdirs()

  Snitcher.theme = SnitcherThemeConfig(
    lightColors = SnitcherColor.defaultColors().copy(primary = Color(0xFF6650a4)),
    darkColors = SnitcherColor.defaultDarkColors().copy(primary = Color(0xFFD0BCFF)),
    typography = SnitcherTypography.defaultTypography().copy(
      title = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Black),
    ),
    shapes = SnitcherShapes(button = RoundedCornerShape(20.dp)),
  )
  Snitcher.platformInfo = "1.0.3 (4) macOS 26.5 (JVM 21)"

  render(File(outputDirectory, "desktop_trace.png"), darkTheme = false) {
    ExceptionTraceScreen(
      snitcherException = sampleException,
      onRestore = {},
      onDebug = {},
    )
  }

  render(File(outputDirectory, "desktop_trace_dark.png"), darkTheme = true) {
    ExceptionTraceScreen(
      snitcherException = sampleException,
      onRestore = {},
      onDebug = {},
    )
  }

  render(File(outputDirectory, "desktop_restore.png"), darkTheme = false) {
    AppRestoreScreen(onRestore = {})
  }
}

private fun render(output: File, darkTheme: Boolean, content: @Composable () -> Unit) {
  val scene = ImageComposeScene(
    width = WIDTH,
    height = HEIGHT,
    density = Density(2f),
  ) {
    SnitcherTheme(darkTheme = darkTheme) {
      Box(modifier = Modifier.fillMaxSize()) {
        content()
      }
    }
  }

  try {
    val image = scene.render()
    val data = image.encodeToData(EncodedImageFormat.PNG)
      ?: error("failed to encode ${output.name}")
    output.writeBytes(data.bytes)
    println("wrote ${output.absolutePath}")
  } finally {
    scene.close()
  }
}

private const val WIDTH = 1200
private const val HEIGHT = 1100

private val sampleException = SnitcherException(
  threadId = 1,
  threadName = "main",
  packageName = "RuntimeException",
  message = "This is an intended runtime exception.",
  stackTrace = """
    java.lang.RuntimeException: This is an intended runtime exception.
        at com.skydoves.snitcherdemo.desktop.MainKt.DemoContent(Main.kt:74)
        at androidx.compose.foundation.ClickableNode.handleUpEvent(Clickable.kt:950)
        at androidx.compose.ui.input.pointer.Node.dispatchMainEventPass(HitPathTracker.kt:445)
        at androidx.compose.ui.platform.ComposeSceneMediator.processPointerInput(Mediator.kt:381)
        at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:92)
  """.trimIndent(),
)
