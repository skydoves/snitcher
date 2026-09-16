# Tracing Global Exceptions

Every platform installer takes an `exceptionHandler` lambda, which runs with the captured crash before the process goes down. It is useful to report exceptions to another platform, such as [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics).

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    Firebase.crashlytics.log(exception.stackTrace)
  },
)
```

The handler gives you a `SnitcherException`, which carries the exception name, the message, the whole stack trace, and the thread information.

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    val name: String = exception.packageName
    val message: String = exception.message
    val stackTrace: String = exception.stackTrace
    val threadName: String = exception.threadName

    // do something
  },
)
```

On Android and on the desktop the original throwable can be restored, which is handy for reporters that take a `Throwable`:

```kotlin
val throwable: Throwable = exception.toThrowable()
```

Kotlin/Native does not expose structured stack trace frames, so `toThrowable()` is only available on Android and on the desktop, and `SnitcherException.stackTraceElement` is empty on iOS. The whole trace is always available as a string.

## Observing the crash anywhere

Snitcher publishes the most recent crash as a `StateFlow`, so any part of your app can observe it, including on the launch that follows a crash.

```kotlin
val exception: SnitcherException? by Snitcher.exception.collectAsState()
val launcher: String by Snitcher.launcher.collectAsState()
```

Once you have handled it, clear it so that it is not displayed again:

```kotlin
Snitcher.clear()
```
