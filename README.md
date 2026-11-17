# App Category Widgets

Native Android app for creating multiple independent home-screen app widgets.

- Built-in Play Store category set and custom categories.
- Manual per-app category assignment.
- Optional public Google Play listing metadata lookup, cached locally.
- Independent widget configuration.
- Scrolling list or grid layout.
- A–Z or Z–A app sorting.
- Adjustable background color and opacity, including fully transparent.
- Adjustable text color and size, icon size, grid columns, title and icon visibility.
- App rows launch the selected app.

## Category lookup caveat
Google does not provide a supported public API for installed-app Play Store categories. The optional lookup reads public listing metadata when available, but Google may block or change the listing page. It is not guaranteed for every app. Android metadata is a fallback; manual assignment is always available.

## Privacy
App inventory and assignments remain on-device. Online lookup sends app package names to Google Play listing URLs. No account or analytics service is included.
