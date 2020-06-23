INSERT INTO public.credential_state(id, state_name) VALUES (
'1'::bigint, 'Vigente'::character varying(255));

INSERT INTO public.credential_state(id, state_name) VALUES (
'2'::bigint, 'Revocada'::character varying(255));

INSERT INTO public.credential_state(id, state_name) VALUES (
'3'::bigint, 'Pendiente-didi'::character varying(255));


INSERT INTO public.person (
id, birth_date, document_number, first_name, last_name) VALUES (
'6'::bigint, '1999-04-21'::date, '24580962'::bigint, 'Jorge'::character varying(255), 'Gómez'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number,  first_name, last_name) VALUES (
'7'::bigint, '1999-04-21'::date, '24580961'::bigint, 'Tomas'::character varying(255), 'Gipson'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number,  first_name, last_name) VALUES (
'8'::bigint, '1999-05-25'::date, '24580963'::bigint, 'Rodolfo'::character varying(255), 'Rodriguez'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number, first_name, last_name) VALUES (
'4'::bigint, '1999-05-25'::date, '54754754'::bigint, 'Rodolfo'::character varying(255), 'Rodriguez Jr'::character varying(255));

INSERT INTO public.person (
id, birth_date, document_number, first_name, last_name) VALUES (
'5'::bigint, '1999-05-25'::date, '123456'::bigint, 'Rodolfo'::character varying(255), 'Rodriguez Jr'::character varying(255));



INSERT INTO public.credential (
id, date_of_expiry, credential_category, date_of_issue, id_didi_credential, id_didi_issuer, id_didi_receptor, beneficiary_id, id_credential, credential_state_id, credential_status, credential_description) VALUES (
'1'::bigint, '2020-04-23'::timestamp without time zone, 'Finanzas'::character varying(255), '2020-04-08'::timestamp without time zone, '1'::bigint, '1'::bigint, '1'::bigint, '1'::bigint, '1'::bigint, '1'::bigint, ''::character varying(255), 'Creditos Semillas'::character varying(255));

INSERT INTO public.credential (
id, date_of_expiry, credential_category, date_of_issue, id_didi_credential, id_didi_issuer, id_didi_receptor, beneficiary_id, id_credential, credential_state_id,credential_status, credential_description) VALUES (
'2'::bigint, '2020-04-24'::timestamp without time zone, 'Finanzas'::character varying(255), '2020-04-09'::timestamp without time zone, '2'::bigint, '2'::bigint, '2'::bigint, '1'::bigint, '2'::bigint, '2'::bigint, ''::character varying(255), 'Creditos Semillas'::character varying(255));

INSERT INTO public.credential (
id, credential_state_id, credential_category, date_of_expiry, date_of_issue, id_didi_credential, id_didi_issuer, id_didi_receptor, beneficiary_id, id_credential, credential_status, credential_description) VALUES (
'3'::bigint, '1'::bigint, 'Laboral'::character varying(255), '2020-04-15'::timestamp without time zone, '2020-04-10'::timestamp without time zone, '3'::bigint, '3'::bigint, '3'::bigint, '2'::bigint, '3'::bigint, ''::character varying(255), 'Credito Emprendedor'::character varying(255));

INSERT INTO public.credential (
id, credential_description, credential_category, credential_state_id, date_of_expiry, date_of_issue, id_didi_credential, updated, id_didi_issuer, id_didi_receptor, beneficiary_id, credential_status, id_credential) VALUES (
'4'::bigint, 'Identidad - Titular'::character varying(255), 'Identidad'::character varying(255), '1'::bigint, '2020-04-24 00:00:00'::timestamp without time zone, '2020-04-09 00:00:00'::timestamp without time zone, '4'::bigint, '2020-04-08 00:00:00'::timestamp without time zone, '4'::bigint, '4'::bigint, '1'::bigint,  ''::character varying(255),'4'::bigint);



INSERT INTO public.credential_credit (
amount, credit_name, credit_state, dni_beneficiary, group_name, id_credit, id_group, rol, id, total_cycles) VALUES (
'10000'::double precision, 'credito'::character varying(255), 'Vigente'::character varying(255), '4353456345'::bigint, 'grupo'::character varying(255), '1'::bigint, '1'::bigint, 'rol'::character varying(255), '1'::bigint, '12'::bigint);

INSERT INTO public.credential_credit (
amount, credit_name, credit_state, dni_beneficiary, group_name, id_credit, id_group, rol, id, total_cycles) VALUES (
'20000'::double precision, 'credito2'::character varying(255), 'Vigente'::character varying(255), '468767'::bigint, 'grupo'::character varying(255), '1'::bigint, '1'::bigint, 'rol'::character varying(255), '2'::bigint, '12'::bigint);

INSERT INTO public.credential_entrepreneurship (
entrepreneurship_address,  end_activity, entrepreneurship_type, main_activity, entrepreneurship_name, start_activity, id) VALUES (
'calle 1'::character varying(255), '2020-04-21'::timestamp without time zone, 'Tipo'::character varying(255), 'Administracion'::character varying(255), 'Rodolfo Rodriguez'::character varying(255), '2020-01-20'::timestamp without time zone, '3'::bigint);

INSERT INTO public.credential_identity (
dni_beneficiary, dni_credit_holder, id, name_beneficiary, name_credit_holder) VALUES (
'435435436'::bigint, '46464'::bigint, '4'::bigint, 'Pedro Alberto Gonzales'::character varying(255), 'Pedro Alberto Gonzales'::character varying(255));