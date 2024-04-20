package com.mountainspring.mapFeature;

public interface MapFeatureProjection {
    String getId();
    String getName();
    String getCoordinate();
    String getType();
    String getPronunciation();
    String getTranslation();
    boolean getPrimaryImage();
}
