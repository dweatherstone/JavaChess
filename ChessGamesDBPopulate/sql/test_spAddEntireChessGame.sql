START TRANSACTION;

CALL spAddEntireChessGame(
	"London m5",
    "London",
    "1862.??.??",
    "?",
    "Paulsen, Louis",
    "Mackenzie, George Henry",
    "1-0",
    "",
    "",
    "C51",
    @game_id
    );


SELECT * FROM Game;

SELECT * FROM Move;

SELECT @game_id;

ROLLBACK;
-- COMMIT;
