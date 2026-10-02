# Prump 2.0 — peer interview practice bot

A Java Telegram bot for arranging peer mock interviews and tracking coding practice.
The bot has a Russian-language interface and uses PostgreSQL for persistent data.

This is a learning project. The implementation was restored from the repository's
`dev` branch so that the default branch contains source code and a reproducible
build instead of compiled artifacts.

## Features in the source

- Registration with Telegram and LeetCode profiles.
- Creating, joining and cancelling peer interview sessions.
- LeetCode task selection and scheduled interview reminders.
- Participant feedback, ratings, XP and leagues.
- Administrator statistics and topic configuration.

These describe the existing implementation, not a guarantee of live service
availability. This repository does not provide a hosted public bot.

## Architecture

```text
Telegram updates → Bot / keyboards → services → DAOs → PostgreSQL
                         │
                         ├── LeetCode GraphQL requests
                         └── scheduled reminders and practice checks
```

| Area | Implementation |
| --- | --- |
| Language and build | Java 17, Maven, executable shaded JAR |
| Telegram | TelegramBots long polling |
| Storage | PostgreSQL, JDBC, HikariCP |
| Schema changes | Flyway migrations in `src/main/resources/db/migration` |
| External integration | LeetCode GraphQL, Java HTTP client, Jackson |
| Local deployment | Docker Compose with separate bot and database containers |

## Run with Docker Compose

1. Create your own bot with [BotFather](https://core.telegram.org/bots/features#botfather).
2. Copy the configuration template:

   ```sh
   cp .env.example .env
   ```

3. Edit `.env`: set `BOT_TOKEN`, `BOT_USERNAME` (without `@`) and a new, unique
   `DB_PASSWORD`. The file is ignored by Git and excluded from the Docker build.
4. Build and start:

   ```sh
   docker compose up --build -d
   docker compose logs -f bot
   ```

The bot uses long polling, so no inbound HTTP port is needed. The database is
accessible to the bot on the Compose network and is not published on the host.
Flyway applies migrations when the application starts. Open your own bot in
Telegram and send `/start`.

Stop the services with `docker compose down`. Database contents remain in the
named volume. Changing `.env` does not change the password of an existing
PostgreSQL database; update that database's credentials separately when needed.

## Build and run with Java

Requirements: JDK 17 or later and Maven 3.9. To run the application, also provide
an accessible PostgreSQL 15 database and Telegram credentials.

```sh
mvn --batch-mode --no-transfer-progress clean verify
```

The build and configuration tests do not start the bot or connect to Telegram or
PostgreSQL. The executable is written to `target/algo_train.jar`.

To run it, create an empty database, configure its address and credentials in your
local `.env`, and export the variables. For a POSIX shell, use shell-compatible
values in that file and run:

```sh
set -a
. ./.env
set +a
java -jar target/algo_train.jar
```

Java reads environment variables directly; it does not load `.env` automatically.

| Variable | Purpose |
| --- | --- |
| `BOT_TOKEN` | Required token for your own Telegram bot |
| `BOT_USERNAME` | Required Telegram bot username, without `@` |
| `DB_URL` | Required JDBC URL for native Java execution; Compose sets its internal URL |
| `DB_USERNAME` | Required database username; Compose defaults to `prump` |
| `DB_PASSWORD` | Required database password |
| `TZ` | Compose timezone; defaults to `Europe/Moscow` |

## Project layout

```text
src/main/java/org/example/
  Bot.java             Telegram handlers and conversation state
  config/              Environment validation, database and topic configuration
  dao/                 Database access
  service/             User and interview operations
  model/               User, interview and rating models
  multithreading/      Scheduled reminders and practice checks
  util/                LeetCode integration and task selection
src/main/resources/db/migration/
src/test/java/         Isolated configuration tests
```

## Current limitations

- Automated tests currently cover environment configuration. Interview flows,
  scheduling, database operations and Telegram/LeetCode integration still need
  dedicated tests.
- Conversation state is kept in process memory; the current design is intended
  for one bot instance.
- The scheduler, external API failures and permission checks need further review
  before public deployment.
- LeetCode integration depends on an external GraphQL interface. No latency,
  throughput or availability claims are made.

## Credentials and repository history

Never commit a real `.env`, bot token, database password or compiled build output.
Use `.env.example` as the configuration template.

Earlier revisions of this repository contained credentials. Treat every such
credential as exposed and revoke or rotate it before using this project. Removing
files or rewriting Git history does not revoke credentials or remove copies from
other clones.

## License

[MIT](LICENSE).
