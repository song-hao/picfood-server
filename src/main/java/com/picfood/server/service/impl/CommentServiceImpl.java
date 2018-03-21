package com.picfood.server.service.impl;

import java.util.List;
import com.picfood.server.entity.Comment;

import com.picfood.server.entity.Post;
import com.picfood.server.entity.Upvote;
import com.picfood.server.repository.CommentRepository;
import com.picfood.server.repository.PostRepository;
import com.picfood.server.repository.UpvoteRepository;
import com.picfood.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UpvoteRepository upvoteRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UpvoteRepository upvoteRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.upvoteRepository = upvoteRepository;
        this.postRepository = postRepository;
    }

    public Comment makeComment(String uid, String postId, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(uid);
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }

    public List<Comment> getComment(String postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteByCommentId(commentId);
    }

    @Override
    public List<Comment> getCommentByPostId(String postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public long getCommentCountByPostId(String postId) {
        return commentRepository.getCommentCountByPostId(postId);
    }

}
