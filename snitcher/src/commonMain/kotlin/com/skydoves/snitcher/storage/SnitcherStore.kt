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

import com.skydoves.snitcher.model.SnitcherPreference
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path

/**
 * A tiny synchronous store for the last captured crash.
 *
 * Every read and write is blocking on purpose. A crash is written while the process is being torn
 * down, where a suspending write is not guaranteed to reach the disk, and the payload is a single
 * small document.
 */
internal class SnitcherStore(private val fileSystem: FileSystem, private val path: Path) {

  fun read(): SnitcherPreference? = runCatching {
    if (!fileSystem.exists(path)) {
      return null
    }
    val content = fileSystem.read(path) { readUtf8() }
    if (content.isBlank()) null else json.decodeFromString(SnitcherPreference.serializer(), content)
  }.getOrNull()

  fun write(preference: SnitcherPreference) {
    runCatching {
      path.parent?.let { fileSystem.createDirectories(it) }
      fileSystem.write(path) {
        writeUtf8(json.encodeToString(SnitcherPreference.serializer(), preference))
      }
    }
  }

  fun clear() {
    runCatching { fileSystem.delete(path, mustExist = false) }
  }

  private companion object {
    private val json = Json { ignoreUnknownKeys = true }
  }
}
