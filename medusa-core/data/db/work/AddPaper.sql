CREATE PROCEDURE AddPaper(
	IN sym VARCHAR(20),
	IN quan INT,
	IN des INT
)

MODIFIES SQL DATA
COMMENT 'Add paper to the wallet'

BEGIN

	CALL CreatePaper(sym);

	INSERT INTO 
		wallet
	VALUES 
		(sym, des, quan) 
	ON DUPLICATE KEY UPDATE 
		desired = des, 
		quantity = quan;
END
