package com.picfood.server.service.impl;

import com.picfood.server.entity.Upvote;
import com.picfood.server.entity.Post;
import com.picfood.server.repository.UpvoteRepository;
import com.picfood.server.repository.PostRepository;
import com.picfood.server.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpvoteServiceImpl implements UpvoteService {
    private final UpvoteRepository upvoteRepository;
    private final PostRepository postRepository;

    @Autowired
    public UpvoteServiceImpl(UpvoteRepository upvoteRepository, PostRepository postRepository) {
        this.upvoteRepository = upvoteRepository;
        this.postRepository = postRepository;
    }

    public Upvote upvote(String uid, String postId) {
        Upvote like = new Upvote();
        like.setUserId(uid);
        like.setPostId(postId);
        Post post = postRepository.findByPostId(postId);
        post.setUpvoteCount(Math.max(0, post.getUpvoteCount() + 1));
        return upvoteRepository.save(like);
    }

    public void deleteUpvote(String likeId, String postId) {
        upvoteRepository.deleteByUpvoteId(likeId);
        Post post = postRepository.findByPostId(postId);
        post.setUpvoteCount(Math.max(0, post.getUpvoteCount() - 1));
    }
}