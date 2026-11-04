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

create index connections_user_a_idx on public.connections (user_a);
create index connections_user_b_idx on public.connections (user_b);

-- ---------------------------------------------------------------------------
-- Invite codes: 6 chars, no 0/O/1/I (mirrors InviteCode.kt)
-- ---------------------------------------------------------------------------
create or replace function public.generate_invite_code()
returns text
language plpgsql
set search_path = ''
as $$
declare
  alphabet constant text := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
  code text;
  i int;
begin
  loop
    code := '';
    for i in 1..6 loop
      code := code || substr(alphabet, 1 + floor(random() * length(alphabet))::int, 1);
    end loop;
    exit when not exists (select 1 from public.profiles where invite_code = code);
  end loop;
  return code;
end;
$$;

-- ---------------------------------------------------------------------------
-- Create the profile automatically when someone signs up.
-- Works even when "Confirm email" is on (no session exists yet at that point).
-- ---------------------------------------------------------------------------
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles (id, display_name, email, invite_code)
  values (
    new.id,
    coalesce(new.raw_user_meta_data ->> 'display_name', ''),
    coalesce(new.email, ''),
    public.generate_invite_code()
  );
  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- ---------------------------------------------------------------------------
-- Look up a user by invite code without exposing the whole profiles table.
-- ---------------------------------------------------------------------------
create or replace function public.find_user_by_code(code text)
returns table (id uuid)
language sql
stable
security definer
set search_path = ''
as $$
  select p.id from public.profiles p where p.invite_code = upper(trim(code)) limit 1;
$$;

revoke all on function public.find_user_by_code(text) from public, anon;
grant execute on function public.find_user_by_code(text) to authenticated;

-- ---------------------------------------------------------------------------
-- Row level security
-- ---------------------------------------------------------------------------
alter table public.profiles    enable row level security;
alter table public.connections enable row level security;

-- You can see your own profile and the profile of anyone you share a
-- connection (pending or accepted) with. Emails are not world-readable.
create policy "profiles: read own or connected"
  on public.profiles for select to authenticated
  using (
    id = (select auth.uid())
    or exists (
      select 1 from public.connections c
      where (c.user_a = (select auth.uid()) and c.user_b = profiles.id)
         or (c.user_b = (select auth.uid()) and c.user_a = profiles.id)
    )
  );

create policy "profiles: update own"
  on public.profiles for update to authenticated
  using (id = (select auth.uid()))
  with check (id = (select auth.uid()));

create policy "connections: read mine"
  on public.connections for select to authenticated
  using ((select auth.uid()) in (user_a, user_b));

create policy "connections: request as myself"
  on public.connections for insert to authenticated
  with check (
    (select auth.uid()) in (user_a, user_b)
    and requester_id = (select auth.uid())
    and status = 'PENDING'
  );

-- Only the person who was asked may accept.
create policy "connections: recipient accepts"
  on public.connections for update to authenticated
  using ((select auth.uid()) in (user_a, user_b) and requester_id <> (select auth.uid()))
  with check (status = 'ACCEPTED');

create policy "connections: either side removes"
  on public.connections for delete to authenticated
  using ((select auth.uid()) in (user_a, user_b));
