-- Insert Admin:
INSERT INTO public.person(dtype, id, email, first_name, password, surname, user_salt)VALUES ('Admin', 1, 'admin@example.com', 'admin', 'TPLX2hGXrTT+XPUZdrT1uJKZjvI=', 'admin', 'CrGiyF7zfHY=');
--Insert Doctors:
INSERT INTO public.person(dtype, id, email, first_name, surname, password,  user_salt) VALUES ('Doctor', 2, 'doctor1@example.com', 'Doctor1Vorname', 'Doctor1Nachname', 'Dx9spQK5BPEI4yp3IDEmfrAExvA=+/eb9dc=', 'FKc1pBLh6Ec=');
INSERT INTO public.person(dtype, id, email, first_name, surname, password,  user_salt) VALUES ('Doctor', 3, 'doctor2@example.com', 'Doctor2Vorname', 'Doctor2Nachname', '32mAVHmCZwqC3SkxGqjEw2uvZWc=', 'Li9tDMzKp8w=');
