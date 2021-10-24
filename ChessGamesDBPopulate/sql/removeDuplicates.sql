SELECT Event, Site, Date, Round, White, Black, Winner, WhiteELO, BlackELO, ECO, COUNT(*) AS Qty, MIN(Game_ID) AS MinGameId, MAX(Game_ID) AS MaxGameId
FROM Game
GROUP BY Event, Site, Date, Round, White, Black, Winner, WhiteELO, BlackELO, ECO
HAVING COUNT(*) > 1
ORDER BY Qty DESC
LIMIT 100;

SELECT Game_ID FROM Game
WHERE Event = 'Breslau m'
AND Site = 'Breslau'
AND Date = '1865-01-01'
AND Round IS NULL
AND White = 50842
AND Black = 50763
AND Winner = 'W'
AND WhiteELO IS NULL
AND BlackELO IS NULL
AND ECO = 'C37';

START TRANSACTION;

DELETE FROM Move WHERE Game_ID IN (
60302,
60303,
60304,
60306,
266006,
266008,
266009,
266010,
266011);

SELECT ROW_COUNT();

DELETE FROM Game
WHERE Game_ID IN (
60302,
60303,
60304,
60306,
266006,
266008,
266009,
266010,
266011);

SELECT ROW_COUNT();

SELECT *
FROM Game
WHERE Event = 'Breslau m'
AND Site = 'Breslau'
AND Date = '1865-01-01'
AND Round IS NULL
AND White = 50842
AND Black = 50763
AND Winner = 'W'
AND WhiteELO IS NULL
AND BlackELO IS NULL
AND ECO = 'C37';

ROLLBACK;
COMMIT;

DROP TEMPORARY TABLE IF EXISTS Game_Id_To_Delete;

CREATE TEMPORARY TABLE Game_Id_To_Delete (
	Game_ID int,
    PRIMARY KEY(Game_ID),
    INDEX(Game_ID)
);

INSERT INTO Game_Id_To_Delete (Game_ID)
SELECT g.Game_ID
FROM   Game g
INNER JOIN (
	SELECT Event, Site, Date, Round, White, Black, Winner, WhiteELO, BlackELO, ECO, COUNT(*) AS Qty, MIN(Game_ID) AS MinGameId, MAX(Game_ID) AS MaxGameId
	FROM Game
	GROUP BY Event, Site, Date, Round, White, Black, Winner, WhiteELO, BlackELO, ECO
	HAVING COUNT(*) > 1
) t
ON ifnull(g.Event, 'zzz') = ifnull(t.Event, 'zzz')
AND ifnull(g.Site, 'zzz') = ifnull(t.Site, 'zzz')
AND ifnull(g.Date, '2999-12-31') = ifnull(t.Date, '2999-12-31')
AND ifnull(g.Round, 'zzz') = ifnull(t.Round, 'zzz')
AND ifnull(g.White, -1) = ifnull(t.White, -1)
AND ifnull(g.Black, -1) = ifnull(t.Black, -1)
AND ifnull(g.Winner, 'W') = ifnull(t.Winner, 'W')
AND ifnull(g.WhiteELO, -1) = ifnull(t.WhiteELO, -1)
AND ifnull(g.BlackELO, -1) = ifnull(t.BlackELO, -1)
AND ifnull(g.ECO, 'zzz') = ifnull(t.ECO, 'zzz')
AND g.Game_ID != t.MinGameId
GROUP BY g.Game_ID;

SELECT COUNT(*) AS 'Should be 130136' FROM Game_Id_To_Delete;

-- START TRANSACTION;

DELETE FROM Move
WHERE Game_ID IN (SELECT Game_ID FROM Game_Id_To_Delete);

SELECT row_count();

DELETE FROM Game
WHERE Game_ID IN (SELECT Game_ID FROM Game_Id_To_Delete);

SELECT row_count() AS 'Should be 130136';

-- ROLLBACK;