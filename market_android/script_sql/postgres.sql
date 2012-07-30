--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;


CREATE ROLE marketgroup
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

  CREATE ROLE market LOGIN
  PASSWORD 'market'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;
GRANT marketgroup TO market;
--
-- Name: applicazione; Type: TABLE; Schema: public; Owner: marketgroup; Tablespace: 
--

CREATE TABLE applicazione (
    name text NOT NULL,
    icon text,
    package_ text,
    version_code integer,
    version_name text,
    md5sum text,
    apkname text
);


ALTER TABLE public.applicazione OWNER TO marketgroup;

--
-- Name: group_; Type: TABLE; Schema: public; Owner: marketgroup; Tablespace: 
--

CREATE TABLE group_ (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.group_ OWNER TO marketgroup;

--
-- Name: group_applicazione; Type: TABLE; Schema: public; Owner: marketgroup; Tablespace: 
--

CREATE TABLE group_applicazione (
    id integer NOT NULL,
    id_group_ integer,
    id_applicazione text
);


ALTER TABLE public.group_applicazione OWNER TO marketgroup;

--
-- Name: seq_group_; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_group_
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_group_ OWNER TO postgres;

--
-- Name: seq_group_; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_group_', 1, false);


--
-- Name: seq_user_; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_user_
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_user_ OWNER TO postgres;

--
-- Name: seq_user_; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_user_', 1, false);


--
-- Name: seq_usergroup; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_usergroup
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_usergroup OWNER TO postgres;

--
-- Name: seq_usergroup; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_usergroup', 1, false);


--
-- Name: user_; Type: TABLE; Schema: public; Owner: marketgroup; Tablespace: 
--

CREATE TABLE user_ (
    id integer NOT NULL,
    email text,
    password text
);


ALTER TABLE public.user_ OWNER TO marketgroup;

--
-- Name: usergroup; Type: TABLE; Schema: public; Owner: marketgroup; Tablespace: 
--

CREATE TABLE usergroup (
    id integer NOT NULL,
    id_user integer,
    id_group integer
);


ALTER TABLE public.usergroup OWNER TO marketgroup;

--
-- Name: pk_applicazione; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY applicazione
    ADD CONSTRAINT pk_applicazione PRIMARY KEY (name);


--
-- Name: pk_group_; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY group_
    ADD CONSTRAINT pk_group_ PRIMARY KEY (id);


--
-- Name: pk_group_applicazione; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY group_applicazione
    ADD CONSTRAINT pk_group_applicazione PRIMARY KEY (id);


--
-- Name: pk_user_; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY user_
    ADD CONSTRAINT pk_user_ PRIMARY KEY (id);


--
-- Name: pk_usergroup; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT pk_usergroup PRIMARY KEY (id);


--
-- Name: unique_email; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY user_
    ADD CONSTRAINT unique_email UNIQUE (email);


--
-- Name: unique_group_app; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY group_applicazione
    ADD CONSTRAINT unique_group_app UNIQUE (id_group_, id_applicazione);


--
-- Name: unique_user_group; Type: CONSTRAINT; Schema: public; Owner: marketgroup; Tablespace: 
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT unique_user_group UNIQUE (id_user, id_group);


--
-- Name: fk_apllicazione; Type: FK CONSTRAINT; Schema: public; Owner: marketgroup
--

ALTER TABLE ONLY group_applicazione
    ADD CONSTRAINT fk_apllicazione FOREIGN KEY (id_applicazione) REFERENCES applicazione(name);


--
-- Name: fk_group; Type: FK CONSTRAINT; Schema: public; Owner: marketgroup
--

ALTER TABLE ONLY group_applicazione
    ADD CONSTRAINT fk_group FOREIGN KEY (id_group_) REFERENCES group_(id);


--
-- Name: fk_group_; Type: FK CONSTRAINT; Schema: public; Owner: marketgroup
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT fk_group_ FOREIGN KEY (id_group) REFERENCES group_(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fk_user; Type: FK CONSTRAINT; Schema: public; Owner: marketgroup
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES user_(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: applicazione; Type: ACL; Schema: public; Owner: marketgroup
--

REVOKE ALL ON TABLE applicazione FROM PUBLIC;


--
-- Name: group_; Type: ACL; Schema: public; Owner: marketgroup
--

REVOKE ALL ON TABLE group_ FROM PUBLIC;


--
-- Name: group_applicazione; Type: ACL; Schema: public; Owner: marketgroup
--

REVOKE ALL ON TABLE group_applicazione FROM PUBLIC;


--
-- Name: seq_group_; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_group_ FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_group_ FROM postgres;
GRANT ALL ON SEQUENCE seq_group_ TO postgres;
GRANT ALL ON SEQUENCE seq_group_ TO marketgroup WITH GRANT OPTION;


--
-- Name: seq_user_; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_user_ FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_user_ FROM postgres;
GRANT ALL ON SEQUENCE seq_user_ TO postgres;
GRANT ALL ON SEQUENCE seq_user_ TO marketgroup WITH GRANT OPTION;


--
-- Name: seq_usergroup; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE seq_usergroup FROM PUBLIC;
REVOKE ALL ON SEQUENCE seq_usergroup FROM postgres;
GRANT ALL ON SEQUENCE seq_usergroup TO postgres;
GRANT ALL ON SEQUENCE seq_usergroup TO marketgroup WITH GRANT OPTION;


--
-- Name: user_; Type: ACL; Schema: public; Owner: marketgroup
--

REVOKE ALL ON TABLE user_ FROM PUBLIC;


--
-- Name: usergroup; Type: ACL; Schema: public; Owner: marketgroup
--

REVOKE ALL ON TABLE usergroup FROM PUBLIC;


--
-- PostgreSQL database dump complete
--

