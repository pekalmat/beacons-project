-- Insert Admin:
INSERT INTO public.person(dtype, id, email, first_name, password, surname)VALUES ('Admin', 1, 'admin@example.com', 'admin', '$2a$10$h.YGVRBWRudZ6NyAqdvAOuIVMQO3OuKdP9D0iyMyEXMrpCqN8UsBq', 'admin');
--Insert Doctors:
INSERT INTO public.person(dtype, id, email, first_name, surname, password) VALUES ('Doctor', 2, 'doctor1@example.com', 'Hans', 'Peter', '$2a$10$zOzC8A.0NySiVWFyUCUVK.PRisIVg1UwGS0qCa0ealow5AeeiiCFy');
INSERT INTO public.person(dtype, id, email, first_name, surname, password) VALUES ('Doctor', 3, 'doctor2@example.com', 'Hans', 'Jörg', '$2a$10$RmXW38gs7EzzuU/ONmKBFe8WSmNcmXwBsD9CkzVxewWA33/4oEeP2');
-- Insert Beacon1 and Bed1 and Patient 1
INSERT INTO public.beacon(id, major, minor, uuid) VALUES (1, '10002', '9571', '01122334-4556-6778-899a-abbccddeeff0');
INSERT INTO public.bed(id, beacon_id) VALUES (1, 1);
UPDATE public.beacon  SET bed_id = 1 WHERE id=1;
INSERT INTO public.patient(id, ahv_nr, first_name, surname, bed_id) VALUES (1, '1234-5132-521', 'Hans', 'Wurst', 1);
UPDATE public.bed  SET patient_id = 1 WHERE id=1;
-- Insert Beacon2 and Bed2 and Patient 2
INSERT INTO public.beacon(id, major, minor, uuid) VALUES (2, '10002', '9586', '01122334-4556-6778-899a-abbccddeeff0');
INSERT INTO public.bed(id, beacon_id) VALUES (2, 2);
UPDATE public.beacon  SET bed_id = 2 WHERE id=2;
INSERT INTO public.patient(id, ahv_nr, first_name, surname, bed_id) VALUES (2, '1234-5132-521', 'Hans', 'Peter', 1);
UPDATE public.bed  SET patient_id = 2 WHERE id=2;


-- update all sequences to start with 100
SELECT setval('beacon_id_seq', 100, true);
SELECT setval('bed_id_seq', 100, true);
SELECT setval('patient_id_seq', 100, true);
SELECT setval('treatment_id_seq', 100, true);