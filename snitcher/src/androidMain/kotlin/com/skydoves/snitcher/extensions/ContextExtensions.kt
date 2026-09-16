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
package com.skydoves.snitcher.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import com.skydoves.snitcher.Snitcher

@JvmSynthetic
internal fun Context.findActivity(): Activity? {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context
    context = context.baseContext
  }
  return null
}

@JvmSynthetic
internal fun Context.packageInfo(): PackageInfo? = try {
  packageManager.getPackageInfo(packageName, 0)
} catch (e: Exception) {
  null
}

@JvmSynthetic
internal fun PackageInfo?.versionCode(): Long? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    this?.longVersionCode
  } else {
    @Suppress("DEPRECATION")
    this?.versionCode?.toLong()
  }

/** Restarts the application from the given [launcher] activity and finishes the current one. */
@JvmSynthetic
internal fun Context.restoreApp(launcher: String) {
  if (launcher.isEmpty()) {
    return
  }

  // the crash was handled, so it must not be displayed again on the next launch.
  Snitcher.clear()

  startActivity(
    Intent().setClassName(this, launcher).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    },
  )

  findActivity()?.finish()
}
