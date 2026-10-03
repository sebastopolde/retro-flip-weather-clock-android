# HTC Weather Clock Modern Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a locally testable Android 6–16 recreation of the HTC flip weather widget and a reproducible Windows emulator environment.

**Architecture:** Keep the HTC-signed APK immutable and use it only for analysis. Build an independently signed Kotlin application around standard `AppWidgetProvider`/`RemoteViews`, an isolated weather provider, cached state, and WorkManager refreshes.

**Tech Stack:** JDK 17, Android SDK 36, AGP 8.10.1, Gradle 8.11.1, Kotlin 2.1.20, AndroidX AppCompat/WorkManager, JUnit, Robolectric, JADX, Apktool, Android Emulator.

**Spec:** `docs/superpowers/specs/2026-10-03-htc-weather-clock-modern-design.md`

## Global Constraints

- Application ID: `fr.papycha.htcweatherclock`.
- `minSdk = 23`, `targetSdk = 36`, `compileSdk = 36`.
- The original HTC APK is never modified or included in a distributable build.
- No runtime dependency on HTC Sense, HTC launcher permissions, or `com.htc.software.hdk`.
- The clock remains functional with no network and no cached weather.
- The primary interactive test target is an Android 16/API 36 Google APIs x86_64 AVD.

## Review Focus

- A fresh install with no settings and no network must render a usable clock and configuration hint.
- A malformed or incomplete weather response must retain the last valid snapshot.
- Launcher resize callbacks must select a valid layout at every supported width.
- Android process death or device reboot must reconstruct every widget from persistent state.
- Multiple widget instances must keep independent location and unit settings.

---

### Task 1: Reproducible Windows toolchain

**Files:**
- Create: `tools/bootstrap.ps1`
- Create: `tools/env.ps1`
- Create: `tools/create-avd.ps1`
- Create: `tools/start-emulator.ps1`
- Create: `docs/ENVIRONMENT.md`

**Interfaces:**
- Produces: project-local environment variables `JAVA_HOME`, `ANDROID_HOME`, and commands for `adb`, `sdkmanager`, `avdmanager`, and `emulator`.

- [ ] Install JDK 17 and Android command-line tools from their official distributions.
- [ ] Verify the command-line-tools archive against SHA-256 `90ae805d20434428bffcb699c290860f19bb5f66a67e6b330067e3de801fb04a`.
- [ ] Install platform-tools, emulator, platform 36, build-tools 36.0.0, and Android 16 Google APIs x86_64 image.
- [ ] Accept Android SDK licenses and create AVD `HTCClock_API36`.
- [ ] Run `sdkmanager --list_installed`, `adb version`, and `emulator -accel-check`; record successful output in `docs/ENVIRONMENT.md`.
- [ ] Commit the environment scripts and documentation.

### Task 2: Official APK archive and analysis

**Files:**
- Create: `reference/original/README.md`
- Create: `reference/original/SHA256SUMS.txt`
- Create: `tools/analyze-apk.ps1`
- Create: `analysis/README.md`
- Create: `analysis/htc-dependencies.md`
- Create: `analysis/manifest-summary.md`

**Interfaces:**
- Produces: verified APK path and documented resource/dependency inventory used as the visual reference for Task 4.

- [ ] Download HTC Weather Clock Widget 10.10.1059527 from the APKMirror page that reports the HTC signing certificate.
- [ ] Verify file SHA-256 `40179c84772d7c0085f7574682a979428350a0adac51b2e85a39a828fe2d70f0` and certificate SHA-256 `a6ce16fbe0b5b1468b48f8fba9f0a608728c424509e2cb9310ea696b5fc4b2be`.
- [ ] Install project-local JADX and Apktool and generate their outputs under ignored `analysis/generated/`.
- [ ] Document HTC-only permissions, features, providers, services, resources, layouts, and widget dimensions.
- [ ] Commit the provenance and analysis reports without generated decompilation output.

### Task 3: Minimal independently buildable widget

**Files:**
- Create: Gradle wrapper and root Gradle configuration.
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/fr/papycha/htcweatherclock/widget/FlipClockWidgetProvider.kt`
- Create: `app/src/main/java/fr/papycha/htcweatherclock/widget/WidgetRenderer.kt`
- Create: `app/src/main/res/xml/flip_clock_widget_info.xml`
- Create: legacy and `layout-v31` widget layouts.
- Test: `app/src/test/java/fr/papycha/htcweatherclock/widget/WidgetLayoutSelectorTest.kt`

**Interfaces:**
- Produces: `WidgetRenderer.render(context, appWidgetId, state): RemoteViews` and installable debug APK.

- [ ] Write layout-selection tests for compact, standard, wide, and Android 12+ sizes, including every Review Focus resize case.
- [ ] Run the tests and confirm they fail before implementation.
- [ ] Implement the smallest provider, renderer, manifest, resources, and preview that expose a 4x2 widget.
- [ ] Run `gradlew testDebugUnitTest assembleDebug` and confirm success.
- [ ] Install the APK on API 36 and confirm it appears in the launcher widget picker.
- [ ] Commit the minimal widget.

### Task 4: Functional flip clock and deterministic weather

**Files:**
- Create: `clock/ClockSnapshot.kt`, `clock/ClockFormatter.kt`
- Create: `weather/WeatherSnapshot.kt`, `weather/WeatherProvider.kt`, `weather/DemoWeatherProvider.kt`
- Create: `storage/WidgetPreferences.kt`
- Modify: `widget/WidgetRenderer.kt`, widget layouts and drawables.
- Test: formatter, weather fallback, persistence isolation, and renderer state tests.

**Interfaces:**
- Produces: per-widget `WidgetState`, deterministic demo weather, and a renderer that updates time/date/weather without HTC services.

- [ ] Write failing tests for 12/24-hour formatting, midnight/noon, locale date formatting, no-cache fallback, malformed weather, and independent widget IDs.
- [ ] Implement immutable clock/weather models and deterministic demo weather.
- [ ] Implement private per-widget persistence and complete `RemoteViews` reconstruction.
- [ ] Recreate the recognizable flip-panel composition with independently drawn resources.
- [ ] Run unit tests, assemble the debug APK, install it, place the widget, and verify one minute rollover.
- [ ] Commit the functional offline widget.

### Task 5: Live weather and end-to-end verification

**Files:**
- Create: `weather/OpenMeteoProvider.kt`, DTOs and mapper.
- Create: `work/WeatherRefreshWorker.kt`, `work/RefreshScheduler.kt`
- Create: `settings/WidgetConfigurationActivity.kt`
- Create: `tools/smoke-test.ps1`
- Create: `docs/TESTING.md`
- Test: HTTP parsing, stale-cache behavior, worker retry, configuration validation, and process-restart reconstruction.

**Interfaces:**
- Consumes: `WeatherProvider`, `WidgetState`, `WidgetRenderer.render`.
- Produces: city search/configuration, cached live weather, periodic refresh, and a repeatable emulator smoke test.

- [ ] Write failing tests for valid, empty, malformed, offline, and stale Open-Meteo responses plus every remaining Review Focus case.
- [ ] Implement the Open-Meteo provider and mapper behind `WeatherProvider`.
- [ ] Implement configuration and WorkManager refresh with network constraints and exponential backoff.
- [ ] Run all unit and instrumentation tests and build the APK.
- [ ] Boot API 36, install the APK, place/configure the widget, test offline mode and resize, and capture a final screenshot.
- [ ] Run compatibility builds/tests for API 24, 29, 33, and 36 where images are available.
- [ ] Document the exact build/install/test commands and commit the verified deliverable.
