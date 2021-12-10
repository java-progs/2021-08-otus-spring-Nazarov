drop table if exists book;
drop table if exists author;
drop table if exists genre;
drop table if exists book_author;
drop table if exists book_genre;

create table book(id identity primary key, name varchar(255) not null, isbn varchar(255));
create unique index if not exists book_name on book(name, isbn);

create table author(id identity primary key, surname varchar(255) not null, name varchar(255) not null, patronymic varchar(255));
create unique index if not exists author_name on author(surname, name, patronymic);

create table genre(id identity primary key, name varchar(255) not null);
create unique index if not exists genre_name on genre(name);

create table book_author(book_id long not null, author_id long not null);
create unique index if not exists book_and_author on book_author(book_id, author_id);
alter table book_author add foreign key (author_id) references author(id) on update cascade;
alter table book_author add foreign key (book_id) references book(id) on delete cascade on update cascade;

create table book_genre(book_id long not null, genre_id long not null);
create unique index if not exists book_and_genre on book_genre(book_id, genre_id);
alter table book_genre add foreign key (genre_id) references genre(id) on update cascade;
alter table book_genre add foreign key (book_id) references book(id) on delete cascade on update cascade;