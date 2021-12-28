insert into author (`id`, `surname`, `name`, `patronymic`) values (1, 'Pushkin', 'Aleksandr', 'Sergeevich');
insert into author (`id`, `surname`, `name`) values (2, 'Gutierrez', 'Felipe');

insert into genre (`id`, `name`) values (1, 'Belletristic');
insert into genre (`id`, `name`) values (2, 'Programming');
insert into genre (`id`, `name`) values (3, 'Java-book');

insert into book (`id`, `name`) values (1, 'The Fisherman and the Goldfish');
insert into book (`id`, `name`) values (2, 'Spring boot 2');

insert into book_author (`book_id`, `author_id`) values (1, 1);
insert into book_author (`book_id`, `author_id`) values (2, 2);

insert into book_genre (`book_id`, `genre_id`) values (1, 1);
insert into book_genre (`book_id`, `genre_id`) values (2, 2);
insert into book_genre (`book_id`, `genre_id`) values (2, 3);

insert into comment (`id`, `author`, `time`, `text`, `book_id`) values (1, 'User1', current_timestamp(), 'Comment text for book Spring boot 2', 2);