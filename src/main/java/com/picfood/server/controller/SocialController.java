package com.picfood.server.controller;

import com.picfood.server.entity.*;
import com.picfood.server.entity.DTO.PostDTO;
import com.picfood.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.picfood.server.config.JwtUtil.USER_ID;

/**
 * Created by shawn on 2018/3/21.
 */
@RestController
public class SocialController {

    private final SocialService socialService;
    private final UpvoteService upvoteService;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public SocialController(SocialService socialService, UpvoteService upvoteService, PostService postService, CommentService commentService, UserService userService, DishService dishService) {
        this.socialService = socialService;
        this.upvoteService = upvoteService;
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/api/follow")
    public Object follow(@RequestHeader(value = USER_ID) String userId, @RequestParam String id) {
        return socialService.follow(userId, id);
    }

    @PostMapping("/api/unfollow")
    public Object unfollow(@RequestHeader(value = USER_ID) String userId, @RequestParam String id) {
        return socialService.unfollow(userId, id);
    }

    @GetMapping("/api/followers")
    public List<User> getFollowers(@RequestHeader(value = USER_ID) String userId) {
        return socialService.getFollowers(userId);
    }

    @GetMapping("/api/followings")
    public List<User> getFollowings(@RequestHeader(value = USER_ID) String userId) {
        return socialService.getFollowings(userId);
    }

    @GetMapping("/api/followers/{id}")
    public List<User> getFollowers(@RequestHeader(value = USER_ID) String userId, @PathVariable("id") String id) {
        return socialService.getFollowers(id);
    }

    @GetMapping("/api/followings/{id}")
    public List<User> getFollowings(@RequestHeader(value = USER_ID) String userId, @PathVariable("id") String id) {
        return socialService.getFollowings(id);
    }

    @GetMapping("/api/timeline")
    public List<Timeline> getTimeline(@RequestHeader(value = USER_ID) String userId,
                                      @RequestParam(value = "time", required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date time) {
        return time == null ? getAllTimeline(userId, false) : getPageTimeline(userId, time);
    }

    @GetMapping("/api/timeline/{id}")
    public List<Timeline> getTimelineByUserId(@RequestHeader(value = USER_ID) String userId,
                                              @PathVariable("id") String id,
                                              @RequestParam(value = "time", required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date time) {
        return time == null ? getAllTimeline(id, true) : getPageTimeline(id, time);
    }

    private List<Timeline> getPageTimeline(String id, Date time) {
        List<User> followings = socialService.getFollowings(id);
        List<Timeline> timelines = new ArrayList<>();
        for (User f : followings) {
            timelines.addAll(commentService.getCommentByUserId(f.getUserId(), time));
            timelines.addAll(upvoteService.getUpvoteByUserId(f.getUserId(), time));
            timelines.addAll(postService.getPostByUserId(f.getUserId(), time));
        }
        timelines.sort((o1, o2) -> (o2.getTime().compareTo(o1.getTime())));
        int length = 20;
        if (timelines.size() > length)
            while (timelines.get(length).getTime().equals(timelines.get(length - 1))) length++;
        List<Timeline> first20 = timelines.subList(0, Math.min(length, timelines.size()));
        first20.stream().map(this::addTimelineDetail).collect(Collectors.toList());
        return first20;
    }

    private List<Timeline> getAllTimeline(String id, boolean one) {
        List<Timeline> timelines = new ArrayList<>();
        List<User> followings = one ? Collections.singletonList(userService.getUserById(id)) : socialService.getFollowings(id);
        for (User f : followings) {
            timelines.addAll(commentService.getCommentByUserId(f.getUserId()));
            timelines.addAll(upvoteService.getUpvoteByUserId(f.getUserId()));
            timelines.addAll(postService.getPostByUserId(f.getUserId()));
        }
        timelines.stream().map(this::addTimelineDetail).collect(Collectors.toList());
        timelines.sort((o1, o2) -> (o2.getTime().compareTo(o1.getTime())));
        return timelines;
    }

    private Timeline addTimelineDetail(Timeline t) {
        User u = userService.getUserById(t.getUserId());
        t.setUserAvatar(u.getAvatar());
        t.setUserName(u.getName());
        try {
            PostDTO p = postService.getPost(t.getPostId(), false);
            if (p != null) {
                t.setRestaurantId(p.getRestaurantId());
                t.setRestaurantName(p.getRestaurantName());
                t.setPosterName(p.getCreator());
                t.setPosterId(p.getCreatorId());
                t.setDishName(p.getDishName());
                t.setDishId(p.getDishId());
                // Dish d = dishService.findByPostId(t.getPostId());
                // if (d != null) t.setDishName(d.getName());
            }
        } catch (NullPointerException ignored) {

        }
        return t;
    }

}
