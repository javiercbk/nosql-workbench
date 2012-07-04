-- MySQL dump 10.13  Distrib 5.5.23, for osx10.6 (i386)
--
-- Host: localhost    Database: sube
-- ------------------------------------------------------
-- Server version	5.5.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `card_usages`
--

DROP TABLE IF EXISTS `card_usages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `card_usages` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_provider` bigint(20) unsigned NOT NULL,
  `provider_type` tinyint(3) unsigned NOT NULL,
  `money` double NOT NULL,
  `id_card` bigint(20) unsigned NOT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_responsable` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_service_provider_card_service_usage` (`id_provider`),
  KEY `fk_card_card_service_usage` (`id_card`),
  KEY `idx_cu_provider` (`id_provider`),
  KEY `idx_cu_card` (`id_card`),
  KEY `idx_cu_date` (`datetime`),
  KEY `idx_responsable` (`id_responsable`),
  CONSTRAINT `fk_card_card_service_usage` FOREIGN KEY (`id_card`) REFERENCES `cards` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_usages`
--

LOCK TABLES `card_usages` WRITE;
/*!40000 ALTER TABLE `card_usages` DISABLE KEYS */;
/*!40000 ALTER TABLE `card_usages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cards` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `balance` double unsigned NOT NULL,
  `createdBy` int(10) unsigned NOT NULL,
  `id_user` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_data_entry_card` (`createdBy`),
  KEY `fk_user_card` (`id_user`),
  CONSTRAINT `fk_user_card` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_data_entry_card` FOREIGN KEY (`createdBy`) REFERENCES `data_entries` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashier_providers`
--

DROP TABLE IF EXISTS `cashier_providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cashier_providers` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(100) NOT NULL,
  `id_person` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_legal_person_cashier_provider` (`id_person`),
  KEY `idx_cp_name` (`name`),
  CONSTRAINT `fk_legal_person_cashier_provider` FOREIGN KEY (`id_person`) REFERENCES `legal_persons` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashier_providers`
--

LOCK TABLES `cashier_providers` WRITE;
/*!40000 ALTER TABLE `cashier_providers` DISABLE KEYS */;
/*!40000 ALTER TABLE `cashier_providers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_entries`
--

DROP TABLE IF EXISTS `data_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_entries` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `password` varchar(45) NOT NULL,
  `id_person` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_physical_person_data_entry` (`id_person`),
  CONSTRAINT `fk_physical_person_data_entry` FOREIGN KEY (`id_person`) REFERENCES `physical_persons` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_entries`
--

LOCK TABLES `data_entries` WRITE;
/*!40000 ALTER TABLE `data_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_types`
--

DROP TABLE IF EXISTS `document_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_types` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type_name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_name_UNIQUE` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_types`
--

LOCK TABLES `document_types` WRITE;
/*!40000 ALTER TABLE `document_types` DISABLE KEYS */;
INSERT INTO `document_types` VALUES (1,'DNI'),(2,'LE'),(3,'PAS');
/*!40000 ALTER TABLE `document_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `legal_persons`
--

DROP TABLE IF EXISTS `legal_persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `legal_persons` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `legal_name` varchar(45) NOT NULL,
  `fantasy_name` varchar(45) NOT NULL,
  `cuit` int(10) unsigned NOT NULL,
  `location` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cuit_UNIQUE` (`cuit`),
  UNIQUE KEY `legal_name_UNIQUE` (`legal_name`),
  UNIQUE KEY `idx_lp_legal_name` (`legal_name`),
  KEY `idx_lp_fantasy_name` (`fantasy_name`),
  KEY `idx_lp_cuit` (`cuit`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `legal_persons`
--

LOCK TABLES `legal_persons` WRITE;
/*!40000 ALTER TABLE `legal_persons` DISABLE KEYS */;
/*!40000 ALTER TABLE `legal_persons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `physical_persons`
--

DROP TABLE IF EXISTS `physical_persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `physical_persons` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `doc_num` bigint(20) NOT NULL,
  `id_doc_type` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pp_doc_num_type` (`doc_num`,`id_doc_type`),
  KEY `fk_physical_person_doc_type` (`id_doc_type`),
  KEY `idx_pp_first_name` (`first_name`),
  KEY `idx_pp_last_name` (`last_name`),
  KEY `idx_pp_doc_num` (`doc_num`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `physical_persons`
--

LOCK TABLES `physical_persons` WRITE;
/*!40000 ALTER TABLE `physical_persons` DISABLE KEYS */;
/*!40000 ALTER TABLE `physical_persons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_providers`
--

DROP TABLE IF EXISTS `service_providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_providers` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(100) NOT NULL,
  `id_person` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_legal_person_service_provider` (`id_person`),
  KEY `idx_sp_name` (`name`),
  CONSTRAINT `fk_legal_person_service_provider` FOREIGN KEY (`id_person`) REFERENCES `legal_persons` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_providers`
--

LOCK TABLES `service_providers` WRITE;
/*!40000 ALTER TABLE `service_providers` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_providers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_card` bigint(20) unsigned DEFAULT NULL,
  `id_person` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_physical_person_user` (`id_person`),
  KEY `fk_card_user` (`id_card`),
  CONSTRAINT `fk_card_user` FOREIGN KEY (`id_card`) REFERENCES `cards` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_physical_person_user` FOREIGN KEY (`id_person`) REFERENCES `physical_persons` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-06-25  2:35:59
