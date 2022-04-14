package ru.otus.homework.service;

import ru.otus.homework.domain.Book;
import ru.otus.homework.domain.BookMongo;

public interface BookService {

    BookMongo convertBookToBookMongo(Book book);

}
