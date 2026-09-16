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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.ui.theme.SnitcherSystemBars
import com.skydoves.snitcher.ui.theme.SnitcherTheme
import kotlinx.coroutines.delay

/**
 * Displays the captured [snitcherException] with the stack trace, and the actions that the host
 * platform supports.
 *
 * @param snitcherException The captured crash to render.
 * @param modifier The modifier of the screen.
 * @param onRestore Restores the application. The button is hidden when it is null.
 * @param onDebug Throws the exception again so that a debugger catches it. The button is hidden
 * when it is null, which is the case on platforms that cannot restore a throwable.
 */
@Composable
public fun ExceptionTraceScreen(
  snitcherException: SnitcherException,
  modifier: Modifier = Modifier,
  onRestore: (() -> Unit)? = null,
  onDebug: (() -> Unit)? = null,
) {
  SnitcherSystemBars()

  val clipboardManager = LocalClipboardManager.current
  val scrollState = rememberScrollState()
  val strings = Snitcher.strings
  var copied by remember { mutableStateOf(false) }

  LaunchedEffect(copied) {
    if (copied) {
      delay(COPIED_MESSAGE_DURATION)
      copied = false
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SnitcherTheme.colors.background)
      .systemBarsPadding()
      .verticalScroll(scrollState)
      .padding(16.dp)
      .testTag("exception_trace_screen"),
  ) {
    val title = remember(snitcherException) { snitcherException.title() }

    Text(
      text = title,
      color = SnitcherTheme.colors.primary,
      style = SnitcherTheme.typography.title,
    )

    Text(
      modifier = Modifier.padding(vertical = 6.dp),
      text = snitcherException.message,
      color = SnitcherTheme.colors.textHighEmphasis,
      style = SnitcherTheme.typography.message,
    )

    val platformInfo = Snitcher.platformInfo
    if (platformInfo.isNotEmpty()) {
      Text(
        modifier = Modifier.padding(vertical = 6.dp),
        text = platformInfo,
        color = SnitcherTheme.colors.textLowEmphasis,
        style = SnitcherTheme.typography.deviceInfo,
      )
    }

    if (onRestore != null) {
      SnitcherPrimaryButton(
        modifier = Modifier.padding(vertical = 16.dp),
        icon = SnitcherIcons.Restore,
        text = strings.traceRestoreButton,
        onClick = onRestore,
      )
    }

    if (onDebug != null) {
      SnitcherPrimaryButton(
        modifier = Modifier.padding(bottom = 16.dp),
        icon = SnitcherIcons.Debug,
        text = strings.traceDebugButton,
        onClick = onDebug,
      )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
      Text(
        modifier = Modifier.align(Alignment.CenterStart),
        text = strings.traceStacktrace,
        color = SnitcherTheme.colors.primary,
        style = SnitcherTheme.typography.sectionLabel,
      )

      Row(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .clickable {
            clipboardManager.setText(AnnotatedString(snitcherException.stackTrace))
            copied = true
          },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
      ) {
        if (copied) {
          Text(
            text = strings.traceCopied,
            color = SnitcherTheme.colors.textLowEmphasis,
            style = SnitcherTheme.typography.sectionLabel,
          )

          Spacer(modifier = Modifier.width(6.dp))
        }

        Icon(
          imageVector = SnitcherIcons.Copy,
          tint = SnitcherTheme.colors.textHighEmphasis,
          contentDescription = "copy",
        )
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    Text(
      modifier = Modifier
        .border(
          border = BorderStroke(2.dp, SnitcherTheme.colors.outline),
          shape = SnitcherTheme.shapes.stacktrace,
        )
        .padding(12.dp),
      text = snitcherException.stackTrace,
      color = SnitcherTheme.colors.textHighEmphasis,
      style = SnitcherTheme.typography.stacktrace,
    )
  }
}

private const val COPIED_MESSAGE_DURATION = 2000L

private fun SnitcherException.title(): String = packageName.ifEmpty {
  stackTrace.split(":").firstOrNull()?.split(".")?.last().orEmpty()
}
