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
package com.skydoves.snitcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.skydoves.snitcher.Snitcher
import com.skydoves.snitcher.model.SnitcherException
import com.skydoves.snitcher.ui.theme.SnitcherTheme

/**
 * This Activity will be launched when you encounter encounter a crash in your app.
 *
 * You have the flexibility to override this Activity, or alternatively,
 * you can initiate your custom activity by providing the launcher parameter when invoking the
 * `Snitcher.install` method.
 */
public open class ExceptionTraceActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val exception: SnitcherException? by Snitcher.exception.collectAsState()
      val launcher: String by Snitcher.launcher.collectAsState()

      SnitcherTheme {
        if (exception != null) {
          if (Snitcher.isDebuggable) {
            ExceptionTraceScreen(
              launcher = launcher,
              snitcherException = exception!!,
            )
          } else {
            AppRestoreScreen(launcher = launcher)
          }
        }
      }
    }
  }
}
