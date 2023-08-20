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

import kotlinx.serialization.Serializable

@Serializable
public data class SnitcherStackTraceElement(
  public val fileName: String,
  public val lineNumber: Int,
  public val className: String,
  public val methodName: String,
)

@JvmSynthetic
internal fun StackTraceElement.toSnitcherElement(): SnitcherStackTraceElement {
  return SnitcherStackTraceElement(
    fileName = "",
    lineNumber = lineNumber,
    className = className,
    methodName = methodName,
  )
}

@JvmSynthetic
internal fun SnitcherStackTraceElement.toStackTraceElement(): StackTraceElement {
  return StackTraceElement(className, methodName, fileName, lineNumber)
}
