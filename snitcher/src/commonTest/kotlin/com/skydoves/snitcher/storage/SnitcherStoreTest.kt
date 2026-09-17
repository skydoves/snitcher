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
package com.skydoves.snitcher.storage

import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.model.SnitcherPreference
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class SnitcherStoreTest {

  private val path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY /
    "snitcher-test-${Random.nextLong()}" / "crash.json"
  private val store = SnitcherStore(FileSystem.SYSTEM, path)

  @AfterTest
  fun tearDown() {
    store.clear()
  }

  @Test
  fun readsBackWhatItWrote() {
    val preference = SnitcherPreference(
      snitcherException = exception,
      launcher = "com.skydoves.snitcherdemo.MainActivity",
    )

    store.write(preference)

    assertEquals(preference, store.read())
  }

  @Test
  fun readsNullWhenNothingWasWritten() {
    assertNull(store.read())
  }

  @Test
  fun readsNullAndForgetsACorruptFile() {
    store.write(SnitcherPreference(snitcherException = exception, launcher = null))
    path.parent?.let { FileSystem.SYSTEM.createDirectories(it) }
    FileSystem.SYSTEM.write(path) { writeUtf8("{ this is not a crash }") }

    assertNull(store.read())
    // a file that cannot be decoded would fail every launch, so reading it once removes it.
    assertFalse(FileSystem.SYSTEM.exists(path))
  }

  @Test
  fun reportsWhetherTheCrashReachedTheDisk() {
    assertTrue(store.write(SnitcherPreference(snitcherException = exception, launcher = null)))
  }

  @Test
  fun readsNullAfterBeingCleared() {
    store.write(SnitcherPreference(snitcherException = exception, launcher = null))
    store.clear()

    assertNull(store.read())
  }

  private val exception = SnitcherException(
    threadId = 1L,
    threadName = "main",
    packageName = "RuntimeException",
    message = "This is an intended runtime exception.",
    stackTrace = "java.lang.RuntimeException: This is an intended runtime exception.",
  )
}
