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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.ui.theme.SnitcherSystemBars
import com.skydoves.snitcher.ui.theme.SnitcherTheme

/**
 * Displays a friendly recovery screen, which hides the stack trace and is meant for release builds.
 *
 * @param modifier The modifier of the screen.
 * @param onRestore Restores the application. The button is hidden when it is null.
 */
@Composable
public fun AppRestoreScreen(modifier: Modifier = Modifier, onRestore: (() -> Unit)? = null) {
  SnitcherSystemBars()

  val strings = Snitcher.strings

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SnitcherTheme.colors.background)
      .systemBarsPadding()
      .padding(32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = strings.restoreTitle,
      color = SnitcherTheme.colors.textHighEmphasis,
      style = SnitcherTheme.typography.title,
    )

    Text(
      modifier = Modifier.padding(vertical = 18.dp),
      text = strings.restoreDescription,
      color = SnitcherTheme.colors.textHighEmphasis,
      style = SnitcherTheme.typography.message,
    )

    if (onRestore != null) {
      SnitcherPrimaryButton(
        modifier = Modifier.padding(vertical = 48.dp),
        icon = SnitcherIcons.Restore,
        text = strings.restoreButton,
        onClick = onRestore,
      )
    }
  }
}
