# KickPlan Training

**Track your football training manually and view upcoming matches in one simple planner.**

KickPlan Training is a native Android app (Kotlin + Jetpack Compose) for logging
football training sessions by hand and following your weekly progress. It also
includes an additional **Match Schedule** screen that loads football matches from
the [football-data.org](https://www.football-data.org) API v4. The app stays
focused on personal training progress — the match schedule is a supporting
feature only, not a live-score clone.

---

## Features

- **Training log** — add, edit and delete football training sessions manually.
- **Training types** — Passing, Shooting, Running, Defense, Stamina, Dribbling, Goalkeeper, Recovery, Custom.
- **Duration, difficulty (Easy/Medium/Hard) and intensity (Low/Normal/High)** for each session, plus notes.
- **Today screen** — current date, weekly session count and minutes, today's sessions, quick-add drills, a next-match preview and shortcuts.
- **Quick add** — one tap to log common drills (e.g. Passing · 30 min · Medium).
- **Training history** — daily summary cards in reverse chronological order.
- **Day detail** — per-day totals, session list, type/difficulty breakdown, and reset-day with confirmation.
- **Weekly progress** — 7-day bar chart (simple Compose views), totals, daily average, best day, top focus, hard-session count and a simple streak.
- **Match Schedule** — upcoming matches from football-data.org with manual refresh, cached data and demo fallback.
- **Favorite team** — a locally saved name used to highlight matches.
- **Settings** — preferences, disclaimers, privacy note and full local-data reset.

---

## Manual training log disclaimer

> KickPlan Training is a manual football training log. Training sessions are added
> by the user. The app does not track activity automatically, does not use
> sensors, does not connect to Google Fit or Health Connect, and does not provide
> medical or professional coaching advice.

This is **not** a medical or professional performance app. It does not diagnose
fitness, predict results, provide coaching, or automatically improve performance.

## Match schedule API disclaimer

> Match schedule data is provided by football-data.org. Availability, accuracy,
> competitions, and update frequency depend on the API provider and the current
> API plan.

---

## What this app does **not** do

No ads, no analytics, no payments, no Firebase. No account registration and no
cloud sync — everything is stored locally on the device. No betting, no odds, no
bookmakers, no predictions and no gambling language. No official club or league
logos and no real player photos (team placeholders use plain initials). The app
never claims to be official and is not a live-score, streaming, or highlights app.

---

## football-data.org API v4

The Match Schedule screen calls a single endpoint:

```
GET https://api.football-data.org/v4/matches
```

The request sends your token in the `X-Auth-Token` header. Optional `dateFrom`
and `dateTo` query parameters are supported; the competition code is applied
locally for stability across API plans. **No odds, prediction, bookmaker or
betting endpoints are ever called.**

### API usage policy (built in)

- No per-second refresh and no continuous live polling.
- Manual **Refresh** button only; no background or automatic refresh.
- The latest successful response is cached locally and shown on app start.
- Demo data is shown when there is no token and no cache.
- Friendly messages are shown when the API limit is reached, the internet is
  unavailable, or the response is invalid.

---

## Configure your API token in `local.properties`

1. Register for a free token at <https://www.football-data.org/client/register>.
2. Copy `local.properties.example` to `local.properties`.
3. Set your values:

   ```properties
   sdk.dir=/path/to/your/Android/sdk
   FOOTBALL_DATA_API_TOKEN=your_real_token_here
   FOOTBALL_API_BASE_URL=https://api.football-data.org/v4
   ```

These are exposed to the app through `BuildConfig`:

- `BuildConfig.FOOTBALL_DATA_API_TOKEN`
- `BuildConfig.FOOTBALL_API_BASE_URL`

> ⚠️ **Never commit `local.properties` or your real API token.** It is
> git-ignored by default. The token must not appear in source code, the README,
> screenshots, tests, or CI logs. The app also never logs the raw token.

**If the token is missing, empty, or equals `your_api_token_here`,** the app
automatically uses local demo match data, shows a friendly message, and continues
to work normally.

---

## Local storage & DataStore

All user data is stored **only on the device** using **DataStore Preferences**,
serialized as a single JSON string with **kotlinx.serialization**. There is no
database (Room is intentionally not used — DataStore JSON is sufficient).

Stored locally: training sessions, settings, onboarding flag, favorite team,
match-schedule settings, the cached match schedule, the last API update time, and
the last API error message.

The storage layer merges loaded data with default values and falls back to a
fresh, empty state if the stored JSON is missing or corrupted, so the app never
crashes on bad or empty data.

---

## Internet & permissions

The app declares exactly **one** permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

`INTERNET` is used **only** for the Match Schedule API. The app requests **no**
runtime permissions and does **not** use location, camera, microphone, contacts,
storage/gallery, notifications, calendar, alarms, activity recognition, body
sensors, Google Fit, Health Connect, or any wearable integration. There is no
automatic activity tracking of any kind.

---

## Design

**Visual style: "Red Match-Day Training Planner".** Energetic, sporty and clean.

- **Palette** — Match Red-Orange `#F04423`, Deep Red-Orange `#D9361E`, Bright
  Orange Accent `#FF6A2A`, dark charcoal strips `#151515` / `#202124`, light
  background `#F5F6F8`, white cards, and football accent colors for team badges.
- **App icon** — a dark 3D football render with an orange swoosh/arrow on a
  near-black plate, delivered as a full adaptive icon (bitmap foreground +
  dark background) with round and legacy PNG mipmaps at every density plus a
  512 px Play Store icon. No official logos, no club branding, no betting
  symbols. Regenerate from a master PNG any time with
  `python3 scripts/generate_icons.py <master.png>`.
- **Splash screen** — near-black background with the centered football mark,
  implemented with the AndroidX SplashScreen API (no default Android splash,
  no heavy assets).

### Layout uniqueness — "Energetic Match-Day Training Board"

The Today screen is built as a match-day training board rather than a generic
dashboard: a strong red-orange header, a compact date label, a bold dark
weekly-progress card with seven mini strips, quick-add drill chips, compact
white session cards, and a small supporting next-match card with circular team
initials. It deliberately avoids the generic "mascot → title → stats → stack of
big buttons → settings" template, and avoids any betting/odds/casino/live-score
styling. History shows past training days as cards; Weekly shows seven day bars;
Match Schedule is a secondary section with clean white match cards.

---

## Open the project in Android Studio

1. Install **Android Studio** (latest stable) with **Android SDK Platform 35**.
2. **File → Open** and select the project root folder.
3. Android Studio will generate the Gradle wrapper (`gradlew`) automatically and
   sync the project.
4. Create your `local.properties` (see above), then **Run** on a device/emulator
   running Android 7.0 (API 24) or newer.

> The repository ships the `gradlew` / `gradlew.bat` scripts and
> `gradle/wrapper/gradle-wrapper.properties`. The binary `gradle-wrapper.jar` is
> generated on first sync by Android Studio, or in CI by the
> `gradle wrapper` step. To generate it manually run: `gradle wrapper --gradle-version 8.9`.

---

## Build from the command line

```bash
# Debug build
./gradlew :app:assembleDebug

# Release APK + AAB (requires signing config — see below)
./gradlew :app:assembleRelease :app:bundleRelease
```

Outputs:

- APK → `app/build/outputs/apk/release/app-release.apk`
- AAB → `app/build/outputs/bundle/release/app-release.aab` (**Google Play upload target**)

---

## Android configuration

- `compileSdk = 35`, `targetSdk = 35`, `minSdk = 24`.
- Android Gradle Plugin **8.6.1**, Kotlin **2.0.21**, Gradle **8.9**, JDK **17**.
- **16 KB memory page size:** the app has no native (`.so`) libraries, so the
  resulting AAB is compatible with Android 15+ devices that use 16 KB memory
  pages. No old native libraries or unnecessary native SDKs are added.
- No Firebase, ads, analytics, payment, Google Fit, Health Connect, sensor,
  wearable, notification, or background-task SDKs.

---

## Generate a release keystore (PKCS12)

Release APK and AAB must be signed with a **real PKCS12 keystore**, never a debug
key. Generate one with:

```bash
keytool -genkeypair -v -storetype PKCS12 \
  -keystore kickplan-training-release-key.p12 \
  -alias kickplan_training_key \
  -keyalg RSA -keysize 2048 -validity 10000
```

Use the **same password** for the keystore and the key. Keep the `.p12` file and
its passwords private — they are git-ignored and must never be committed.

### Local release signing (optional)

Add these to `local.properties` (git-ignored) to sign a local release build:

```properties
ANDROID_KEYSTORE_PATH=/absolute/path/to/kickplan-training-release-key.p12
ANDROID_KEYSTORE_PASSWORD=your_password
ANDROID_KEY_ALIAS=kickplan_training_key
ANDROID_KEY_PASSWORD=your_password
```

If no release keystore is configured locally, a local release build falls back to
the debug key **for testing only** — CI always signs with the real keystore.

---

## GitHub Actions (CI) & Secrets

The workflow `.github/workflows/android-build.yml` runs on push to `main`. It:

1. Sets up JDK 17 and the Android SDK.
2. Installs `platforms;android-35` and `build-tools;35.0.0`.
3. Writes `local.properties` from secrets or safe placeholders (build succeeds
   even without an API token — the app then uses demo data).
4. Decodes the release keystore from secrets.
5. Builds the **signed release APK and AAB**.
6. **Verifies the APK signature** with `apksigner verify --print-certs`, prints
   the certificate, and **fails the build if it contains `CN=Android Debug`** —
   preventing Google Play rejection from a debug-signed artifact.
7. Uploads the APK and AAB as build artifacts.

### Required GitHub Secrets

| Secret | Purpose |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64 of your `.p12` keystore |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Key alias (e.g. `kickplan_training_key`) |
| `ANDROID_KEY_PASSWORD` | Key password (same as keystore password) |
| `FOOTBALL_DATA_API_TOKEN` | *(optional)* football-data.org token |

Create the base64 keystore secret with:

```bash
base64 -i kickplan-training-release-key.p12 | pbcopy   # macOS
base64 -w0 kickplan-training-release-key.p12           # Linux
```

Then in GitHub: **Settings → Secrets and variables → Actions → New repository
secret**. Keystores and passwords are only ever provided through these secrets.

---

## Release optimization notes

R8 / resource shrinking is configured in `app/build.gradle.kts`:

1. **First verify a non-minified release** by temporarily setting
   `isMinifyEnabled = false` and `isShrinkResources = false`, and confirming the
   app launches.
2. Then keep them enabled (the committed default):
   ```kotlin
   isMinifyEnabled = true
   isShrinkResources = true
   proguardFiles(
       getDefaultProguardFile("proguard-android-optimize.txt"),
       "proguard-rules.pro"
   )
   ```
3. Re-test launch after enabling minify/shrink. `proguard-rules.pro` keeps all
   `@Serializable` models and DTOs so DataStore JSON and API decoding keep working
   in minified builds.

Only standard Android R8/ProGuard is used — no risky third-party obfuscation.

---

## Local launch verification checklist

A green CI build is not proof the app launches. Before release:

```bash
adb install app/build/outputs/apk/release/app-release.apk
adb logcat
```

Confirm there are no `Cannot find class`, `ClassNotFoundException`,
`NoSuchMethodError`, serialization, DataStore JSON, missing navigation argument,
invalid date/time/duration, invalid API response, missing API token, or signature
errors, then walk through:

- First launch with empty storage → complete onboarding.
- Quick-add a session; add a custom session; edit date/time/duration/type; delete a session.
- Reset a selected day; check Today, History and Weekly progress.
- Set a favorite team.
- Open Match Schedule with **no** API token (demo data) and **with** a token; refresh manually; simulate API failure; check cached matches; clear the match cache.
- Reset all local data, relaunch, and launch in airplane mode.
- Verify the release APK signature and confirm **only** the `INTERNET` permission is present (no location, camera, microphone, contacts, storage, notifications, sensors, Google Fit, Health Connect, or wearable permissions).

---

## Privacy note

> KickPlan Training stores training sessions, settings, favorite team, and cached
> match data on this device. The app uses internet only to load football match
> data from football-data.org. No account, no ads, no analytics, no payments, no
> Firebase, no location, no notifications, no sensors, no Google Fit, and no
> Health Connect.

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Kotlin Coroutines ·
ViewModel · DataStore Preferences · Kotlinx Serialization · Retrofit · OkHttp ·
Gradle Kotlin DSL. Architecture is simple MVVM: one local repository
(`AppRepository`), one isolated API repository (`FootballDataRepository`), and
per-area ViewModels with an immutable UI state for the Match Schedule screen.
