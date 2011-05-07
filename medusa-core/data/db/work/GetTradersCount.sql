CREATE PROCEDURE GetTradersCount()

READS SQL DATA
COMMENT 'Get number of all Trader instances in the DB'

BEGIN
	SELECT count(*) FROM trader;
END
