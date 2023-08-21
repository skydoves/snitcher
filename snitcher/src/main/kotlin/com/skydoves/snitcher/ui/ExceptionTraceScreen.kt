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

package com.skydoves.snitcher.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.snitcher.R
import com.skydoves.snitcher.extensions.findActivity
import com.skydoves.snitcher.extensions.toast
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.ui.theme.SnitcherStatusBarColor
import com.skydoves.snitcher.ui.theme.SnitcherTheme

@Composable
public fun ExceptionTraceScreen(
  launcher: String,
  snitcherException: SnitcherException,
) {
  SnitcherStatusBarColor()

  ExceptionTraceScreenContent(
    packageName = launcher,
    snitcherException = snitcherException,
  )
}

@Composable
private fun ExceptionTraceScreenContent(
  packageName: String,
  snitcherException: SnitcherException,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .background(SnitcherTheme.colors.background)
      .verticalScroll(scrollState)
      .padding(16.dp)
      .testTag("exception_trace_screen"),
  ) {
    val title = remember(snitcherException) {
      snitcherException.stackTrace.split(":").firstOrNull()?.split(".")?.last().orEmpty()
    }

    Text(
      text = title,
      color = SnitcherTheme.colors.primary,
      fontWeight = FontWeight.Bold,
      fontSize = 34.sp,
    )

    Text(
      modifier = Modifier.padding(vertical = 6.dp),
      text = snitcherException.message,
      color = SnitcherTheme.colors.textHighEmphasis,
      fontSize = 18.sp,
    )

    SnitcherPrimaryButton(
      modifier = Modifier.padding(vertical = 16.dp),
      icon = Icons.Filled.RestartAlt,
      text = stringResource(id = R.string.snitcher_debug_crash_screen_restore),
      onClick = {
        context.startActivity(
          Intent().setClassName(context, packageName).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
          },
        )

        val activity = context.findActivity()
        activity?.finish()
      },
    )

    SnitcherPrimaryButton(
      modifier = Modifier.padding(bottom = 16.dp),
      icon = Icons.Filled.CenterFocusStrong,
      text = stringResource(id = R.string.snitcher_debug_crash_screen_debug_on_ide),
      onClick = { throw snitcherException.throwable },
    )

    Box(modifier = Modifier.fillMaxWidth()) {
      Text(
        modifier = Modifier.align(Alignment.CenterStart),
        text = stringResource(id = R.string.snitcher_debug_crash_screen_stacktrace),
        color = SnitcherTheme.colors.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
      )

      val clipboardManager: ClipboardManager = LocalClipboardManager.current
      Icon(
        modifier =
        Modifier
          .align(Alignment.CenterEnd)
          .clickable {
            clipboardManager.setText(AnnotatedString(snitcherException.message))
            context.toast("copied!")
          },
        imageVector = Icons.Filled.ContentCopy,
        tint = SnitcherTheme.colors.textHighEmphasis,
        contentDescription = "copy",
      )
    }

    Spacer(modifier = Modifier.height(22.dp))

    Text(
      modifier =
      Modifier
        .border(
          border = BorderStroke(2.dp, SnitcherTheme.colors.primary),
          shape = RoundedCornerShape(6.dp),
        )
        .padding(12.dp),
      text = snitcherException.stackTrace,
      color = SnitcherTheme.colors.textHighEmphasis,
      fontSize = 14.sp,
    )
  }
}
