CREATE PROCEDURE GetPapers() 

READS SQL DATA
COMMENT 'Selects all papers from wallet'

BEGIN
	SELECT 
		*
	FROM
		wallet;
END
