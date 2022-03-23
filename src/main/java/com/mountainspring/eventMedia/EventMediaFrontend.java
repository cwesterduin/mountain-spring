package com.mountainspring.eventMedia;

import com.mountainspring.media.Media;
import lombok.Data;

import java.util.UUID;

@Data
public class EventMediaFrontend {

    private Long id;
    private UUID mediaId;
    private String path;
    private int sortOrder;

}
