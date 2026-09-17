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

/** The name of the file that holds the last captured crash. */
internal const val SNITCHER_STORE_FILE_NAME: String = "snitcher.json"

/**
 * A tiny synchronous store for the last captured crash.
 *
 * Every read and write is blocking on purpose. A crash is written while the process is being torn
 * down, where a suspending write is not guaranteed to reach the disk, and the payload is a single
 * small document.
 */
internal class SnitcherStore(private val fileSystem: FileSystem, private val path: Path) {

  fun read(): SnitcherPreference? {
    if (!fileSystem.exists(path)) {
      return null
    }

    return runCatching {
      val content = fileSystem.read(path) { readUtf8() }
      if (content.isBlank()) {
        null
      } else {
        json.decodeFromString(SnitcherPreference.serializer(), content)
      }
    }.getOrElse {
      // a file that cannot be decoded is a leftover of a crash that could not be written whole,
      // and it would fail every launch from now on, so it goes.
      clear()
      null
    }
  }

  /**
   * Writes the crash and returns whether it reached the disk.
   *
   * The payload goes to a temporary file first and is then moved onto [path], so a reader never
   * meets a half written file, even when the process is killed in the middle of a crash.
   */
  fun write(preference: SnitcherPreference): Boolean = runCatching {
    val directory = path.parent
    if (directory != null) {
      fileSystem.createDirectories(directory)
    }

    val temporaryPath = path.parent?.resolve("$TEMPORARY_PREFIX${path.name}") ?: path
    fileSystem.write(temporaryPath) {
      writeUtf8(json.encodeToString(SnitcherPreference.serializer(), preference))
    }
    if (temporaryPath != path) {
      fileSystem.atomicMove(temporaryPath, path)
    }
    true
  }.getOrElse { false }

  fun clear() {
    runCatching { fileSystem.delete(path, mustExist = false) }
  }

  private companion object {
    private const val TEMPORARY_PREFIX = "~"
    private val json = Json { ignoreUnknownKeys = true }
  }
}
