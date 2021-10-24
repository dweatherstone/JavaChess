USE ChessGames;
DROP PROCEDURE IF EXISTS `spAddGameMoves`;

DELIMITER //
USE ChessGames //

CREATE PROCEDURE `spAddGameMoves` (
	IN insertMoveSql varchar(15000)
)
BEGIN

	SET @insertMoveSql = insertMoveSql;

	PREPARE stmt FROM @insertMoveSql;

	EXECUTE stmt;
	
END //

DELIMITER ;