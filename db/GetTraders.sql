CREATE PROCEDURE GetTraders()

READS SQL DATA
COMMENT 'Get all Trader instance from DB'

BEGIN
	SELECT 
		*
	FROM
		trader 
	WHERE
		name = i_name;
END
