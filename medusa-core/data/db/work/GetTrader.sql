CREATE PROCEDURE GetTrader(
	IN i_name VARCHAR(30)
)

READS SQL DATA
COMMENT 'Get instance of Trader from DB'

BEGIN
	SELECT 
		*
	FROM
		trader 
	WHERE
		name = i_name
	LIMIT 1;
END
