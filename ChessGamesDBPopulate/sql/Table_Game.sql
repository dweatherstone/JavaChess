CREATE TABLE `Game` (
	`Game_ID` int NOT NULL AUTO_INCREMENT,
	`Event` varchar(45) DEFAULT NULL,
	`Site` varchar(45) DEFAULT NULL,
	`Date` date DEFAULT NULL,
	`Round` varchar(45) DEFAULT NULL,
	`White` int NOT NULL,
	`Black` int NOT NULL,
	`Winner` enum('W','B','D') DEFAULT NULL,
	`WhiteELO` int DEFAULT NULL,
	`BlackELO` int DEFAULT NULL,
	`ECO` varchar(10) DEFAULT NULL,
	PRIMARY KEY (`Game_ID`),
	UNIQUE KEY `Game_ID_UNIQUE` (`Game_ID`),
	KEY `NC_White` (`White`),
	KEY `NC_Black` (`Black`),
	KEY `NC_ECO` (`ECO`),
	CONSTRAINT `fk_Game_Black` FOREIGN KEY (`Black`) REFERENCES `Player` (`Player_ID`),
	CONSTRAINT `fk_Game_White` FOREIGN KEY (`White`) REFERENCES `Player` (`Player_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

