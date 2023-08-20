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
import java.io.Serializable

/**
 * Crash information, captured and provided by [Snitcher],
 * encompasses essential data about exceptions, including detailed stack traces.
 */
@kotlinx.serialization.Serializable
public data class SnitcherException(
  public val threadId: Long,
  public val threadName: String,
  public val packageName: String,
  public val message: String,
  public val stackTrace: String,
  public val stackTraceElement: List<SnitcherStackTraceElement>,
) : Serializable {

  /** Creates a [Throwable] from the given [SnitcherException]. */
  public val throwable: Throwable
    get() = Throwable(message).apply {
      stackTrace = stackTraceElement.map { it.toStackTraceElement() }.toTypedArray()
    }
}
