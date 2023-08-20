# Overview

🦉 Snitcher captures global crashes, enabling easy redirection to the exception tracing screen for swift recovery.

## What is Snitcher?

Snitcher offers versatile advantages such as aiding in debugging crashes during development, facilitating easy sharing of exceptions by your QA team, enhancing user experiences with recovery screens instead of abrupt closures, and enabling global exception tracing and customized launch behaviors tailored to your specific needs. You have the complete freedom to customize the crash tracing screens according to your build types and preferences, reporting to the Firebase's Crashlytics with displaying the exception screen, including options like launching a designated Activity, sending messages to your BroadcastReceiver, or any other desired actions.

## Download

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/snitcher.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22snitcher%22)

### Gradle

Add the dependency below to your **module**'s `build.gradle` file:

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:snitcher:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:snitcher:$version")
    }
    ```

## Usage

Installing Snitcher is a breeze; it hooks into global exceptions, replacing application closure with informative exception tracing screens. You can seamlessly install Snitcher using the following example:

```kotlin
class App : Application() {

  override fun onCreate() {
    super.onCreate()

    Snitcher.install(application = this)
  }
}
```

Upon completing the project build, in the event of encountering any exceptions, you will be presented with the screens illustrated below, tailored to your specific build types, rather than experiencing an abrupt application closure:

| Debug | Release |
| ------- | ------- |
| ![debug](https://github.com/skydoves/skydoves/assets/24237865/f0a47ac1-95a3-42c8-913a-81859f458a37) | ![release](https://github.com/skydoves/skydoves/assets/24237865/8a3bbc95-7441-4af2-ae4c-3b0ba9de3171) |