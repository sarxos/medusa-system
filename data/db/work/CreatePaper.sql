CREATE PROCEDURE CreatePaper(
	IN i_symbol VARCHAR(20)
)

MODIFIES SQL DATA
COMMENT 'Create paper in the database'

BEGIN
	SET @s = CONCAT(
		"CREATE TABLE IF NOT EXISTS ", i_symbol, " ( ",
		"    time DATE NOT NULL PRIMARY KEY, ",
		"    open FLOAT NOT NULL, ",
		"    high FLOAT NOT NULL, ",
		"    low FLOAT NOT NULL, ",
		"    close FLOAT NOT NULL, ",
		"    volume BIGINT NOT NULL ",
		")"
	);
	PREPARE stmt FROM @s;
	EXECUTE stmt;
END
