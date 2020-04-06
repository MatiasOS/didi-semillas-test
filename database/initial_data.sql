INSERT INTO security.role (id, code, description) 
VALUES (1, 'ADMIN', 'Admin role');

INSERT INTO security.users (
id, active, email, last_name, name, password, phone, username, id_role) VALUES (
1, true, 'admin@semillas.com', 'semillas', 'administrador', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', null, 'admin', 1);

INSERT INTO public.person (
id, birth_date, document_number, document_type, name) VALUES (
'1'::bigint, '1999-04-21'::date, '454645687'::bigint, 'jjty'::character varying(255), 'Jorge Perez'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number, document_type, name) VALUES (
'2'::bigint, '1999-04-21'::date, '456456455'::bigint, 'jjty'::character varying(255), 'Tomas Gipson'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number, document_type, name) VALUES (
'3'::bigint, '1999-05-25'::date, '547547547'::bigint, 'type'::character varying(255), 'Rodolfo Rodriguez'::character varying(255));

INSERT INTO public.credential (
id, date_of_expiry, date_of_issue, id_didi_credential, id_didi_issueer, id_didi_receptor, beneficiary_id, id_credential, credential_state, credential_type) VALUES (
'1'::bigint, '2020-03-28'::timestamp without time zone, '2020-03-28'::timestamp without time zone, '1'::bigint, '1'::bigint, '1'::bigint, '1'::bigint, '1'::bigint, 'Vigente'::character varying(255), 'CredentialCredit'::character varying(255));

INSERT INTO public.credential (
id, date_of_expiry, date_of_issue, id_didi_credential, id_didi_issueer, id_didi_receptor, beneficiary_id, id_credential, credential_state,credential_type) VALUES (
'2'::bigint, '2020-03-28'::timestamp without time zone, '2020-03-28'::timestamp without time zone, '2'::bigint, '2'::bigint, '2'::bigint, '1'::bigint, '2'::bigint, 'Revocada'::character varying(255), 'CredentialCredit'::character varying(255));

INSERT INTO public.credential (
id, credential_state, date_of_expiry, date_of_issue, id_didi_credential, id_didi_issueer, id_didi_receptor, beneficiary_id, id_credential, credential_type) VALUES (
'3'::bigint, 'Vigente'::character varying(255), '2020-03-29 00:00:00'::timestamp without time zone, '2020-03-28'::timestamp without time zone, '3'::bigint, '3'::bigint, '3'::bigint, '2'::bigint, '3'::bigint, 'CredentialEntrepreneurship'::character varying(255));

INSERT INTO public.credential_credit (
amount, credit_name, credit_state, dni_beneficiary, group_name, id_credit, id_group, rol, id) VALUES (
'10000'::double precision, 'credito'::character varying(255), 'Vigente'::character varying(255), '4353456345'::bigint, 'grupo'::character varying(255), '1'::bigint, '1'::bigint, 'rol'::character varying(255), '1'::bigint);

INSERT INTO public.credential_credit (
amount, credit_name, credit_state, dni_beneficiary, group_name, id_credit, id_group, rol, id) VALUES (
'20000'::double precision, 'credito2'::character varying(255), 'Vigente'::character varying(255), '468767'::bigint, 'grupo'::character varying(255), '1'::bigint, '1'::bigint, 'rol'::character varying(255), '2'::bigint);

INSERT INTO public.credential_entrepreneurship (
address_entrepreneurship, credit_state, end_activity, entrepreneurship_type, main_activity, name_entrepreneurship, start_activity, id) VALUES (
'calle 1'::character varying(255), 'Vigente'::character varying(255), '2020-04-21'::timestamp without time zone, 'Tipo'::character varying(255), 'Administracion'::character varying(255), 'Rodolfo Rodriguez'::character varying(255), '2020-01-20'::timestamp without time zone, '3'::bigint);
