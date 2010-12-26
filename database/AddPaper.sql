CREATE PROCEDURE AddPaper(
	IN sym VARCHAR(20),
	IN quan INT,
	IN des INT
)

MODIFIES SQL DATA
COMMENT 'Add paper to the wallet'

BEGIN

	SET @s = CONCAT(
		"CREATE TABLE IF NOT EXISTS ", sym, " ( ",
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

	INSERT INTO 
		wallet
	VALUES 
		(sym, des, quan) 
	ON DUPLICATE KEY UPDATE 
		desired = des, 
		quantity = quan;
END



