package com.mountainspring.eventMedia;

import com.mountainspring.media.Media;
import lombok.Data;

@Data
public class EventMediaFrontend {

    private Long id;
    private Long mediaId;
    private String path;
    private int sortOrder;

}
