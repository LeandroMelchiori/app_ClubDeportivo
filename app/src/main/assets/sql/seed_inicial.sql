-- ===============================
-- ACTIVIDADES (id_actividad, nombre, precio)
-- ===============================
INSERT OR IGNORE INTO actividades (nombre, precio) VALUES
('Fútbol', 8000.00),
('Básquet', 8500.00),
('Vóley', 7000.00),
('Yoga', 6500.00),
('CrossFit', 9500.00),
('Natación Adultos', 9000.00);
('GAP', 5000.00);
('Funcional', 5000.00);
('Spinning', 5000.00);

-- ===============================
-- PROFESORES (dni PK, nombre, apellido, fecha_nac, telefono, direccion, fecha_inscripcion, ficha_medica, email, activo, titulo)
-- ===============================
INSERT OR IGNORE INTO profesores
(dni, nombre, apellido, fecha_nac, telefono, direccion, fecha_inscripcion, ficha_medica, email, activo, titulo) VALUES
('20123456','Juan','Pérez','1988-04-12','3415551111','San Martín 123, Rosario','2025-01-10',1,'juan.perez@club.com',1,'Prof. Ed. Física'),
('22333444','María','Giménez','1990-09-02','3415552222','Mendoza 456, Rosario','2025-01-15',1,'maria.gimenez@club.com',1,'Instructora de Yoga'),
('27999888','Diego','Sosa','1985-07-22','3415553333','Sarmiento 789, Rosario','2025-01-20',1,'diego.sosa@club.com',1,'Entrenador de Futbol'),
('25444777','Lucía','Benítez','1992-03-18','3415554444','Oroño 321, Rosario','2025-01-22',1,'lucia.benitez@club.com',1,'Entrenadora de Natación'),
('23111222','Agustín','Rossi','1987-11-05','3415555555','Italia 999, Rosario','2025-01-25',1,'agustin.rossi@club.com',1,'Coach CrossFit'),
('20888999','Sofía','Almada','1991-12-01','3415556666','Córdoba 1500, Rosario','2025-02-01',1,'sofia.almada@club.com',1,'Prof. Vóley');

-- ===============================
-- NO_SOCIOS (idNoSocio AI, nombre, apellido, dni UNIQUE, fecha_nac, telefono, email UNIQUE, direccion, fecha_inscripcion, ficha_medica, activo)
-- ===============================
INSERT OR IGNORE INTO no_socios
(nombre, apellido, dni, fecha_nac, telefono, email, direccion, fecha_inscripcion, ficha_medica, activo) VALUES
('Carlos','Ruiz','33111222','1999-05-10','3416000001','carlos.ruiz@gmail.com','Mitre 120, Rosario','2025-03-01',1,1),
('Ana','Martínez','30999888','2001-11-23','3416000002','ana.martinez@gmail.com','Belgrano 450, Rosario','2025-03-02',1,1),
('Matías','Ojeda','28123456','1995-08-14','3416000003','matias.ojeda@gmail.com','Dorrego 980, Rosario','2025-03-03',1,1),
('Camila','Lopez','32123456','2000-02-28','3416000004','camila.lopez@gmail.com','Tucumán 2100, Rosario','2025-03-04',1,1),
('Bruno','Ferreyra','34123456','1998-07-07','3416000005','bruno.ferreyra@gmail.com','Paraguay 300, Rosario','2025-03-05',1,1),
('Valentina','Suárez','35123456','2002-09-19','3416000006','valentina.suarez@gmail.com','Catamarca 750, Rosario','2025-03-06',1,1),
('Ezequiel','Páez','36123456','1997-01-30','3416000007','eze.paez@gmail.com','Urquiza 210, Rosario','2025-03-07',1,1),
('Julieta','Bianchi','37123456','2003-04-22','3416000008','julieta.bianchi@gmail.com','Salta 1750, Rosario','2025-03-08',1,1);
