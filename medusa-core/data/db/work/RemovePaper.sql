CREATE PROCEDURE RemovePaper(
	IN sym VARCHAR(20)
)

MODIFIES SQL DATA
COMMENT 'Remove given symbol from the wallet'

BEGIN
	DELETE
	FROM
		wallet
	WHERE
		symbol = sym;
END


