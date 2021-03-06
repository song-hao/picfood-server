package com.picfood.server.service.impl;

import com.picfood.server.entity.Upvote;
import com.picfood.server.entity.Post;
import com.picfood.server.repository.UpvoteRepository;
import com.picfood.server.repository.PostRepository;
import com.picfood.server.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UpvoteServiceImpl implements UpvoteService {
    private final UpvoteRepository upvoteRepository;
    private final PostRepository postRepository;

    @Autowired
    public UpvoteServiceImpl(UpvoteRepository upvoteRepository, PostRepository postRepository) {
        this.upvoteRepository = upvoteRepository;
        this.postRepository = postRepository;
    }

    public Upvote upvote(String uid, String postId) {
        Upvote upvote = new Upvote();
        upvote.setUserId(uid);
        upvote.setPostId(postId);
        Post post = postRepository.findByPostId(postId);
        post.setUpvoteCount(Math.max(0, post.getUpvoteCount() + 1));
        upvoteRepository.save(upvote);
        return upvote;
    }

    public void deleteUpvote(String upvoteId, String postId) {
        upvoteRepository.deleteByUpvoteId(upvoteId);
        Post post = postRepository.findByPostId(postId);
        if (post == null) {
            return;
        }
        post.setUpvoteCount(Math.max(0, post.getUpvoteCount() - 1));
    }

    public List<Upvote> getUpvoteByUserId(String userId) {
        return upvoteRepository.findAllByUserId(userId);
    }

    public List<Upvote> getUpvoteByUserId(String userId, Date time) {
        return upvoteRepository.findFirst20ByUserIdAndTimeBeforeOrderByTimeDesc(userId, time);
    }

    public String hasUpvoted(String userId, String postId) {
        List<Upvote> u = upvoteRepository.findAllByUserIdAndPostId(userId, postId);
        if (u.size() > 0) {
            return u.get(0).getUpvoteId();
        } else {
            return "Not Upvoted";
        }
    }

}
