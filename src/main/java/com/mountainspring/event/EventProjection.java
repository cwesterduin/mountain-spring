package com.mountainspring.event;

import java.util.Date;

public interface EventProjection {
    String getId();
    String getName();
    String getDescriptionId();
    Date getDate();
    boolean getCoordinates();
}
