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
package com.skydoves.snitcher.internal

import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.model.SnitcherStackTraceElement

/** The name of the thread that is running the current code. */
internal expect fun currentThreadName(): String

/** The id of the thread that is running the current code, or `0` where there is none. */
internal expect fun currentThreadId(): Long

/** The structured stack trace frames, which are only available on the JVM. */
internal expect fun Throwable.snitcherStackTraceElements(): List<SnitcherStackTraceElement>

/**
 * Rebuilds a [Throwable] out of a captured exception so that it can be thrown again for a debugger,
 * or `null` where the platform cannot restore one.
 */
internal expect fun SnitcherException.restoreThrowable(): Throwable?

/** Converts a [Throwable] into the serializable model that Snitcher stores and renders. */
internal fun Throwable.toSnitcherException(): SnitcherException = SnitcherException(
  threadId = currentThreadId(),
  threadName = currentThreadName(),
  packageName = this::class.simpleName.orEmpty(),
  message = message.orEmpty(),
  stackTrace = stackTraceToString(),
  stackTraceElement = snitcherStackTraceElements(),
)
