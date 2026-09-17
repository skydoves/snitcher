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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id(libs.plugins.compose.multiplatform.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
  }
}

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
  targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
}

compose.desktop {
  application {
    mainClass = "com.skydoves.snitcherdemo.desktop.MainKt"
  }
}

dependencies {
  implementation(project(":snitcher"))
  implementation(compose.desktop.currentOs)
}

/** Renders the pre-built screens into `art/` for the documentation. */
tasks.register<JavaExec>("generateScreenshots") {
  group = "documentation"
  description = "Renders the desktop Snitcher screens off screen into the art directory."
  mainClass.set("com.skydoves.snitcherdemo.desktop.ScreenshotsKt")
  classpath = sourceSets.getByName("main").runtimeClasspath
  args("${rootDir}/art")
}
