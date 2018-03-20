package com.picfood.server.repository;

import com.picfood.server.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Shuqi on 18/3/20.
 */
public interface ImageRepository extends JpaRepository<Image, String>{
    public Image findImageByID(String id);
    public Image findImageByUrl(String url);
}
