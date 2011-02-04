CREATE PROCEDURE AddTrader(
	IN i_symbol VARCHAR(20),
	IN i_name VARCHAR(30)
)

MODIFIES SQL DATA
COMMENT 'Add instance of Trader to DB'

BEGIN

	CREATE TABLE IF NOT EXISTS trader (
		name VARCHAR(30) NOT NULL,
		symbol VARCHAR(20) NOT NULL,
		PRIMARY KEY (name)
	)
	
	INSERT INTO 
		trader (
			name, 
			symbol
		)
	VALUES (
		i_name, 
		i_symbol
	);
END
