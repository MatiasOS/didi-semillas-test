\connect semillas;
CREATE SCHEMA security;

-- Table: security.role

-- DROP TABLE security.role;

CREATE TABLE security.role
(
    id SERIAL PRIMARY KEY,
    code character varying(50) COLLATE pg_catalog."default" NOT NULL,
    description character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT uk_security_role_code UNIQUE (code)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE security.role
    OWNER to semillas;

-- Table: security.users

-- DROP TABLE security.users;

CREATE TABLE security.users
(
    id SERIAL PRIMARY KEY,
    active boolean NOT NULL,
    email character varying(255) COLLATE pg_catalog."default",
    last_name character varying(150) COLLATE pg_catalog."default",
    name character varying(150) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    phone character varying(50) COLLATE pg_catalog."default",
    username character varying(50) COLLATE pg_catalog."default" NOT NULL,
    id_role integer NOT NULL,
    CONSTRAINT fk_security_user_role FOREIGN KEY (id_role)
        REFERENCES security.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE security.users
    OWNER to semillas;