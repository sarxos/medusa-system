CREATE PROCEDURE AddTrader(
	IN i_name VARCHAR(30),
	IN i_symbol VARCHAR(20),
	IN i_position INT,
	IN i_siggen TINYTEXT,
	IN i_class TINYTEXT,
	IN i_params TINYTEXT
)

MODIFIES SQL DATA
COMMENT 'Add instance of Trader to DB'

BEGIN

	CREATE TABLE IF NOT EXISTS trader (
		name VARCHAR(30) NOT NULL,
		symbol VARCHAR(20) DEFAULT NULL,
		position INT DEFAULT 0,
		siggen TINYTEXT NOT NULL,
		class TINYTEXT NOT NULL,
		params TINYTEXT NOT NULL,
		PRIMARY KEY (name)
	);
	
	INSERT INTO 
		trader 
	VALUES (
		i_name, 
		i_symbol, 
		i_position, 
		i_siggen, 
		i_class,
		i_params
	)
	ON DUPLICATE KEY UPDATE
		name = i_name,
		symbol = i_symbol, 
		position = i_position, 
		siggen = i_siggen,
		class = i_class, 
		params = i_params;
END
