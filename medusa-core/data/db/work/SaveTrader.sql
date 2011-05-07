CREATE PROCEDURE SaveTrader(
	IN i_name VARCHAR(30),
	IN i_symbol VARCHAR(20),
	IN i_position INT,
	IN i_siggen TINYTEXT,
	IN i_class TINYTEXT,
	IN i_params TINYTEXT,
	IN i_quantity INT,
	IN i_desired INT
)

MODIFIES SQL DATA
COMMENT 'Save instance of Trader in the DB'

BEGIN
	
	INSERT INTO 
		trader 
	VALUES (
		i_name, 
		i_symbol, 
		i_position, 
		i_siggen, 
		i_class,
		i_params,
		i_quantity,
		i_desired
	)
	ON DUPLICATE KEY UPDATE
		name = i_name,
		symbol = i_symbol, 
		position = i_position, 
		siggen = i_siggen,
		class = i_class, 
		params = i_params,
		quantity = i_quantity,
		desired = i_desired;
END
