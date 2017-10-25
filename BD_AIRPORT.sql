-- MySQL dump 10.13  Distrib 5.7.19, for Linux (x86_64)
--
-- Host: localhost    Database: BD_AIRPORT
-- ------------------------------------------------------
-- Server version	5.7.19-0ubuntu0.16.04.1

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
-- Table structure for table `AGENTS`
--

DROP TABLE IF EXISTS `AGENTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AGENTS` (
  `numID` int(11) NOT NULL,
  `nom` varchar(20) DEFAULT NULL,
  `prenom` varchar(20) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  `numTelephone` int(10) DEFAULT NULL,
  `adresse` varchar(50) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`numID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AGENTS`
--

LOCK TABLES `AGENTS` WRITE;
/*!40000 ALTER TABLE `AGENTS` DISABLE KEYS */;
INSERT INTO `AGENTS` VALUES (100105,'Martin','Daniel','Directeur',484831825,'Rue de la fraternité 5/10','daniel.martin@student.hepl.be');
/*!40000 ALTER TABLE `AGENTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BAGAGES`
--

DROP TABLE IF EXISTS `BAGAGES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BAGAGES` (
  `numBillet` varchar(20) NOT NULL,
  `numBagage` varchar(20) DEFAULT NULL,
  `poids` float NOT NULL,
  `typeBagage` varchar(20)
  PRIMARY KEY (`numBagage`),
  CONSTRAINT `pfk_numbillet` FOREIGN KEY (`numBillet`) REFERENCES `BILLETS` (`numBillet`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BAGAGES`
--

LOCK TABLES `BAGAGES` WRITE;
/*!40000 ALTER TABLE `BAGAGES` DISABLE KEYS */;
/*!40000 ALTER TABLE `BAGAGES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BILLETS`
--

DROP TABLE IF EXISTS `BILLETS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BILLETS` (
  `nom` varchar(20) DEFAULT NULL,
  `prenom` varchar(20) DEFAULT NULL,
  `numID` int(11) DEFAULT NULL,
  `numBillet` varchar(20) NOT NULL,
  `numVol` int(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`numBillet`),
  KEY `fk_numVol` (`numVol`),
  CONSTRAINT `fk_numVol` FOREIGN KEY (`numVol`) REFERENCES `VOLS` (`numVol`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BILLETS`
--

LOCK TABLES `BILLETS` WRITE;
/*!40000 ALTER TABLE `BILLETS` DISABLE KEYS */;
/*!40000 ALTER TABLE `BILLETS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VOLS`
--

DROP TABLE IF EXISTS `VOLS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VOLS` (
  `destination` varchar(20) DEFAULT NULL,
  `heureDepart` date DEFAULT NULL,
  `heureArriveePrevue` date DEFAULT NULL,
  `heureArriveeReelle` date DEFAULT NULL,
  `idAvion` int(10) DEFAULT NULL,
  `numVol` int(5) NOT NULL,
  PRIMARY KEY (`numVol`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VOLS`
--

LOCK TABLES `VOLS` WRITE;
/*!40000 ALTER TABLE `VOLS` DISABLE KEYS */;
/*!40000 ALTER TABLE `VOLS` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-10 11:08:18
