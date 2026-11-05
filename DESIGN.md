# MoodLink design direction

Read together with the antislop rules. This file is the direction; antislop is the filter.

## Identity

MoodLink is a small, honest utility: you say how you feel, and the people you chose see it on
their home screen. The screen should feel like a page in a warm paper notebook, with the mood
itself as the only loud thing on it. The hand-drawn faces are the brand. Everything else steps back.

## Palette

One accent, one ink, one paper. Moods own the rest of the colour.

| Token | Hex | Use |
| --- | --- | --- |
| Paper | `#F5F0E6` | screen background |
| Raised | `#FFFDF8` | cards, sheets, fields |
| Ink | `#1E1B16` | text, primary buttons, selected state |
| Ink soft | `#5E574B` | secondary text (6:1 on Paper) |
| Line | `#D9D1C0` | 1dp borders and dividers |
| Accent | `#C8412B` | the single action colour: links, focus, the share button |
| Danger | `#B3261E` | destructive actions only |

Each mood has one flat colour. No gradients, no glow, no blur, no mesh backgrounds.

## Type

System sans for text. The system serif for display lines only (welcome headline, screen titles),
which gives the editorial note without shipping a font. Persian uses the same roles; the system
Arabic-script fonts cover both. Sizes step 12 / 14 / 16 / 22 / 32 / 40, nothing in between.

## Shape and depth

Radius is 14dp for panels, 12dp for buttons and fields, 10dp inside controls. Only the avatar is
a circle. Depth comes from a 1dp line, never from shadow.

## Motion

Press feedback is a quick 97% scale. A mood change swaps the face with a short crossfade. The face
blinks where it is the focus (profile, picker selection). Nothing floats, breathes or drifts.
Everything that moves respects the system animation scale.

## Language and direction

English and Persian, switchable in the app. Layout uses start and end, never left and right, so
Persian mirrors correctly. Copy is plain: say what the button does, no exclamation marks, no
em dashes, no hype words.

## What is deliberately absent

No mascot, no speech bubble, no stat counters, no "feelings" arrows on tiles, no gradient button.
