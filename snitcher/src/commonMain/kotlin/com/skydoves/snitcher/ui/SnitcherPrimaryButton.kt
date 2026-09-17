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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skydoves.snitcher.ui.theme.SnitcherTheme

@Composable
internal fun SnitcherPrimaryButton(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  text: String = "",
  onClick: () -> Unit,
  enabled: Boolean = true,
  backgroundColor: Color? = null,
  contentColor: Color? = null,
  content: @Composable (RowScope.() -> Unit)? = null,
) {
  Button(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 54.dp),
    shape = SnitcherTheme.shapes.button,
    enabled = enabled,
    colors =
    ButtonDefaults.buttonColors(
      contentColor = contentColor ?: SnitcherTheme.colors.onPrimary,
      backgroundColor = backgroundColor ?: SnitcherTheme.colors.primary,
    ),
    onClick = onClick,
    content = content
      ?: {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
        ) {
          Icon(
            imageVector = icon,
            tint = contentColor ?: SnitcherTheme.colors.onPrimary,
            contentDescription = null,
          )

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = text,
            color = contentColor ?: SnitcherTheme.colors.onPrimary,
            style = SnitcherTheme.typography.button,
          )
        }
      },
  )
}
