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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ThrowableExtensionsTest {

  @Test
  fun capturesTheExceptionNameMessageAndTrace() {
    val throwable = IllegalStateException("something went wrong")

    val exception = throwable.toSnitcherException()

    assertEquals("IllegalStateException", exception.packageName)
    assertEquals("something went wrong", exception.message)
    assertTrue(exception.stackTrace.contains("something went wrong"))
    assertTrue(exception.threadName.isNotEmpty())
  }

  @Test
  fun capturesAnEmptyMessageWhenThereIsNone() {
    val exception = RuntimeException().toSnitcherException()

    assertEquals("", exception.message)
    assertEquals("RuntimeException", exception.packageName)
  }
}
