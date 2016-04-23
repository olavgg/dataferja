CREATE USER svele WITH PASSWORD 'kaffi';
CREATE DATABASE juleferja
  WITH ENCODING='UTF8'
       OWNER=svele
       TEMPLATE=template0
       LC_COLLATE='nn_NO.UTF-8'
       LC_CTYPE='nn_NO.UTF-8'
       CONNECTION LIMIT=-1;
\c juleferja
ALTER SCHEMA public OWNER TO svele;
