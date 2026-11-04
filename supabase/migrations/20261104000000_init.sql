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

-- ---------------------------------------------------------------------------
-- connections: a pair of users, at most one row per pair
-- id is the two uids sorted and joined with "_" (same scheme as before)
-- ---------------------------------------------------------------------------
create table public.connections (
  id           text primary key,
  user_a       uuid not null references auth.users (id) on delete cascade,
  user_b       uuid not null references auth.users (id) on delete cascade,
  requester_id uuid not null references auth.users (id) on delete cascade,
  status       text not null default 'PENDING' check (status in ('PENDING', 'ACCEPTED')),
  created_at   bigint not null default (extract(epoch from now()) * 1000)::bigint,
  constraint connections_sorted   check (user_a < user_b),
  constraint connections_id_match check (id = user_a::text || '_' || user_b::text),
  constraint connections_requester_is_member check (requester_id in (user_a, user_b))
);
