#!/bin/bash
# Bulk-seed the `post` table with large synthetic data for filtering/sorting benchmarks.
# Run from the test-api project root (needs docker compose up -d first).
set -euo pipefail

cd "$(dirname "$0")/.."

CONTAINER=$(docker compose ps -q postgres)
DB=testdb
DB_USER=postgres
BATCH_SIZE=${BATCH_SIZE:-1000000}
BATCHES=${BATCHES:-10}

psql() { docker exec -i "$CONTAINER" psql -U "$DB_USER" -d "$DB" "$@"; }

BOARD_IDS=$(psql -t -A -c "select array_to_string(array_agg(id), ',') from board;")
USER_IDS=$(psql -t -A -c "select array_to_string(array_agg(id), ',') from users;")

if [ -z "$BOARD_IDS" ] || [ -z "$USER_IDS" ]; then
  echo "board/users table is empty — run the app once so DataInitializer seeds them first." >&2
  exit 1
fi

echo "seeding $((BATCH_SIZE * BATCHES)) posts into $DB (board_type in {$BOARD_IDS}, user_id in {$USER_IDS})"

for i in $(seq 0 $((BATCHES - 1))); do
  OFFSET=$((i * BATCH_SIZE))
  START=$((OFFSET + 1))
  END=$((OFFSET + BATCH_SIZE))
  echo "batch $i: rows $START..$END $(date)"
  psql -c "
    INSERT INTO post (title, content, created_at, updated_at, deleted_at, board_type, user_id)
    SELECT
      left('title-' || i, 20),
      -- ponytail: random per-chunk md5, not repeat() — repeated patterns get TOAST-compressed
      -- away and undershoot the target on-disk size
      (SELECT string_agg(md5(random()::text || i::text || g::text), '') FROM generate_series(1,63) g),
      now() - (random() * interval '730 days'),
      now() - (random() * interval '730 days'),
      NULL,
      (ARRAY[$BOARD_IDS])[((i-1) % array_length(ARRAY[$BOARD_IDS], 1)) + 1],
      (ARRAY[$USER_IDS])[((i-1) % array_length(ARRAY[$USER_IDS], 1)) + 1]
    FROM generate_series($START, $END) AS i;
  "
  psql -t -c "select pg_size_pretty(pg_total_relation_size('post')), count(*) from post;"
done

echo "done: $(date)"
