CREATE PROCEDURE RemoveTrader(
	IN i_name VARCHAR(30)
)

MODIFIES SQL DATA
COMMENT 'Remove instance of Trader from DB'

BEGIN
	DELETE FROM
		trader 
	WHERE
		name = i_name;
END
