-- V1__create_users.sql

CREATE TABLE IF NOT EXISTS Users (
  user_id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
  username VARCHAR(191) NOT NULL UNIQUE,  -- 191 is a common length for utf8mb4
  password VARCHAR(255) NOT NULL,
  email VARCHAR(191) NOT NULL UNIQUE,      -- 191 for utf8mb4
  total_points INT DEFAULT 0,
  status ENUM('online', 'offline', 'in_match') DEFAULT 'offline',  
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
