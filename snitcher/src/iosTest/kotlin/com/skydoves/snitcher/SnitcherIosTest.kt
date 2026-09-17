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
@file:OptIn(ExperimentalNativeApi::class)

package com.skydoves.snitcher

import com.skydoves.snitcher.model.SnitcherException
import platform.Foundation.NSException
import platform.Foundation.NSTemporaryDirectory
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.getUnhandledExceptionHook
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Covers the iOS installation, which is the only way an iOS application can record a crash before
 * the runtime terminates the process.
 *
 * The hook itself is not invoked here on purpose. It ends in `terminateWithUnhandledException`,
 * exactly as the runtime would without Snitcher, so running it would kill the test binary.
 */
internal class SnitcherIosTest {

  private var handled: SnitcherException? = null

  @BeforeTest
  fun setUp() {
    handled = null
    Snitcher.install(
      storageDirectory = NSTemporaryDirectory() + "snitcher-test-" + Random.nextLong(),
      platformInfo = "test",
      catchObjectiveCExceptions = false,
      exceptionHandler = { handled = it },
    )
  }

  @AfterTest
  fun tearDown() {
    Snitcher.clear()
  }

  @Test
  fun installsTheUnhandledExceptionHook() {
    assertNotNull(getUnhandledExceptionHook())
  }

  @Test
  fun installingTwiceKeepsTheSameHook() {
    val hook = assertNotNull(getUnhandledExceptionHook())

    Snitcher.install(
      storageDirectory = NSTemporaryDirectory() + "snitcher-test-" + Random.nextLong(),
      catchObjectiveCExceptions = false,
    )

    // chaining Snitcher into itself would recurse until the stack dies.
    assertSame(hook, getUnhandledExceptionHook())
  }

  @Test
  fun capturesAKotlinException() {
    Snitcher.capture(IllegalStateException("captured by the hook"), null)

    val exception = assertNotNull(Snitcher.exception.value)
    assertEquals("IllegalStateException", exception.packageName)
    assertEquals("captured by the hook", exception.message)
    assertTrue(exception.stackTrace.contains("captured by the hook"))
  }

  @Test
  fun runsTheCustomExceptionHandler() {
    Snitcher.capture(RuntimeException("handled"), null)

    assertEquals("handled", assertNotNull(handled).message)
  }

  @Test
  fun keepsTheCrashForTheNextLaunch() {
    Snitcher.capture(RuntimeException("persisted"), null)

    // a new installation reads the crash back, which is what happens on the launch after a crash.
    val storedException = assertNotNull(Snitcher.store?.read()?.snitcherException)
    assertEquals("persisted", storedException.message)
  }

  @Test
  fun capturesAnObjectiveCException() {
    val exception = NSException.exceptionWithName(
      name = "RuntimeException",
      reason = "raised by Objective-C",
      userInfo = null,
    ).toSnitcherException()

    assertEquals("RuntimeException", exception.packageName)
    assertEquals("raised by Objective-C", exception.message)
    assertTrue(exception.stackTrace.contains("RuntimeException"))
  }
}
