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

insert into comment (`id`, `author`, `time`, `text`, `book_id`) values (1, 'user', current_timestamp(),
'Comment text for book Spring boot 2', 2);
insert into comment (`id`, `author`, `time`, `text`, `book_id`) values (2, 'admin', current_timestamp(),
'Admin comment', 2);

insert into sec_role (`id`, `name`) values (1, 'ADMIN');
insert into sec_role (`id`, `name`) values (2, 'USER');

insert into sec_user (`id`, `name`, `password`, `account_non_locked`, `login_attempts`) values (1, 'admin',
'$2a$10$LaATetAOR53qmgu8eRktS.emH0LbzerltIXAEK6FYLFMEb8fg/zvy', true, 0); --password: adminP@ssword
insert into sec_user (`id`, `name`, `password`, `account_non_locked`, `login_attempts`) values (2, 'user',
'$2a$10$4PdcNLJ/oWKa7z69zbPR.uDYGGAWwVwL8W8eSPOMux2Wtdrk7v9SO', true, 0); --password: qwerty
insert into sec_user (`id`, `name`, `password`, `account_non_locked`, `login_attempts`) values (3, 'user1',
'$2a$10$4PdcNLJ/oWKa7z69zbPR.uDYGGAWwVwL8W8eSPOMux2Wtdrk7v9SO', true, 0); --password: qwerty

insert into sec_user_role (`user_id`, `role_id`) values (1, 1);
insert into sec_user_role (`user_id`, `role_id`) values (2, 2);
insert into sec_user_role (`user_id`, `role_id`) values (3, 2);