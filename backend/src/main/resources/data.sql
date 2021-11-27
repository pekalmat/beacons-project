-- Insert Admin:
INSERT INTO public.person(dtype, id, email, first_name, password, surname, user_salt)VALUES ('Admin', 1, 'admin@example.com', 'admin', 'TPLX2hGXrTT+XPUZdrT1uJKZjvI=', 'admin', 'CrGiyF7zfHY=');
--Insert Doctors:
INSERT INTO public.person(dtype, id, email, first_name, surname, password,  user_salt) VALUES ('Doctor', 2, 'doctor1@example.com', 'Doctor1Vorname', 'Doctor1Nachname', 'Dx9spQK5BPEI4yp3IDEmfrAExvA=+/eb9dc=', 'FKc1pBLh6Ec=');
INSERT INTO public.person(dtype, id, email, first_name, surname, password,  user_salt) VALUES ('Doctor', 3, 'doctor2@example.com', 'Doctor2Vorname', 'Doctor2Nachname', '32mAVHmCZwqC3SkxGqjEw2uvZWc=', 'Li9tDMzKp8w=');
-- Insert Beacon1 and Bed1 and Patient 1
INSERT INTO public.beacon(id, major, minor, uid) VALUES (1, '10002', '9571', '01122334-4556-6778-899a-abbccddeeff0');
INSERT INTO public.bed(id, beacon_id) VALUES (1, 1);
UPDATE public.beacon  SET bed_id = 1 WHERE id=1;
INSERT INTO public.patient(id, ahv_nr, first_name, surname, bed_id) VALUES (1, '1234-5132-521', 'Hans', 'Wurst', 1);
UPDATE public.bed  SET patient_id = 1 WHERE id=1;
-- Insert Beacon2 and Bed2 and Patient 2
INSERT INTO public.beacon(id, major, minor, uid) VALUES (2, '10002', '9586', '01122334-4556-6778-899a-abbccddeeff0');
INSERT INTO public.bed(id, beacon_id) VALUES (2, 2);
UPDATE public.beacon  SET bed_id = 2 WHERE id=2;
INSERT INTO public.patient(id, ahv_nr, first_name, surname, bed_id) VALUES (2, '1234-5132-521', 'Hans', 'Peter', 1);
UPDATE public.bed  SET patient_id = 2 WHERE id=2;