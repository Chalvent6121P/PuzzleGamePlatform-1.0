CREATE DATABASE  IF NOT EXISTS `puzzlegame` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `puzzlegame`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: puzzlegame
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `achievement`
--

DROP TABLE IF EXISTS `achievement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `achievement` (
  `achievement_no` int NOT NULL AUTO_INCREMENT,
  `game_no` int DEFAULT NULL,
  `achievement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condition_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`achievement_no`),
  KEY `game_no` (`game_no`),
  CONSTRAINT `achievement_ibfk_1` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement`
--

LOCK TABLES `achievement` WRITE;
/*!40000 ALTER TABLE `achievement` DISABLE KEYS */;
/*!40000 ALTER TABLE `achievement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ending`
--

DROP TABLE IF EXISTS `ending`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ending` (
  `ending_no` int NOT NULL AUTO_INCREMENT,
  `game_no` int NOT NULL,
  `ending_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ending_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ending_no`),
  KEY `game_no` (`game_no`),
  CONSTRAINT `ending_ibfk_1` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ending`
--

LOCK TABLES `ending` WRITE;
/*!40000 ALTER TABLE `ending` DISABLE KEYS */;
INSERT INTO `ending` VALUES (1,1,'成功逃出圖書館','成功','玩家成功取得出口鑰匙並逃出');
/*!40000 ALTER TABLE `ending` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game`
--

DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game` (
  `game_no` int NOT NULL AUTO_INCREMENT,
  `game_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`game_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game`
--

