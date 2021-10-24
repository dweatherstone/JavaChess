USE ChessGames;
DROP PROCEDURE IF EXISTS `spAddEntireChessGame`;

DELIMITER //
USE ChessGames //
CREATE PROCEDURE spAddEntireChessGame(
	IN event_name VARCHAR(45),
    IN site VARCHAR(45),
    IN date_char VARCHAR(45),
    IN round_char VARCHAR(45),
    IN white_name VARCHAR(255),
    IN black_name VARCHAR(255),
    IN result VARCHAR(45),
    IN white_elo VARCHAR(45),
    IN black_elo VARCHAR(45),
    IN eco VARCHAR(10),
    OUT game_id INT
)
BEGIN

	declare white_last_name varchar(45);
    declare white_first_name varchar(45);
    declare white_player_id int;
    declare black_last_name varchar(45);
    declare black_first_name varchar(45);
    declare black_player_id int;
    declare date_date date;
    declare winner char(1);

	SET white_last_name = TRIM(SUBSTRING_INDEX(white_name, ",", 1)),
			white_first_name = TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(white_name,',',2),',',-1)); 
            
	-- debug
	SELECT white_first_name, white_last_name;
            
	INSERT IGNORE INTO Player (FirstName, LastName)
    VALUES (white_first_name, white_last_name);
    
    SET   white_player_id = (
		SELECT Player_ID
		FROM Player
		WHERE FirstName = white_first_name
		AND   LastName = white_last_name
	);
    
    -- debug
    SELECT white_player_id;
    
    SET black_last_name = TRIM(SUBSTRING_INDEX(black_name, ",", 1)),
			black_first_name = TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(black_name,',',2),',',-1)); 
            
	INSERT IGNORE INTO Player (FirstName, LastName)
    VALUES (black_first_name, black_last_name);
    
    SET black_player_id = (
		SELECT Player_ID
		FROM Player
		WHERE FirstName = black_first_name
		AND   LastName = black_last_name
	);
    
    -- Sort out the date
    SET date_date = str_to_date(REPLACE(date_char, "??", "01"), "%Y.%m.%d");
    
    IF result = "1-0" THEN
		SET winner = 'W';
	ELSEIF result = "0-1" THEN
		SET winner = 'B';
	ELSEIF result = "1/2-1/2" THEN
		SET winner = 'D';
	END IF;
    
    INSERT INTO Game (Event, Site, Date, Round, White, Black, Winner, WhiteELO, BlackELO, ECO)
    VALUES (event_name, 
			site, 
            date_date, 
            nullif(round_char, '?'),
            white_player_id,
            black_player_id,
            winner,
            cast(nullif(nullif(white_elo, ''), '?') as unsigned),
            cast(nullif(nullif(black_elo, ''), '?') as unsigned),
            eco
            );
            
	SET game_id = LAST_INSERT_ID();
    
    
    
END //

DELIMITER ;
