CREATE PROCEDURE AddTrader(
	IN i_name VARCHAR(30),
	IN i_symbol VARCHAR(20),
	IN i_siggen TINYTEXT,
	IN i_params TINYTEXT
)

MODIFIES SQL DATA
COMMENT 'Add instance of Trader to DB'

BEGIN

	CREATE TABLE IF NOT EXISTS trader (
		name VARCHAR(30) NOT NULL,
		symbol VARCHAR(20) DEFAULT NULL,
		siggen TINYTEXT NOT NULL,
		params TINYTEXT NOT NULL,
		PRIMARY KEY (name)
	);
	
	INSERT INTO 
		trader 
	VALUES (
		i_name, 
		i_symbol, 
		i_siggen, 
		i_params
	)
	ON DUPLICATE KEY UPDATE
		name = i_name,
		symbol = i_symbol, 
		siggen = i_siggen, 
		params = i_params;
END
