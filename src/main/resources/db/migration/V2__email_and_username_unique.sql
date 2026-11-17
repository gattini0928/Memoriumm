ALTER TABLE users
ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
ADD CONSTRAINT uk_users_username UNIQUE (username);

ALTER TABLE users
ALTER COLUMN email TYPE VARCHAR(150);