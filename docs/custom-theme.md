# Snitcher Theme

The pre-built screens are styled by a `SnitcherThemeConfig`. Give one to `Snitcher.install`, and the built-in `ExceptionTraceActivity`, as well as every screen you wrap in `SnitcherTheme`, will be drawn with it:

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

`lightColors` and `darkColors` are picked by the system dark mode. Every field has a default, so you only need to declare what you want to change.

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
  colors = SnitcherTheme.colors.copy(primary = Color.Blue),
) {
  if (exception != null) {
    ExceptionTraceScreen(
      launcher = launcher,
      snitcherException = exception!!,
    )
  }
}
```

If you wish to personalize the text strings within the pre-built UIs, you can override the following string values within your `strings.xml` file:

```xml
<string name="snitcher_release_crash_screen_title">Oops, Restore the previous screen?</string>
<string name="snitcher_release_crash_screen_description">The app crashed unexpectedly. We apologize for the inconvenience. Would you like to return to where you left off?</string>
<string name="snitcher_release_crash_screen_restore">Restore</string>
<string name="snitcher_debug_crash_screen_restore">Restore App</string>
<string name="snitcher_debug_crash_screen_debug_on_ide">Debug on IDE</string>
<string name="snitcher_debug_crash_screen_stacktrace">Stacktrace</string>
```
