# MoodLink 💜

Share how you feel with the people you love — right on their Android home screen.

Friends and partners connect with a short invite code, then each person's current mood
(emoji + a short note) appears live in a home-screen widget: *"Kowsar is happy now"*, *"Kowsar is sad now"*.

## Features
- Email + password sign up / log in (Firebase Auth)
- Personal 6-character invite code · send / accept / decline connection requests
- 10 moods with pastel tiles and an optional note
- Live "Your people" feed
- Home-screen widget (Jetpack Glance) with periodic + live refresh

## Stack
Kotlin · Jetpack Compose (Material 3) · Glance · Hilt · Firebase Auth + Firestore · WorkManager

## Setup
1. Create a Firebase project and add an Android app with package `dev.imhamid.moodlink`.
2. Enable **Authentication → Email/Password** and create a **Firestore** database.
3. Download `google-services.json` into `app/` (it is git-ignored).
4. Deploy `firebase/firestore.rules`.
5. Open in Android Studio (JDK 17) and run.

## Design
Lavender/pink gradient, frosted-glass cards, rounded shapes and a purple→blue CTA, based on the reference mood-tracker UIs.
