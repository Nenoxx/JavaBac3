-- Table Vols
delete from VOLS;

insert into VOLS (destination, heureDepart, heureArriveePrevue, idAvion, numVol) values ('Bamaco', STR_TO_DATE('10-10-2017 11:32', '%d-%m-%Y %H:%i'), STR_TO_DATE('10-10-2017 17:45', '%d-%m-%Y %H:%i'), 747, 601);
insert into VOLS (destination, heureDepart, heureArriveePrevue, idAvion, numVol) values ('New York', STR_TO_DATE('10-10-2017 09:10', '%d-%m-%Y %H:%i'), STR_TO_DATE('10-10-2017 19:10', '%d-%m-%Y %H:%i'), 380, 007);
insert into VOLS (destination, heureDepart, heureArriveePrevue, idAvion, numVol) values ('Paris', STR_TO_DATE('11-10-2017 15:07', '%d-%m-%Y %H:%i'), STR_TO_DATE('11-10-2017 16:30', '%d-%m-%Y %H:%i'), 707, 111);
insert into VOLS (destination, heureDepart, heureArriveePrevue, idAvion, numVol) values ('Los Angeles', STR_TO_DATE('12-10-2017 04:22', '%d-%m-%Y %H:%i'), STR_TO_DATE('12-10-2017 22:50', '%d-%m-%Y %H:%i'), 320, 666);
-- 4 entrées

-- Table Billets
delete from BILLETS;

insert into BILLETS values ('Martin', 'Daniel', 10001, CONCAT(CAST("601" AS CHAR) ,'-', '1'), 601);
insert into BILLETS values ('Zanzero', 'Arnaud', 10002, CONCAT(CAST("111" AS CHAR) ,'-', '1'), 111);
insert into BILLETS values ('Belmondo', 'Jean-Paul', 10003, CONCAT(CAST("666" AS CHAR) ,'-', '1'), 666);
insert into BILLETS values ('Bond', 'James', 10004, CONCAT(CAST("007" AS CHAR) ,'-', '1'), 007);
insert into BILLETS values ('Francois', 'Claude', 10005, CONCAT(CAST("601" AS CHAR) ,'-', '2'), 601);
insert into BILLETS values ('Lawrence', 'Jennifer', 10006, CONCAT(CAST("111" AS CHAR) ,'-', '2'), 111);
insert into BILLETS values ('Robbie', 'Margot', 10007, CONCAT(CAST("666" AS CHAR) ,'-', '2'), 666);
insert into BILLETS values ('Watson', 'Emma', 10008, CONCAT(CAST("007" AS CHAR) ,'-', '2'), 007);
insert into BILLETS values ('Jolie', 'Angelina', 10009, CONCAT(CAST("601" AS CHAR) ,'-', '3'), 601);
insert into BILLETS values ('Stone', 'Emma', 10010, CONCAT(CAST("111" AS CHAR) ,'-', '3'), 111);
insert into BILLETS values ('Fox', 'Megan', 10011, CONCAT(CAST("666" AS CHAR) ,'-', '3'), 666);
insert into BILLETS values ('Johansson', 'Scarlett', 10012, CONCAT(CAST("007" AS CHAR) ,'-', '3'), 007);
insert into BILLETS values ('Kunis', 'Mila', 10013, CONCAT(CAST("601" AS CHAR) ,'-', '4'), 601);
-- 13 entrées

-- Table Bagages

drop table BAGAGES;

CREATE TABLE BAGAGES (
  numBillet varchar(20) NOT NULL,
  numBagage varchar(20) PRIMARY KEY,
  poids float NOT NULL,
  typeBagage varchar(20),
  CONSTRAINT pfk_numbillet FOREIGN KEY (numBillet) REFERENCES BILLETS (numBillet)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into BAGAGES values('601-1', '601-1-1', 18.1, 'Valise');
insert into BAGAGES values('601-2', '601-2-1', 10.1, 'Valise');
insert into BAGAGES values('601-3', '601-3-1', 19.9, 'Valise');
insert into BAGAGES values('601-4', '601-4-1', 21.2, 'Autre');
insert into BAGAGES values('111-1', '111-1-1', 15.4, 'Valise');
insert into BAGAGES values('111-1', '111-1-2', 31.1, 'Autre');
insert into BAGAGES values('111-2', '111-2-1', 18.5, 'Valise');
insert into BAGAGES values('111-3', '111-3-1', 12.1, 'Autre');
insert into BAGAGES values('007-1', '007-1-1', 9.0, 'Valise');
insert into BAGAGES values('007-2', '007-2-1', 18.7, 'Valise');
insert into BAGAGES values('007-3', '007-3-1', 17.7, 'Valise');
insert into BAGAGES values('666-1', '666-1-1', 16.6, 'Autre');
insert into BAGAGES values('666-2', '666-2-1', 15.5, 'Valise');
insert into BAGAGES values('666-3', '666-3-1', 14.8, 'Valise');
insert into BAGAGES values('666-3', '666-3-2', 19.5, 'Autre');
-- 15 entrées

-- Table Agents
delete from AGENTS;

insert into AGENTS values (20001, 'Damiens', 'Francois', 'Bagagiste', 0478123123, 'Rue du Poulet 28, 4000 Liège', 'francois.damiens@gmail.com');
insert into AGENTS values (20002, 'Focan', 'Claudy', 'Aiguilleur', 0495789456, 'Rue des Oeufs 11, 4000 Liège', 'claudy.focan@gmail.com');
insert into AGENTS values (20003, 'Michel', 'Michel', 'Agent', 0475456456, 'Rue de la Poule 1, 4000 Liège', 'mich-mich@hotmail.com');
insert into AGENTS values (20004, 'Onyme', 'Anne', 'Agent', 0475784521, 'Rue des Poussins 34, 4000 Liège', 'anneonyme@hotmail.com');
-- 4 entrées

