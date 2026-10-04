# Widget Actions and Placement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply validated addresses immediately, simplify the dashboard, open exact-location Windy forecasts, and keep the current-weather icon correctly anchored in real widgets.

**Architecture:** Keep Android launch behavior in `ExternalActionLauncher`, but move URL construction and the address-success decision into small pure Kotlin contracts covered by unit tests. Pass the saved coordinates through widget pending intents, and make the icon position relative to the weather-panel boundary so launcher height no longer affects it.

**Tech Stack:** Android RemoteViews/XML, Kotlin, JUnit 4, AndroidX instrumentation, Gradle.

**Spec:** `docs/superpowers/specs/2026-10-04-widget-actions-and-placement.md`

## Global Constraints

- Preserve Android 6 through Android 16 compatibility (`minSdk 23`, `targetSdk 36`).
- Preserve widget clock-area alarm behavior while removing only dashboard alarm controls.
- Use exact saved latitude/longitude for weather detail whenever available.
- Keep classic and weekly previews visually consistent with installed widgets.
- Test only on `emulator-5580`; never operate the physical Samsung device.

## Review Focus

- A successful address test must persist and refresh widgets, not merely recolor the button.
- Coordinates must use a locale-independent decimal point and preserve southern/western negative values.
- Legacy configurations without coordinates must still open a useful Windy page.
- Widget PendingIntent identity must remain unique per widget/action after adding location extras.
- Icon anchoring must remain stable at both preview height and launcher-assigned height.

---

### Task 1: Exact-location weather links

**Files:**
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/widget/WidgetActionSpec.kt`
- Modify: `app/src/test/java/fr/papycha/htcweatherclock/widget/WidgetActionSpecTest.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/widget/WidgetActionActivity.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/widget/WidgetRenderer.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/widget/WeeklyWidgetRenderer.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/navigation/ExternalActionLauncher.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/MainActivity.kt`

**Interfaces:**
- Produces: `WidgetActionSpec.weatherUrl(location: ResolvedLocation?, fallbackCity: String): String` and widget action extras carrying an optional `ResolvedLocation`.
- Consumes: stored `ResolvedLocation` from `WidgetPreferences.location`.

- [ ] **Step 1: Write failing URL tests** for exact positive coordinates, negative coordinates, and blank legacy fallback.
- [ ] **Step 2: Run `gradlew.bat testDebugUnitTest --tests "*WidgetActionSpecTest"`**; expected failure is the missing exact-location API / old Google URL.
- [ ] **Step 3: Implement Windy URL generation and pass saved location through dashboard and widget actions.**
- [ ] **Step 4: Re-run the focused test**; expected all `WidgetActionSpecTest` tests pass.
- [ ] **Step 5: Commit** with message `feat: open exact Windy forecasts`.

### Task 2: Apply tested addresses and simplify dashboard

**Files:**
- Create: `app/src/main/java/fr/papycha/htcweatherclock/AddressValidationAction.kt`
- Create: `app/src/test/java/fr/papycha/htcweatherclock/AddressValidationActionTest.kt`
- Modify: `app/src/main/java/fr/papycha/htcweatherclock/MainActivity.kt`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Create: `app/src/androidTest/java/fr/papycha/htcweatherclock/DashboardLayoutTest.kt`

**Interfaces:**
- Produces: `AddressValidationAction.forTrigger(trigger): AddressValidationAction` whose test and refresh triggers both persist a successful result.
- Consumes: existing `WidgetPreferences.setConfiguration` and `updateInstalledWidgets` side effects.

- [ ] **Step 1: Write a failing unit test** proving the Test button's success action is `APPLY_AND_REFRESH`.
- [ ] **Step 2: Run the focused unit test**; expected failure is missing `AddressValidationAction`.
- [ ] **Step 3: Implement the action contract and make successful Test persist configuration and refresh installed widgets; update success wording.**
- [ ] **Step 4: Re-run the focused unit test**; expected pass.
- [ ] **Step 5: Write an instrumentation test** that launches the dashboard and proves the clock/alarm menu text and buttons are absent.
- [ ] **Step 6: Run the instrumentation test before layout removal**; expected failure because the menu is visible.
- [ ] **Step 7: Remove the dashboard card and its listeners while preserving widget alarm actions, then re-run the instrumentation test**; expected pass.
- [ ] **Step 8: Commit** with message `feat: apply tested address immediately`.

### Task 3: Anchor weather icons to the panel boundary

**Files:**
- Modify: `app/src/main/res/layout/widget_compact.xml`
- Modify: `app/src/main/res/layout/widget_classic.xml`
- Modify: `app/src/main/res/layout/widget_wide.xml`
- Modify: `app/src/main/res/layout/widget_weekly.xml`
- Create: `app/src/androidTest/java/fr/papycha/htcweatherclock/widget/WidgetIconPlacementTest.kt`

**Interfaces:**
- Produces: layouts where `weather_icon` is positioned relative to the current-weather panel seam at every host height.
- Consumes: the same layouts in `MainActivity` previews and `RemoteViews` renderers.

- [ ] **Step 1: Write a failing instrumentation test** that inflates classic and weekly layouts at two heights and asserts the icon center stays on the weather-panel top boundary within 2dp.
- [ ] **Step 2: Run the focused instrumentation test**; expected failure because current top margins drift when height changes.
- [ ] **Step 3: Move each icon into its weather panel, disable clipping where needed, and position it with a negative half-height translation so it straddles the seam.**
- [ ] **Step 4: Re-run the focused instrumentation test**; expected pass at both heights.
- [ ] **Step 5: Run all unit tests, all instrumentation tests, lint, and assembleDebug.**
- [ ] **Step 6: Install only on `emulator-5580`, inspect 4x3 and 4x4 home-screen widgets, and capture screenshots.**
- [ ] **Step 7: Commit** with message `fix: anchor widget weather icons`.
