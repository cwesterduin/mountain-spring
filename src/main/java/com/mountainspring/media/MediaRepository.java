package com.mountainspring.media;

import com.mountainspring.media.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
