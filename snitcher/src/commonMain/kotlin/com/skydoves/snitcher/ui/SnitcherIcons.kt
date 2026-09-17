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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The icons of the pre-built screens, drawn from the path data of the Material icons, which are
 * published under the Apache 2.0 license. They are declared here because the extended Material
 * icons are not published for every Snitcher target.
 */
internal object SnitcherIcons {

  val Restore: ImageVector by lazy {
    materialIcon(
      name = "Restore",
      pathData = "M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 " +
        "0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 " +
        "3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z",
    )
  }

  val Debug: ImageVector by lazy {
    materialIcon(
      name = "Debug",
      pathData = "M12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm-7 7H3v4c0 1.1.9 2 " +
        "2 2h4v-2H5v-4zM5 5h4V3H5c-1.1 0-2 .9-2 2v4h2V5zm14-2h-4v2h4v4h2V5c0-1.1-.9-2-2-2zm0 " +
        "16h-4v2h4c1.1 0 2-.9 2-2v-4h-2v4z",
    )
  }

  val Copy: ImageVector by lazy {
    materialIcon(
      name = "Copy",
      pathData =
      "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 " +
        "0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z",
    )
  }

  private fun materialIcon(name: String, pathData: String): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
  ).addPath(
    pathData = PathParser().parsePathString(pathData).toNodes(),
    fill = SolidColor(Color.Black),
  ).build()
}
