
CREATE TYPE suggestion_status AS ENUM ('new', 'in process', 'accepted', 'declined');

CREATE TABLE IF NOT EXISTS users
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mail        VARCHAR(255) NOT NULL UNIQUE,
    nickname    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS projects
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id        UUID NOT NULL REFERENCES users (id),
    name            TEXT NOT NULL,
    description     TEXT,
    vote_interval   INTERVAL NOT NULL DEFAULT '1 day'
);

CREATE TABLE IF NOT EXISTS project_rights
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    project_id      UUID NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    is_admin        BOOL NOT NULL DEFAULT false,
    votes_amount    INTEGER NOT NULL DEFAULT 1,
    UNIQUE (user_id, project_id)
);

CREATE TABLE IF NOT EXISTS drafts
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    project_id      UUID NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    last_edit       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    name            TEXT NOT NULL,
    description     TEXT,
);

CREATE TABLE IF NOT EXISTS suggestions
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users (id),
    project_id      UUID NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    placed_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_edit       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    name            TEXT NOT NULL,
    description     TEXT,
    status          suggestion_status NOT NULL DEFAULT 'new'
);

CREATE TABLE IF NOT EXISTS likes
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    suggestion_id   UUID NOT NULL REFERENCES suggestions (id) ON DELETE CASCADE,
    placed_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS comments
(
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users (id),
    suggestion_id       UUID NOT NULL REFERENCES suggestions (id) ON DELETE CASCADE,
    comment_reply_to_id UUID REFERENCES comments (id),
    placed_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_edit           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    text                TEXT NOT NULL
);
