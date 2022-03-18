package ru.otus.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.repository.query.Param;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.homework.domain.Comment;
import ru.otus.homework.exception.RecordNotFoundException;
import ru.otus.homework.repositories.BookRepository;
import ru.otus.homework.repositories.CommentRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String USER_ROLE = "ROLE_USER";

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final JdbcMutableAclService mutableAclService;

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

        val savedComment = commentRepository.save(comment);

        val authentication = SecurityContextHolder.getContext().getAuthentication();
        val ownerSid = new PrincipalSid(authentication);
        val objectId = new ObjectIdentityImpl(comment.getClass(), comment.getId());
        val adminRoleSid = new GrantedAuthoritySid(ADMIN_ROLE);
        val userRoleSid = new GrantedAuthoritySid(USER_ROLE);
        val acl = mutableAclService.createAcl(objectId);

        acl.setOwner(ownerSid);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, ownerSid, true);

        acl.insertAce(acl.getEntries().size(), BasePermission.READ, adminRoleSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, adminRoleSid, true);

        acl.insertAce(acl.getEntries().size(), BasePermission.READ, userRoleSid, true);

        mutableAclService.updateAcl(acl);

        return savedComment.getId() > 0;
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
    //@PreAuthorize(value = "hasPermission(#comment, 'ru.otus.homework.domain.Comment', 'WRITE')")
    public boolean updateComment(@Param("comment") Comment comment) {
        Comment updatedComment;
        comment.setTime(getCurrentTime());
        updatedComment = commentRepository.save(comment);

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
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
        val objectId = new ObjectIdentityImpl(comment.getClass(), comment.getId());
        mutableAclService.deleteAcl(objectId, true);
    }

    @Override
    @Transactional
    public void deleteCommentById(long id) {
        val optionalComment = commentRepository.findById(id);

        if(optionalComment.isPresent()) {
            deleteComment(optionalComment.get());
        }
    }

    private Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

}