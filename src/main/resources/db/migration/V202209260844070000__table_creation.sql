CREATE SEQUENCE category_id_generator;

CREATE TABLE category(
	id INTEGER DEFAULT category_id_generator.nextval,
	name VARCHAR(20) UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE book(
	isbn CHAR(13) NOT NULL,
	title VARCHAR(50) NOT NULL,
	sinopse VARCHAR(500),
	author VARCHAR(50) NOT NULL,
	year_release INTEGER NOT NULL,
	price NUMERIC(10,2) NOT NULL,
	stock INTEGER NOT NULL,
	PRIMARY KEY (isbn)
);

CREATE TABLE book_category(
	book_id CHAR(13) NOT NULL,
	category_id INTEGER NOT NULL,
	FOREIGN KEY (book_id) REFERENCES book(isbn),
	FOREIGN KEY (category_id) REFERENCES category(id)
);
