-- V2__create_friends.sql

CREATE TABLE IF NOT EXISTS Friends (
  user_id CHAR(36) NOT NULL,
  friend_id CHAR(36) NOT NULL,
  status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (friend_id) REFERENCES Users(user_id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;