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
@file:OptIn(ExperimentalSerializationApi::class)

package com.skydoves.snitcher.datastore

import androidx.datastore.core.Serializer
import com.skydoves.snitcher.model.SnitcherPreference
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

internal class SnitcherSerializer : Serializer<SnitcherPreference?> {

  override val defaultValue: SnitcherPreference? = null

  override suspend fun readFrom(input: InputStream): SnitcherPreference? {
    val snitcherInput = input.readBytes()
    if (snitcherInput.isEmpty()) return null
    return ProtoBuf.decodeFromByteArray(SnitcherPreference.serializer(), snitcherInput)
  }

  override suspend fun writeTo(t: SnitcherPreference?, output: OutputStream) {
    val snitcherException = t ?: return
    val byteArray = ProtoBuf.encodeToByteArray(SnitcherPreference.serializer(), snitcherException)

    @Suppress("BlockingMethodInNonBlockingContext")
    output.write(byteArray)
  }
}
