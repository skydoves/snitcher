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
import platform.Foundation.NSThread

internal actual fun currentThreadName(): String {
  val thread = NSThread.currentThread
  val name = thread.name
  return when {
    !name.isNullOrEmpty() -> name
    thread.isMainThread -> "main"
    else -> "unknown"
  }
}

/** Kotlin/Native does not expose a thread id, so the captured crash carries zero. */
internal actual fun currentThreadId(): Long = 0L

/**
 * Kotlin/Native reports a stack trace as an array of strings rather than as structured frames, so
 * the whole trace is kept in [SnitcherException.stackTrace] and this list stays empty.
 */
internal actual fun Throwable.snitcherStackTraceElements(): List<SnitcherStackTraceElement> =
  emptyList()

/** There are no structured frames to restore a throwable from on Kotlin/Native. */
internal actual fun SnitcherException.restoreThrowable(): Throwable? = null
