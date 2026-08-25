  FROM postgres:16

  RUN apt-get update \
      && apt-get install -y --no-install-recommends \
          ca-certificates \
          build-essential \
          postgresql-server-dev-16 \
          git \
          libicu-dev \
      && git clone https://github.com/pgbigm/pg_bigm.git /tmp/pg_bigm \
      && cd /tmp/pg_bigm \
      && make USE_PGXS=1 \
      && make USE_PGXS=1 install \
      && cd / \
      && rm -rf /tmp/pg_bigm \
      && apt-get purge -y --auto-remove build-essential git postgresql-server-dev-16 libicu-dev \
      && rm -rf /var/lib/apt/lists/*
