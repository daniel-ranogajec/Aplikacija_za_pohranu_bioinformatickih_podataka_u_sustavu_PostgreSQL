# Aplikacija za pohranu bioinformatičkih podataka u sustavu PostgreSQL

## FER, Završni rad, 2021. - Daniel Ranogajec

U sklopu završnog rada napravljena je aplikacija u kojoj se mogu pretraživati orga-
nizmi i pregledavati podaci o istima. Organizmima se dodatno mogu spremati i podaci
o genima te sekvence referentnih genoma koje se pohranjuju kao FASTA datoteke na
disku. Završni rad temelji se na dva dijela: baza podataka i aplikacija. U bazi podataka
pohranjeni su svi podaci o organizmima dohvaćeni iz NCBI-eve (Nacionalni centar za
biotehnološke informacije) biblioteke, a putem aplikacije se ti isti podaci prikazuju.
Za implementaciju baze podataka korišten je PostgreSQL sustav, a sama aplikacija je
programirana u jeziku Java uz pomoć Swing biblioteke.

--------------------------------------------------------------------------------------------------------------

Prije korištenja aplikacije, potrebno je napraviti novu PostgreSQL bazu podataka sa proizvoljnim imenom.

Zbog velike količine podataka nije ih bilo moguće povući na git repozitorij.

Zato prije korištenja aplikacije potrebno je otići na link: https://ftp.ncbi.nlm.nih.gov/pub/taxonomy/
i preuzeti taxdmp.zip, taxdump.tar.Z ili taxdump.tar.gz ovisno o operacijskom sustavu koji koristite.
Nakon preuzimanja potrebno je arhivu raspakirati te njene datoteke spremiti u /src/resources/taxdump/ direktorij.

Nakon toga možete krenuti sa korištenjem aplikacije pokretanjem datoteke zavrsni.BioinformaticsApp.java.





