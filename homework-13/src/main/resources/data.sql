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

insert into acl_sid (id, principal, sid) values
(1, 0, 'ROLE_ADMIN'),
(2, 0, 'ROLE_USER'),
(3, 1, 'admin'),
(4, 1, 'user');

insert into acl_class (id, class) values
(1, 'ru.otus.homework.domain.Comment');

insert into acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) values
(1, 1, 1, null, 3, 0),
(2, 1, 2, null, 4, 0);

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask,
                       granting, audit_success, audit_failure) values
(1, 1, 1, 4, 1, 1, 1, 1), -- 7, user_comment, 1, user, R, 1, 1, 1
(2, 1, 2, 4, 2, 1, 1, 1), -- 8, user_comment, 2, user, W, 1, 1, 1
(3, 1, 3, 4, 8, 1, 1, 1), -- 8, user_comment, 2, user, D, 1, 1, 1
(4, 1, 4, 1, 1, 1, 1, 1), -- 8, user_comment, 2, ROLE_ADMIN, R, 1, 1, 1
(5, 1, 5, 1, 8, 1, 1, 1), -- 8, user_comment, 2, ROLE_ADMIN, D, 1, 1, 1
(6, 1, 6, 2, 1, 1, 1, 1), -- 8, user_comment, 2, ROLE_USER, R, 1, 1, 1
(7, 2, 1, 3, 1, 1, 1, 1), -- 7, admin_comment, 1, admin, R, 1, 1, 1
(8, 2, 2, 3, 2, 1, 1, 1), -- 8, admin_comment, 2, admin, W, 1, 1, 1
(9, 2, 3, 3, 8, 1, 1, 1), -- 8, admin_comment, 2, admin, D, 1, 1, 1
(10, 2, 4, 1, 1, 1, 1, 1), -- 8, admin_comment, 2, ROLE_ADMIN, R, 1, 1, 1
(11, 2, 5, 1, 8, 1, 1, 1), -- 8, admin_comment, 2, ROLE_ADMIN, D, 1, 1, 1
(12, 2, 6, 2, 1, 1, 1, 1); -- 8, admin_comment, 2, ROLE_USER, R, 1, 1, 1