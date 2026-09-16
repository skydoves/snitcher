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

/**
 * Rebuilds a [Throwable] out of the captured exception, including the stack trace frames, so that
 * it can be reported to a crash reporter or thrown again for a debugger.
 *
 * Kotlin/Native does not expose structured stack trace frames, so this is only available on Android
 * and on the desktop.
 */
public fun SnitcherException.toThrowable(): Throwable = Throwable(message).apply {
  stackTrace = stackTraceElement.map {
    StackTraceElement(it.className, it.methodName, it.fileName, it.lineNumber)
  }.toTypedArray()
}
