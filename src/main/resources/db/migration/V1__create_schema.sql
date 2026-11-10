
CREATE TABLE users (
    uuid UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    email VARCHAR(300) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(300) NOT NULL,

    profile_picture TEXT,
    bio VARCHAR(300),

    created_at TIMESTAMP,
    privacy VARCHAR(30)
);

CREATE TABLE user_followers (
    following_id UUID REFERENCES users(uuid),
    follower_id UUID REFERENCES users(uuid),

    PRIMARY KEY (following_id, follower_id)
);

CREATE TABLE memories (
    uuid UUID PRIMARY KEY,

    user_id UUID NOT NULL REFERENCES users(uuid),

    title VARCHAR(100) NOT NULL,
    photo VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,

    visibility VARCHAR(30),
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP
);

CREATE TABLE user_favorites (
    user_id UUID REFERENCES users(uuid),
    memory_id UUID REFERENCES memories(uuid),

    PRIMARY KEY (user_id, memory_id)
);

CREATE TABLE memory_likes (
    user_id UUID REFERENCES users(uuid),
    memory_id UUID REFERENCES memories(uuid),

    PRIMARY KEY (user_id, memory_id)
);

CREATE TABLE memory_autumn_hypes (
    user_id UUID REFERENCES users(uuid),
    memory_id UUID REFERENCES memories(uuid),

    PRIMARY KEY (user_id, memory_id)
);

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,

    icon VARCHAR(10),
    user_id UUID REFERENCES users(uuid),

    name VARCHAR(50) NOT NULL,
    visibility VARCHAR(30),

    UNIQUE (user_id, name)
);

CREATE TABLE memory_tags (
    memory_id UUID REFERENCES memories(uuid),
    tag_id INTEGER REFERENCES tags(id),

    PRIMARY KEY (memory_id, tag_id)
);

CREATE TABLE comments (
    uuid UUID PRIMARY KEY,

    user_id UUID NOT NULL REFERENCES users(uuid),
    memory_id UUID NOT NULL REFERENCES memories(uuid),

    text VARCHAR(500) NOT NULL,
    created_at TIMESTAMP
);
