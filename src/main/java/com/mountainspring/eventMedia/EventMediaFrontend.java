package com.mountainspring.eventMedia;

import lombok.Data;

import java.util.UUID;

@Data
public class EventMediaFrontend {

    private UUID id;
    private UUID mediaId;
    private String path;
    private int sortOrder;

}
