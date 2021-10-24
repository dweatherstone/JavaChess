CREATE TABLE `Move` (
	`Move_ID` int NOT NULL AUTO_INCREMENT,
    `Game_ID` int NOT NULL,
    `MoveNum` int NOT NULL,
    `WhiteMove` varchar(10) NOT NULL,
    `BlackMove` varchar(10) DEFAULT NULL,
    PRIMARY KEY (`Move_ID`),
    UNIQUE KEY `uq_move_game_id_moveNum` (`Game_ID`,`MoveNum`),
    KEY `fk_Move_Game_idx` (`Game_ID`),
    CONSTRAINT `fk_Move_Game` FOREIGN KEY (`Game_ID`) REFERENCES `Game` (`Game_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