LOCK TABLES `game` WRITE;
/*!40000 ALTER TABLE `game` DISABLE KEYS */;
INSERT INTO `game` VALUES (1,'失落的圖書館','簡單','尋找線索逃出圖書館','2026-07-05 21:34:08');
/*!40000 ALTER TABLE `game` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `inventory_no` int NOT NULL AUTO_INCREMENT,
  `record_no` int NOT NULL,
  `item_no` int NOT NULL,
  `get_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_no`),
  KEY `record_no` (`record_no`),
  KEY `item_no` (`item_no`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`record_no`) REFERENCES `player_game_record` (`record_no`),
  CONSTRAINT `inventory_ibfk_2` FOREIGN KEY (`item_no`) REFERENCES `item` (`item_no`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,3,1,'2026-07-05 22:32:42'),(2,3,2,'2026-07-05 22:32:42'),(3,3,3,'2026-07-05 22:32:43'),(4,4,1,'2026-07-05 23:18:04'),(5,4,2,'2026-07-05 23:18:04'),(6,5,1,'2026-07-05 23:22:29'),(7,5,2,'2026-07-05 23:22:30'),(8,5,3,'2026-07-05 23:22:30'),(9,6,1,'2026-07-05 23:58:54'),(10,6,2,'2026-07-05 23:59:15'),(11,6,3,'2026-07-05 23:59:45'),(12,7,1,'2026-07-06 09:20:14'),(13,7,2,'2026-07-06 09:20:41'),(14,7,3,'2026-07-06 09:20:55'),(15,10,1,'2026-07-06 09:29:39'),(16,11,1,'2026-07-06 09:29:50'),(17,14,1,'2026-07-06 11:19:02'),(18,14,2,'2026-07-06 11:19:41'),(19,14,3,'2026-07-06 11:19:56'),(20,20,1,'2026-07-06 17:02:27'),(21,20,2,'2026-07-06 17:02:41'),(22,20,3,'2026-07-06 17:02:53'),(23,22,1,'2026-07-06 17:10:37'),(24,22,2,'2026-07-06 17:10:40'),(25,22,3,'2026-07-06 17:11:02'),(26,23,2,'2026-07-06 17:25:14'),(27,23,3,'2026-07-06 17:25:21');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `item_no` int NOT NULL AUTO_INCREMENT,
  `game_no` int NOT NULL,
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`item_no`),
  KEY `game_no` (`game_no`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,1,'紙條','提示','真正答案藏在時間裡'),(2,1,'小鑰匙','鑰匙','可以打開抽屜'),(3,1,'出口鑰匙','鑰匙','可以打開出口門');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `player_no` int NOT NULL AUTO_INCREMENT,
  `player_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`player_no`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES (1,'測試者01','001','001','2026-07-05 22:27:02'),(2,'測試者02','002','002','2026-07-06 17:08:18');
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_achievement`
--

DROP TABLE IF EXISTS `player_achievement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_achievement` (
  `player_achievement_no` int NOT NULL AUTO_INCREMENT,
  `player_no` int NOT NULL,
  `achievement_no` int NOT NULL,
  `unlock_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`player_achievement_no`),
  KEY `player_no` (`player_no`),
  KEY `achievement_no` (`achievement_no`),
  CONSTRAINT `player_achievement_ibfk_1` FOREIGN KEY (`player_no`) REFERENCES `player` (`player_no`),
  CONSTRAINT `player_achievement_ibfk_2` FOREIGN KEY (`achievement_no`) REFERENCES `achievement` (`achievement_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_achievement`
--

LOCK TABLES `player_achievement` WRITE;
/*!40000 ALTER TABLE `player_achievement` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_achievement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_game_record`
--

DROP TABLE IF EXISTS `player_game_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_game_record` (
  `record_no` int NOT NULL AUTO_INCREMENT,
  `player_no` int NOT NULL,
  `game_no` int NOT NULL,
  `current_room_no` int DEFAULT NULL,
  `current_puzzle_no` int DEFAULT NULL,
  `ending_no` int DEFAULT NULL,
  `progress_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未完成',
  `result_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未完成',
  `current_step` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`record_no`),
  KEY `player_no` (`player_no`),
  KEY `game_no` (`game_no`),
  KEY `current_room_no` (`current_room_no`),
  KEY `current_puzzle_no` (`current_puzzle_no`),
  KEY `ending_no` (`ending_no`),
  CONSTRAINT `player_game_record_ibfk_1` FOREIGN KEY (`player_no`) REFERENCES `player` (`player_no`),
  CONSTRAINT `player_game_record_ibfk_2` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`),
  CONSTRAINT `player_game_record_ibfk_3` FOREIGN KEY (`current_room_no`) REFERENCES `room` (`room_no`),
  CONSTRAINT `player_game_record_ibfk_4` FOREIGN KEY (`current_puzzle_no`) REFERENCES `puzzle` (`puzzle_no`),
  CONSTRAINT `player_game_record_ibfk_5` FOREIGN KEY (`ending_no`) REFERENCES `ending` (`ending_no`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_game_record`
--

LOCK TABLES `player_game_record` WRITE;
/*!40000 ALTER TABLE `player_game_record` DISABLE KEYS */;
INSERT INTO `player_game_record` VALUES (2,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-05 22:27:14',NULL),(3,1,1,NULL,NULL,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-05 22:32:42','2026-07-05 22:32:43'),(4,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-05 23:18:04',NULL),(5,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-05 23:22:29','2026-07-05 23:22:30'),(6,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-05 23:58:51','2026-07-05 23:59:47'),(7,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-06 09:20:11','2026-07-06 09:20:56'),(8,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 09:21:32',NULL),(9,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 09:21:47',NULL),(10,1,1,NULL,NULL,NULL,'進行中','未完成','玩家取得紙條','2026-07-06 09:29:38',NULL),(11,1,1,NULL,NULL,NULL,'進行中','未完成','玩家取得紙條','2026-07-06 09:29:47',NULL),(12,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 11:14:27',NULL),(13,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 11:18:20',NULL),(14,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-06 11:18:48','2026-07-06 11:19:57'),(15,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 11:21:34',NULL),(17,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 14:17:52',NULL),(18,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 14:20:11',NULL),(19,1,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 14:42:33',NULL),(20,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-06 17:02:20','2026-07-06 17:02:55'),(21,2,1,NULL,NULL,NULL,'進行中','未完成','玩家開始遊戲','2026-07-06 17:09:08',NULL),(22,2,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-06 17:10:31','2026-07-06 17:11:03'),(23,1,1,NULL,2,1,'完成','成功','玩家取得出口鑰匙並成功逃出圖書館','2026-07-06 17:25:01','2026-07-06 17:25:22');
/*!40000 ALTER TABLE `player_game_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puzzle`
--

DROP TABLE IF EXISTS `puzzle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puzzle` (
  `puzzle_no` int NOT NULL AUTO_INCREMENT,
  `game_no` int NOT NULL,
  `room_no` int DEFAULT NULL,
  `puzzle_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `correct_answer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `puzzle_order` int DEFAULT NULL,
  PRIMARY KEY (`puzzle_no`),
  KEY `game_no` (`game_no`),
  KEY `room_no` (`room_no`),
  CONSTRAINT `puzzle_ibfk_1` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`),
  CONSTRAINT `puzzle_ibfk_2` FOREIGN KEY (`room_no`) REFERENCES `room` (`room_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puzzle`
--

LOCK TABLES `puzzle` WRITE;
/*!40000 ALTER TABLE `puzzle` DISABLE KEYS */;
INSERT INTO `puzzle` VALUES (1,1,NULL,'百科全書頁碼','815','時鐘停在 8:15',1),(2,1,NULL,'密碼盒密碼','2580','抽屜裡藏著密碼',2);
/*!40000 ALTER TABLE `puzzle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puzzle_record`
--

DROP TABLE IF EXISTS `puzzle_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puzzle_record` (
  `puzzle_record_no` int NOT NULL AUTO_INCREMENT,
  `record_no` int NOT NULL,
  `puzzle_no` int NOT NULL,
  `input_answer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_correct` tinyint(1) DEFAULT '0',
  `answer_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`puzzle_record_no`),
  KEY `record_no` (`record_no`),
  KEY `puzzle_no` (`puzzle_no`),
  CONSTRAINT `puzzle_record_ibfk_1` FOREIGN KEY (`record_no`) REFERENCES `player_game_record` (`record_no`),
  CONSTRAINT `puzzle_record_ibfk_2` FOREIGN KEY (`puzzle_no`) REFERENCES `puzzle` (`puzzle_no`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puzzle_record`
--

LOCK TABLES `puzzle_record` WRITE;
/*!40000 ALTER TABLE `puzzle_record` DISABLE KEYS */;
INSERT INTO `puzzle_record` VALUES (3,4,2,'815',0,'2026-07-05 23:18:04'),(4,5,1,'815',1,'2026-07-05 23:22:29'),(5,5,2,'2580',1,'2026-07-05 23:22:30'),(6,6,1,'815',1,'2026-07-05 23:59:29'),(7,6,2,'2580',1,'2026-07-05 23:59:44'),(8,7,1,'815',1,'2026-07-06 09:20:44'),(9,7,2,'2580',1,'2026-07-06 09:20:55'),(10,14,1,'815',1,'2026-07-06 11:19:34'),(11,14,2,'2580',1,'2026-07-06 11:19:56'),(12,20,1,'815',1,'2026-07-06 17:02:39'),(13,20,2,'2580',1,'2026-07-06 17:02:53'),(14,22,1,'815',1,'2026-07-06 17:10:50'),(15,22,2,'2580',1,'2026-07-06 17:11:02'),(16,23,1,'815',1,'2026-07-06 17:25:13'),(17,23,2,'2580',1,'2026-07-06 17:25:21');
/*!40000 ALTER TABLE `puzzle_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `room_no` int NOT NULL AUTO_INCREMENT,
  `game_no` int NOT NULL,
  `room_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_order` int DEFAULT NULL,
  PRIMARY KEY (`room_no`),
  KEY `game_no` (`game_no`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `save_game`
--

DROP TABLE IF EXISTS `save_game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `save_game` (
  `save_no` int NOT NULL AUTO_INCREMENT,
  `record_no` int NOT NULL,
  `player_no` int NOT NULL,
  `game_no` int NOT NULL,
  `save_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `save_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `save_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`save_no`),
  KEY `record_no` (`record_no`),
  KEY `player_no` (`player_no`),
  KEY `game_no` (`game_no`),
  CONSTRAINT `save_game_ibfk_1` FOREIGN KEY (`record_no`) REFERENCES `player_game_record` (`record_no`),
  CONSTRAINT `save_game_ibfk_2` FOREIGN KEY (`player_no`) REFERENCES `player` (`player_no`),
  CONSTRAINT `save_game_ibfk_3` FOREIGN KEY (`game_no`) REFERENCES `game` (`game_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `save_game`
--

LOCK TABLES `save_game` WRITE;
/*!40000 ALTER TABLE `save_game` DISABLE KEYS */;
/*!40000 ALTER TABLE `save_game` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_player_game_record`
--

DROP TABLE IF EXISTS `vw_player_game_record`;
/*!50001 DROP VIEW IF EXISTS `vw_player_game_record`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_player_game_record` AS SELECT 
 1 AS `record_no`,
 1 AS `player_name`,
 1 AS `game_name`,
 1 AS `room_name`,
 1 AS `puzzle_name`,
 1 AS `ending_name`,
 1 AS `progress_status`,
 1 AS `result_status`,
 1 AS `current_step`,
 1 AS `start_time`,
 1 AS `end_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_player_game_record`
--

/*!50001 DROP VIEW IF EXISTS `vw_player_game_record`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_player_game_record` AS select `r`.`record_no` AS `record_no`,`p`.`player_name` AS `player_name`,`g`.`game_name` AS `game_name`,`rm`.`room_name` AS `room_name`,`pu`.`puzzle_name` AS `puzzle_name`,`e`.`ending_name` AS `ending_name`,`r`.`progress_status` AS `progress_status`,`r`.`result_status` AS `result_status`,`r`.`current_step` AS `current_step`,`r`.`start_time` AS `start_time`,`r`.`end_time` AS `end_time` from (((((`player_game_record` `r` join `player` `p` on((`r`.`player_no` = `p`.`player_no`))) join `game` `g` on((`r`.`game_no` = `g`.`game_no`))) left join `room` `rm` on((`r`.`current_room_no` = `rm`.`room_no`))) left join `puzzle` `pu` on((`r`.`current_puzzle_no` = `pu`.`puzzle_no`))) left join `ending` `e` on((`r`.`ending_no` = `e`.`ending_no`))) where (`r`.`player_no` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-06 17:37:00


-- ================================================================
-- Phase 1 upgrade appended below
-- ================================================================
-- ================================================================
-- PuzzleGamePlatform Phase 1 升級腳本
-- 適用：既有 puzzlegame 資料庫（MySQL 8.x）
-- 內容：玩家角色/狀態、管理員帳號、五款遊戲、房間、謎題、結局、成就
-- 注意：此腳本設計為執行一次。
-- ================================================================

USE puzzlegame;

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE player
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'PLAYER' AFTER password,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER role,
    ADD COLUMN last_login_time DATETIME NULL AFTER create_time;

ALTER TABLE game
    ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER description,
    ADD COLUMN cover_image_path VARCHAR(255) NULL AFTER is_active;

UPDATE player
SET role = 'PLAYER', status = 'ACTIVE'
WHERE role IS NULL OR role = '';

INSERT INTO player
    (player_name, account, password, role, status)
VALUES
    ('系統管理員', 'admin', 'Admin@1234', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    player_name = VALUES(player_name),
    role = 'ADMIN',
    status = 'ACTIVE';

INSERT INTO game
    (game_no, game_name, difficulty, description, is_active, cover_image_path)
VALUES
    (1, '失落的圖書館', '簡單',
     '尋找時間、書頁與鑰匙的線索，逃出古老圖書館。', 1,
     '/images/library/library_scene.png'),
    (2, '逆行的鐘塔', '普通',
     '破解逆向運轉的時間機關，逃離重複的午夜。', 1,
     '/images/games/clock_tower.png'),
    (3, '霧鎖病棟', '普通',
     '在白霧籠罩的病棟中找回病歷、藥櫃與電梯權限。', 1,
     '/images/games/hospital.png'),
    (4, '沉沒實驗室', '困難',
     '重啟海底實驗室的氧氣、能源與逃生艙。', 1,
     '/images/games/laboratory.png'),
    (5, '鏡廳旅館', '困難',
     '找回被鏡子奪走的房號、姓名與記憶。', 1,
     '/images/games/mirror_hotel.png')
ON DUPLICATE KEY UPDATE
    game_name = VALUES(game_name),
    difficulty = VALUES(difficulty),
    description = VALUES(description),
    is_active = VALUES(is_active),
    cover_image_path = VALUES(cover_image_path);

INSERT INTO room
    (room_no, game_no, room_name, description, room_order)
VALUES
    (1, 1, '古老藏書室', '放滿古籍、時鐘、地球儀與上鎖家具的圖書館。', 1),
    (2, 2, '逆行鐘塔主機室', '齒輪、鏡面鐘盤與午夜校準裝置。', 1),
    (3, 3, '封鎖病棟', '病歷櫃、藥櫃與被霧封鎖的電梯。', 1),
    (4, 4, '海底控制艙', '氧氣、能源與逃生艙控制系統。', 1),
    (5, 5, '鏡廳大堂', '無數鏡子映出不同年代的旅館住客。', 1)
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    room_name = VALUES(room_name),
    description = VALUES(description),
    room_order = VALUES(room_order);

UPDATE puzzle SET room_no = 1 WHERE puzzle_no IN (1, 2);

INSERT INTO puzzle
    (puzzle_no, game_no, room_no, puzzle_name,
     correct_answer, hint, puzzle_order)
VALUES
    (3, 2, 2, '鏡面鐘盤', '0220', '將 12:00 減去鏡面時間 21:40。', 1),
    (4, 2, 2, '齒輪序列', '48', '數列每次乘以 2。', 2),
    (5, 2, 2, '午夜校準', '268', '將 220 與 48 相加。', 3),

    (6, 3, 3, '缺頁病歷', '116', '104、108、112，每次增加 4。', 1),
    (7, 3, 3, '藥櫃標籤', '312', 'A=1、B=2、C=3，CAB。', 2),
    (8, 3, 3, '電梯權限', '28', '取 116 與 312 的末兩位相加。', 3),

    (9, 4, 4, '氧氣配比', '32', '2、4、8、16，每次加倍。', 1),
    (10, 4, 4, '能源矩陣', '56', '計算 7×8。', 2),
    (11, 4, 4, '逃生艙座標', '88', '將 32 與 56 相加。', 3),

    (12, 5, 5, '倒置房號', '816', '將鏡中的 618 反向排列。', 1),
    (13, 5, 5, '失落姓名', 'MIRROR', '13、9、18、18、15、18 對應英文字母。', 2),
    (14, 5, 5, '主鏡封印', '15', '計算 8+1+6。', 3)
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    room_no = VALUES(room_no),
    puzzle_name = VALUES(puzzle_name),
    correct_answer = VALUES(correct_answer),
    hint = VALUES(hint),
    puzzle_order = VALUES(puzzle_order);

INSERT INTO ending
    (ending_no, game_no, ending_name, ending_type, description)
VALUES
    (1, 1, '成功逃出圖書館', '成功', '取得出口鑰匙並逃出圖書館。'),
    (2, 2, '黎明鐘聲', '成功', '修復主鐘並逃離時間迴圈。'),
    (3, 3, '霧散之時', '成功', '恢復電梯權限並離開病棟。'),
    (4, 4, '海面曙光', '成功', '啟動逃生艙並離開海底基地。'),
    (5, 5, '保住姓名的人', '成功', '封印主鏡並離開旅館。')
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    ending_name = VALUES(ending_name),
    ending_type = VALUES(ending_type),
    description = VALUES(description);

INSERT INTO achievement
    (achievement_no, game_no, achievement_name, description, condition_text)
VALUES
    (1, 1, '圖書館逃脫者', '成功逃出失落的圖書館。', '完成遊戲 1'),
    (2, 2, '時間修復者', '讓逆行鐘塔恢復正向運轉。', '完成遊戲 2'),
    (3, 3, '霧中生還者', '成功離開霧鎖病棟。', '完成遊戲 3'),
    (4, 4, '深海逃生者', '從沉沒實驗室搭乘逃生艙離開。', '完成遊戲 4'),
    (5, 5, '鏡界守名者', '封印主鏡並保住自己的名字。', '完成遊戲 5')
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    achievement_name = VALUES(achievement_name),
    description = VALUES(description),
    condition_text = VALUES(condition_text);

ALTER TABLE inventory
    ADD CONSTRAINT uq_inventory_record_item
    UNIQUE (record_no, item_no);

ALTER TABLE player_achievement
    ADD CONSTRAINT uq_player_achievement
    UNIQUE (player_no, achievement_no);

DROP VIEW IF EXISTS vw_player_game_record;
CREATE VIEW vw_player_game_record AS
SELECT
    r.record_no,
    r.player_no,
    p.player_name,
    p.account,
    g.game_no,
    g.game_name,
    rm.room_name,
    pu.puzzle_name,
    e.ending_name,
    r.progress_status,
    r.result_status,
    r.current_step,
    r.start_time,
    r.end_time
FROM player_game_record r
JOIN player p ON r.player_no = p.player_no
JOIN game g ON r.game_no = g.game_no
LEFT JOIN room rm ON r.current_room_no = rm.room_no
LEFT JOIN puzzle pu ON r.current_puzzle_no = pu.puzzle_no
LEFT JOIN ending e ON r.ending_no = e.ending_no;

SET FOREIGN_KEY_CHECKS = 1;

-- 預設管理員測試帳號：
-- account: admin
-- password: Admin@1234
-- 正式交付前請立即修改密碼。
