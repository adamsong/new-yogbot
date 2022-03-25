CREATE TABLE IF NOT EXISTS bans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    discord_id BIGINT(20) NOT NULL,
    issued_at DATETIME NOT NULL DEFAULT NOW(),
    expires_at DATETIME,
    revoked_at DATETIME,
    reason VARCHAR(2048)
);

