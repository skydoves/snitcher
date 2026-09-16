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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.skydoves.snitcher.extensions.restoreApp
import com.skydoves.snitcher.internal.restoreThrowable
import com.skydoves.snitcher.model.SnitcherException

/**
 * Displays the captured [snitcherException] with the actions that Android supports, which are
 * restoring the application from the [launcher] activity and throwing the exception again for a
 * debugger.
 *
 * @param launcher The class name of the activity that restores the application.
 * @param snitcherException The captured crash to render.
 * @param modifier The modifier of the screen.
 */
@Composable
public fun ExceptionTraceScreen(
  launcher: String,
  snitcherException: SnitcherException,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  ExceptionTraceScreen(
    snitcherException = snitcherException,
    modifier = modifier,
    onRestore = { context.restoreApp(launcher) },
    onDebug = { snitcherException.restoreThrowable()?.let { throw it } },
  )
}

/**
 * Displays the recovery screen, which restores the application from the [launcher] activity.
 *
 * @param launcher The class name of the activity that restores the application.
 * @param modifier The modifier of the screen.
 */
@Composable
public fun AppRestoreScreen(launcher: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current

  AppRestoreScreen(
    modifier = modifier,
    onRestore = { context.restoreApp(launcher) },
  )
}
