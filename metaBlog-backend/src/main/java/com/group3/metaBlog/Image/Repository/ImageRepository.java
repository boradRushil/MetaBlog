package com.group3.metaBlog.Image.Repository;

import com.group3.metaBlog.Image.Model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByName(String imageURL);
}
