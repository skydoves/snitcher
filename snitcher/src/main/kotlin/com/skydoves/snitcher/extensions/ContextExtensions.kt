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
import android.content.pm.PackageInfo
import android.os.Build
import android.widget.Toast

@JvmSynthetic
internal fun Context.toast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

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
internal fun Context.packageInfo(): PackageInfo? {
  return try {
    packageManager.getPackageInfo(packageName, 0)
  } catch (e: Exception) {
    null
  }
}

@JvmSynthetic
internal fun PackageInfo?.versionCode(): Long? {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    this?.longVersionCode
  } else {
    this?.versionCode?.toLong()
  }
}
