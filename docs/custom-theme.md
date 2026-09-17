# Snitcher Theme

The pre-built screens are styled by a `SnitcherThemeConfig`. Give one to the platform installer, and the built-in `ExceptionTraceActivity`, as well as every screen you wrap in `SnitcherTheme`, will be drawn with it:

```kotlin
Snitcher.install(
  application = this,
  theme = SnitcherThemeConfig(
    lightColors = SnitcherColor.defaultColors().copy(primary = Color(0xFF6650a4)),
    darkColors = SnitcherColor.defaultDarkColors().copy(primary = Color(0xFFD0BCFF)),
    typography = SnitcherTypography.defaultTypography().copy(
      title = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Black),
    ),
    shapes = SnitcherShapes(button = RoundedCornerShape(20.dp)),
  ),
)
```

`lightColors` and `darkColors` are picked by the system dark mode. Start from `defaultColors()`, `defaultDarkColors()` and `defaultTypography()` and `copy` what you want to change, since a palette and a typography carry no partial defaults.

**SnitcherColor**

| Property | Where it is used |
|---|---|
| `primary` | The title, the section labels, and the button background |
| `onPrimary` | The content that is drawn on the buttons |
| `background` | The screen background, which is also drawn behind the system bars |
| `textHighEmphasis` | The exception message, the stack trace, and the restore screen texts |
| `textLowEmphasis` | The package and device information |
| `outline` | The border of the stack trace container |

**SnitcherTypography**

`title`, `message`, `deviceInfo`, `sectionLabel`, `stacktrace`, and `button` are plain `TextStyle`s, so fonts, sizes, and weights are all yours.

**SnitcherShapes**

`button` and `stacktrace` are plain `Shape`s.

You can also swap the theme at any time, which restyles the screens right away:

```kotlin
Snitcher.theme = Snitcher.theme.copy(darkColors = SnitcherColor.defaultDarkColors())
```

If you build your own trace screens, wrap them in `SnitcherTheme`. Its defaults come from the installed configuration, so your screens and the pre-built ones stay in sync, and you can still override any of them for a single screen:

```kotlin
SnitcherTheme(
  colors = Snitcher.theme.lightColors.copy(primary = Color.Blue),
) {
  exception?.let {
    ExceptionTraceScreen(snitcherException = it, onRestore = { Snitcher.clear() })
  }
}
```

`SnitcherTheme.colors` reads the theme of the surrounding composition, so build the argument from `Snitcher.theme` rather than from `SnitcherTheme.colors` when you call `SnitcherTheme` yourself.

The texts of the pre-built screens are plain strings rather than platform resources, so they are shared across platforms. Give a `SnitcherStrings` to the installer to translate or reword them:

```kotlin
Snitcher.install(
  application = this,
  strings = SnitcherStrings(
    restoreTitle = "Oops, Restore the previous screen?",
    restoreDescription = "The app crashed unexpectedly. Would you like to return to where you left off?",
    restoreButton = "Restore",
    traceRestoreButton = "Restore App",
    traceDebugButton = "Debug on IDE",
    traceStacktrace = "Stacktrace",
    traceCopied = "Copied!",
  ),
)
```

Both can also be replaced at any time, which restyles the screens right away:

```kotlin
Snitcher.theme = Snitcher.theme.copy(darkColors = SnitcherColor.defaultDarkColors())
Snitcher.strings = SnitcherStrings(traceStacktrace = "Stack trace")
```
