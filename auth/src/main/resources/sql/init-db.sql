DROP USER IF EXISTS "auth_user"@"localhost";
CREATE USER "auth_user"@"localhost" IDENTIFIED BY "Auth@123";

DROP DATABASE IF EXISTS auth;
CREATE DATABASE auth;

GRANT ALL PRIVILEGES ON auth.* TO "auth_user"@"localhost";

USE auth;

CREATE TABLE user (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

INSERT INTO user (email, password) VALUE("user@gmail.com", "Password");

