CREATE TABLE `Player` (
	`Player_ID` int NOT NULL AUTO_INCREMENT,
    `FirstName` varchar(45) DEFAULT NULL,
    `LastName` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`Player_ID`),
    UNIQUE KEY `uq_Player_FirstName_LastName` (`FirstName`,`LastName`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
