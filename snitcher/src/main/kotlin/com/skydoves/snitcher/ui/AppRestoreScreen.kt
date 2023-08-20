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

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.snitcher.R
import com.skydoves.snitcher.extensions.findActivity
import com.skydoves.snitcher.ui.theme.SnitcherStatusBarColor
import com.skydoves.snitcher.ui.theme.SnitcherTheme

@Composable
public fun AppRestoreScreen(
  launcher: String,
) {
  SnitcherStatusBarColor()

  AppRestoreScreenContent(packageName = launcher)
}

@Composable
private fun AppRestoreScreenContent(
  packageName: String,
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SnitcherTheme.colors.background)
      .padding(32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(id = R.string.snitcher_release_crash_screen_title),
      color = SnitcherTheme.colors.textHighEmphasis,
      fontWeight = FontWeight.Bold,
      fontSize = 34.sp,
    )

    Text(
      modifier = Modifier.padding(vertical = 18.dp),
      text = stringResource(id = R.string.snitcher_release_crash_screen_description),
      color = SnitcherTheme.colors.textHighEmphasis,
      fontSize = 18.sp,
    )

    SnitcherPrimaryButton(
      modifier = Modifier.padding(vertical = 48.dp),
      icon = Icons.Filled.RestartAlt,
      text = stringResource(id = R.string.snitcher_release_crash_screen_restore),
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
  }
}
