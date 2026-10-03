# HTC Weather Clock Modern — Design

## Objective

Create a locally testable, modern Android recreation of the classic HTC Sense flip weather clock. Preserve the official HTC APK unchanged as a behavioral and visual reference, but ship a separately signed implementation that has no runtime dependency on HTC Sense or HTC proprietary services.

## Compatibility

- Build with Android SDK 36 and target Android 16 (API 36).
- Support Android 6.0 (API 23) through Android 16 (API 36).
- Use standard Android App Widget APIs so the widget works with common launchers, including Pixel Launcher, Samsung One UI, and Xiaomi/HyperOS.
- Avoid native libraries so the same APK works on ARM32, ARM64, x86, and x86_64 devices.
- Provide legacy layouts for Android 6–11 and responsive widget layouts for Android 12+.

## Hybrid source strategy

The official `com.htc.widget.weatherclock` APK remains immutable under `reference/original/`, accompanied by its SHA-256 hash and HTC certificate fingerprint. JADX and Apktool outputs live under `analysis/` and are used only to document behavior, dimensions, animation frames, resources, and HTC-specific dependencies. The buildable application under `app/` is an independent Kotlin implementation.

Modified output is never represented as an HTC-signed application. Development and release builds use a separate application ID and signing key.

## Project components

- `reference/original/`: untouched APK, hashes, certificate report, provenance notes.
- `analysis/`: generated reverse-engineering reports and a dependency-removal checklist.
- `app/`: Android application and App Widget implementation.
- `tools/`: reproducible PowerShell scripts for analysis, building, installation, and emulator startup.
- `docs/`: design, implementation plan, environment notes, and test instructions.

The application is divided into focused packages:

- `widget`: `AppWidgetProvider`, responsive `RemoteViews`, rendering, and click actions.
- `clock`: local time formatting and flip-frame state.
- `weather`: provider interface, Open-Meteo implementation, domain models, and mapping.
- `storage`: cached weather snapshot and per-widget settings.
- `settings`: widget configuration activity for city, units, and refresh behavior.
- `work`: reliable periodic weather refresh through WorkManager.

## Runtime behavior

The clock always renders from device-local time and remains functional without network access. Weather renders from the latest cached snapshot. A failed or unavailable weather request preserves the cached result and exposes a subtle stale-data state rather than blanking the widget.

The default location flow is manual city search, requiring no location permission. Optional device-location support may be added later without becoming a prerequisite for the widget.

Open-Meteo is the initial provider behind an interface so it can be replaced without changing widget code. Weather synchronization uses WorkManager and a network constraint. User-triggered refresh uses one-time work; periodic work is battery-friendly and not expected to run at an exact minute.

## Visual behavior

The initial functional milestone reproduces the recognizable HTC composition: two dark flip-number panels, location, weather condition, temperature, date, and weather artwork. The widget supports a compact 4x2 phone layout first, then responsive widths and foldable/tablet sizes. Android 12+ corner clipping and launcher padding are handled explicitly.

The original assets are retained only for private analysis until their redistribution status is resolved. The buildable prototype may use newly generated or redrawn equivalents where required.

## Error handling

- No network: show cached weather and keep the clock operational.
- No cache: show a configuration or retry state without crashing.
- Unknown city: keep the previous valid selection and show an inline validation message.
- Provider or parsing failure: record a concise diagnostic and retry with exponential backoff.
- Launcher resize or process restart: rebuild `RemoteViews` entirely from persisted state.
- Emulator acceleration unavailable: retain command-line build and unit-test capability and document physical-device fallback.

## Test environment

Install Android Studio, JDK, Android SDK command-line tools, platform-tools, build-tools, emulator, and required system images. Use Android 16/API 36 as the primary interactive AVD. Compatibility validation covers API 24, 29, 33, and 36; API 23 is covered by build configuration and automated tests, with an emulator added if a compatible image remains available.

Tests include JVM unit tests for mapping, formatting, cache decisions, and layout selection; Android instrumentation tests for provider/configuration behavior; installation and widget-host smoke tests; offline and stale-cache tests; resize tests; and screenshot comparison on the primary emulator.

## Milestones

1. Reproducible Windows Android toolchain and bootable API 36 emulator.
2. Verified HTC APK archive and generated static analysis report.
3. Installable prototype exposing a widget in the launcher picker.
4. Functional flip clock with cached/demo weather.
5. Live weather, configuration, responsive layouts, and compatibility matrix.

## Completion criteria

The deliverable is testable when a documented command builds and installs the independently signed APK, the API 36 emulator boots to a launcher, the widget can be placed on the home screen, the flip clock advances correctly, weather is visible from either live or deterministic cached data, and the same build passes the automated compatibility checks.
