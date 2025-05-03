package com.mountainspring.mapFeature;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "id"})
public interface MapFeatureProjection {
    String getId();
    String getName();
    String getCoordinate();
    String getType();
    String getPronunciation();
    String getTranslation();
    boolean getPrimaryImage();
}
