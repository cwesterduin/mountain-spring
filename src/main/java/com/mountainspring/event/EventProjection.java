package com.mountainspring.event;

import java.util.Date;

public interface EventProjection {
    String getId();
    String getName();
    Date getDate();
    boolean getCoordinates();
}
