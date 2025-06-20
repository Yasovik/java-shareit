DROP TABLE IF EXISTS comment CASCADE;
 DROP TABLE IF EXISTS booking CASCADE;
 DROP TABLE IF EXISTS item CASCADE;
 DROP TABLE IF EXISTS request CASCADE;
 DROP TABLE IF EXISTS users CASCADE;

 CREATE TABLE IF NOT EXISTS users (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL UNIQUE
 );

 CREATE TABLE IF NOT EXISTS request (
     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     description VARCHAR(1000) NOT NULL,
     requestor_id BIGINT NOT NULL,
     CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) REFERENCES users(id)
         ON UPDATE CASCADE ON DELETE CASCADE
 );

 CREATE TABLE IF NOT EXISTS item (
     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description VARCHAR(1000) NOT NULL,
     is_available BOOLEAN NOT NULL,
     owner_id BIGINT NOT NULL,
     CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id)
         ON UPDATE CASCADE ON DELETE CASCADE
 );

 CREATE TABLE IF NOT EXISTS comment (
     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     text VARCHAR(2000) NOT NULL,
     item_id BIGINT NOT NULL,
     author_id BIGINT NOT NULL,
     created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
     CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id)
         ON UPDATE CASCADE ON DELETE CASCADE,
     CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item(id)
         ON UPDATE CASCADE ON DELETE CASCADE
 );

 CREATE TABLE IF NOT EXISTS booking (
     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     item_id BIGINT NOT NULL,
     booker_id BIGINT NOT NULL,
     status VARCHAR(20) NOT NULL,
     CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item(id)
         ON UPDATE CASCADE ON DELETE CASCADE,
     CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users(id)
         ON UPDATE CASCADE ON DELETE CASCADE
 );