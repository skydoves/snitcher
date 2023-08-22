# Tracing Global Exceptions

You can trace the global exceptions by providing `exceptionHandler` lambda parameter. This can be highly beneficial if you intend to gather and report exceptions to other platforms, such as [Firebase Crashlyrics](https://firebase.google.com/docs/crashlytics).

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    Firebase.crashlytics.log(exception.stackTrace) // or exception.message, 
  }
)
```

The `exceptionHandler` gives you `SnitcherException`, encompassing the exception message, stack traces, package name, and thread information. Additionally, it enables you to recover the original `Throwable` instance with the `SnitcherException.throwable` extension.

```kotlin
Snitcher.install(
  application = this,
  exceptionHandler = { exception: SnitcherException ->
    val message: String = exception.message
    val stackTrace: String = exception.stackTrace
    val throwable: Throwable = exception.throwable
    val threadName: String = exception.threadName

    // do somethings
  }
)
```