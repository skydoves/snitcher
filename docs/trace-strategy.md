# Trace Strategy

You can globally trace exceptions by providing the `exceptionHandler` lambda parameter during Snitcher installation. However, there might be instances where you don't wish to launch the trace Activity but rather perform other actions, such as reporting crashes or sending messages to a `BroadcastReceiver`. In such cases, you can modify the trace strategy as shown in the example below:

```kotlin
Snitcher.install(
  application = this,
  traceStrategy = TraceStrategy.REPLACE,
  exceptionHandler = {
    // do something
  },
)
```

In this scenario, only the `exceptionHandler` lambda function will be executed without triggering the launch of any trace Activity. If the `traceStrategy` parameter is not specified, the default behavior is set to `TraceStrategy.CO_WORK`, which involves executing the `exceptionHandler` lambda and initiating the trace activity when an app crash occurs.