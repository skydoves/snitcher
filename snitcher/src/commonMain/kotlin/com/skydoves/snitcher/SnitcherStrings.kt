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
package com.skydoves.snitcher

import androidx.compose.runtime.Immutable

/**
 * The texts that are displayed by the pre-built Snitcher screens. Give an instance of this class to
 * the `strings` parameter of the platform installers, or assign [Snitcher.strings] directly, to
 * translate or reword them.
 *
 * @property restoreTitle The title of the restore screen, which is shown on a release build.
 * @property restoreDescription The description of the restore screen.
 * @property restoreButton The label of the button that restores the app from the restore screen.
 * @property traceRestoreButton The label of the button that restores the app from the trace screen.
 * @property traceDebugButton The label of the button that throws the exception again for a debugger.
 * @property traceStacktrace The label of the stack trace section.
 * @property traceCopied The message that confirms the stack trace was copied.
 */
@Immutable
public data class SnitcherStrings(
  val restoreTitle: String = "Oops, Restore the previous screen?",
  val restoreDescription: String =
    "The app crashed unexpectedly. We apologize for the inconvenience. " +
      "Would you like to return to where you left off?",
  val restoreButton: String = "Restore",
  val traceRestoreButton: String = "Restore App",
  val traceDebugButton: String = "Debug on IDE",
  val traceStacktrace: String = "Stacktrace",
  val traceCopied: String = "Copied!",
) {
  public companion object {
    /**
     * Provides the default texts, which is also the entry point for Swift, where Kotlin default
     * arguments are not available.
     *
     * @return A [SnitcherStrings] instance holding our texts.
     */
    public fun defaultStrings(): SnitcherStrings = SnitcherStrings()
  }
}
