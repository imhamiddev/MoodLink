# MoodLink 💜

Share how you feel with the people you love — right on their Android home screen.

Friends and partners connect with a short invite code, then each person's current mood
(emoji + a short note) appears live in a home-screen widget: *"Kowsar is happy now"*, *"Kowsar is sad now"*.

## Features
- English and Persian (RTL), switchable in the app
- Email + password sign up / log in (Supabase Auth)
- Personal 6-character invite code · send / accept / decline connection requests
- 10 moods, one tap to share
- Live "Your people" feed
- Home-screen widget (Jetpack Glance) with periodic + live refresh

## Stack
Kotlin · Jetpack Compose (Material 3) · Glance · Hilt · Supabase (Auth · Postgres · Realtime) · WorkManager

## Setup
1. Create a project at [supabase.com](https://supabase.com).
2. Run `supabase/migrations/20261104000000_init.sql` in the **SQL editor** (or `supabase db push`).
3. **Authentication → Providers → Email**: turn **Confirm email** off while developing, so sign-up logs in right away.
4. Copy the **Project URL** and the **anon / publishable key** from *Project Settings → API*.
5. Put them in `local.properties` (git-ignored):
   ```properties
   SUPABASE_URL=https://xxxxx.supabase.co
   SUPABASE_ANON_KEY=eyJ...
   ```
6. Open in Android Studio (JDK 17) and run.

## Build on GitHub Actions
Add two repository secrets under **Settings → Secrets and variables → Actions**:

| Secret | Value |
| --- | --- |
| `SUPABASE_URL` | Project URL |
| `SUPABASE_ANON_KEY` | anon / publishable key |

Every push and PR runs `assembleDebug` and uploads `moodlink-debug-apk` as an artifact. You can also start it by hand from the **Actions** tab (*Run workflow*). Without the secrets the build still succeeds, but the APK points at a placeholder project.

The anon key is designed to ship inside the app; access is enforced by the row level security policies in the migration. Never put the `service_role` key in the app or in secrets used for the build.

## Releases
Push a tag (`git tag v0.1.0 && git push origin v0.1.0`) or run **Actions → Release → Run workflow**.
The workflow builds the APK and attaches it to a GitHub Release. The APK is signed with the debug key, which is fine for sideloading.

## Design
Lavender/pink gradient, frosted-glass cards, rounded shapes and a purple→blue CTA, based on the reference mood-tracker UIs.
