INSERT INTO security.role (id, code, description) 
VALUES (1, 'ADMIN', 'Admin role');

INSERT INTO security.users (
id, active, email, last_name, name, password, phone, username, id_role) VALUES (
1, true, 'admin@semillas.com', 'semillas', 'administrador', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', null, 'admin', 1)
 returning id;


--INSERT INTO security.credentials
--(id, credential_name) VALUES
--(1, "credencial crediticia")
-- returning id;