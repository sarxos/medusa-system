CREATE PROCEDURE UpdatePaper(
	IN symbol VARCHAR(20),
	IN quantity INT,
	IN desired INT
)

MODIFIES SQL DATA
COMMENT 'Update given symbol in the wallet'

BEGIN
	UPDATE
		wallet AS w
	SET
		w.desired = desired, 
		w.quantity = quantity 
	WHERE
		w.symbol = symbol;
END


