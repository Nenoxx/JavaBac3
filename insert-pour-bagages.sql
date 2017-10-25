select * from BAGAGES;

select * from VOLS;

select * from AGENTS;

select * from BILLETS;

alter table AGENTS add column login varchar(20);
alter table AGENTS add column password varchar(20);

alter table BAGAGES add column reception char;
alter table BAGAGES add column charge char;
alter table BAGAGES add column verifie char;
alter table BAGAGES add column remarque varchar(50);

update BAGAGES set remarque = 'neant' where remarque is null;
update BAGAGES set reception = 'N' where reception is null;
update BAGAGES set charge = 'N' where charge is null;
update BAGAGES set verifie = 'N' where verifie is null;

insert into AGENTS values (20005, 'Test', 'Test', 'Bagagiste', 0475784463, 'Rue du lapin 39, 4000 Liège', 'test@hotmail.com', 'test', 'test');

update AGENTS set login = 'Francois', password = 'pwd' where numID = 20001;

commit;
