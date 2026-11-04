-- MoodLink schema for Supabase (Postgres).
-- Run in the Supabase SQL editor, or with `supabase db push`.

-- ---------------------------------------------------------------------------
-- profiles: one row per auth user, holds the shared mood
-- ---------------------------------------------------------------------------
create table public.profiles (
  id              uuid primary key references auth.users (id) on delete cascade,
  display_name    text   not null default '',
  email           text   not null default '',
  invite_code     text   not null unique,
  mood_id         text,
  note            text   not null default '',
  mood_updated_at bigint not null default 0,
  created_at      timestamptz not null default now()
);
