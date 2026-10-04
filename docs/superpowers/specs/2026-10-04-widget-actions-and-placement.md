# Widget actions and weather icon placement

## Approved design

- A successful **Tester l'adresse** action immediately stores the resolved location and fresh forecast, then refreshes every installed widget. The success state remains green and confirms that the address was applied.
- Remove the visible clock/alarm section from the dashboard while preserving the widget clock-area action that opens Android alarms.
- Open full weather on Windy at the exact saved latitude and longitude. Fall back to the saved label only for legacy configurations without coordinates.
- Anchor the current-weather icon to the clock/weather boundary instead of a fixed distance from the widget top so launcher-assigned widget height does not move the icon into the clock.
- Keep preview and runtime layouts aligned for both classic and weekly widgets.

## Verification

- Unit tests cover automatic application policy and exact Windy URL generation, including legacy fallback.
- Layout-focused tests cover the icon anchor contract and absence of dashboard alarm controls.
- Run all unit tests, Android lint, debug build, and inspect installed 4x3 and 4x4 widgets on the emulator.
