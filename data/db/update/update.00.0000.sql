
--
-- create traders table
-- 

CREATE TABLE IF NOT EXISTS trader (
	name VARCHAR(30) NOT NULL,
	symbol VARCHAR(20) DEFAULT NULL,
	position INT DEFAULT 0,
	siggen TINYTEXT NOT NULL,
	class TINYTEXT NOT NULL,
	params TINYTEXT NOT NULL,
	desired INT,
	quantity INT,
	PRIMARY KEY (name),
);

--
-- database schema version table
--

CREATE TABLE IF NOT EXISTS version (
	v INT NOT NULL DEFAULT 0
);

INSERT INTO version VALUES (0);