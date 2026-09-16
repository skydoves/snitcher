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

import com.skydoves.snitcher.Configuration
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
  id(libs.plugins.compose.multiplatform.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.nexus.plugin.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
  id(libs.plugins.dokka.get().pluginId)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
  configure(KotlinMultiplatform(javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml")))

  val artifactId = "snitcher"
  coordinates(
    Configuration.artifactGroup,
    artifactId,
    rootProject.extra.get("libVersion").toString()
  )

  pom {
    name.set(artifactId)
    description.set("Snitcher captures global crashes, enabling easy redirection to the exception tracing screen for swift recovery.")
  }
}

kotlin {
  explicitApi()

  @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
  abiValidation {
    referenceDumpDir.set(layout.projectDirectory.dir("api"))
  }

  android {
    namespace = "com.skydoves.snitcher"
    compileSdk = Configuration.compileSdk
    minSdk = Configuration.minSdk

    lint {
      abortOnError = false
    }

    withHostTest {}

    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
    }
  }

  jvm("desktop") {
    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
    }
  }

  // Compose Multiplatform no longer publishes the intel simulator target.
  listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
    target.binaries.framework {
      baseName = "Snitcher"
      isStatic = true
    }
  }

  applyDefaultHierarchyTemplate()

  sourceSets {
    val jvmSharedMain = create("jvmSharedMain")
    jvmSharedMain.dependsOn(commonMain.get())
    getByName("desktopMain").dependsOn(jvmSharedMain)
    getByName("androidMain").dependsOn(jvmSharedMain)

    commonMain.dependencies {
      // these carry types of the public API, such as StateFlow, Color, TextStyle and Modifier.
      api(libs.compose.runtime)
      api(libs.compose.foundation)
      api(libs.compose.ui)
      api(libs.coroutines)
      api(libs.kotlinx.serialization.core)

      implementation(libs.compose.material)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.okio)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.coroutines.test)
    }

    androidMain.dependencies {
      // ExceptionTraceActivity extends ComponentActivity, which consumers subclass.
      api(libs.androidx.activity.compose)

      implementation(libs.androidx.core.ktx)
    }

  }
}

baselineProfile {
  filter {
    include("com.skydoves.snitcher.**")
  }
}

dependencies {
  baselineProfile(project(":benchmark"))
}
