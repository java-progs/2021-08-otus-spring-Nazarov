package ru.otus.homework.service;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.dao.BookRepository;
import ru.otus.homework.dao.CommentRepository;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    public CommentServiceImpl(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountComments() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(long id) throws RecordNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Not found comment with id = %d", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getBookComments(long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public boolean saveComment(Comment comment) {

        if (comment.getTime() == null) {
            comment.setTime(getCurrentTime());
        }

        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean saveComment(String author, String text, long bookId) {
        val optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isEmpty()) {
            return false;
        }

        val book = optionalBook.get();
        val comment = new Comment(0, author, getCurrentTime(), text, book);

        return saveComment(comment);
    }

    @Override
    @Transactional
    public boolean updateComment(Comment comment) {
        Comment updatedComment;

        comment.setTime(getCurrentTime());
        try {
            updatedComment = commentRepository.save(comment);
        } catch (Exception e) {
            return false;
        }

        return comment.equals(updatedComment);
    }

    @Override
    @Transactional
    public boolean updateComment(long id, String text) {
        val optionalComment = commentRepository.findById(id);

        if (optionalComment.isEmpty()) {
            return false;
        }

        val comment = optionalComment.get();
        comment.setText(text);

        return updateComment(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(long id) {
        commentRepository.deleteById(id);
    }

    private Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

}