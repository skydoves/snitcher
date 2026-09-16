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
package com.skydoves.snitcher.model

import com.skydoves.snitcher.Snitcher
import kotlinx.serialization.Serializable

/**
 * Crash information, captured and provided by [Snitcher],
 * encompasses essential data about exceptions, including detailed stack traces.
 *
 * @property threadId The id of the thread that threw the exception, or `0` where the platform does
 * not expose one.
 * @property threadName The name of the thread that threw the exception.
 * @property packageName The simple name of the thrown exception class.
 * @property message The message of the thrown exception.
 * @property stackTrace The whole stack trace, rendered as a string.
 * @property stackTraceElement The stack trace frames. Kotlin/Native does not expose structured
 * frames, so this list is empty on iOS.
 */
@Serializable
public data class SnitcherException(
  public val threadId: Long,
  public val threadName: String,
  public val packageName: String,
  public val message: String,
  public val stackTrace: String,
  public val stackTraceElement: List<SnitcherStackTraceElement> = emptyList(),
)
