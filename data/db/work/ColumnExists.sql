CREATE PROCEDURE ColumnExists(
	IN i_table VARCHAR(32),
	IN i_column VARCHAR(32)
) 

READS SQL DATA
COMMENT 'Check if given column exists within given table'

BEGIN

	SET @col = (
		SELECT 
			count(*)
		FROM
			information_schema.COLUMNS
		WHERE
			TABLE_NAME = i_table AND
			COLUMN_NAME = i_column LIMIT 1
	);
	
	-- TODO something is wrong here - change to function
	
	IF EXISTS (@col) THEN
	BEGIN
		SELECT 1;
		RETURN;
	END IF;
	
	SELECT 0;
	
END


