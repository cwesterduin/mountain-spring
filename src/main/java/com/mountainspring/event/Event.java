package com.mountainspring.event;

import com.fasterxml.jackson.annotation.*;
import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.mapFeature.MapFeature;
import com.mountainspring.models.Point;
import com.mountainspring.trip.Trip;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Event {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(unique = true)
    private String name;

    private Date date;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Point> coordinates;

    private String descriptionId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer rating;

    private Double elevation;

    private Double distance;

    @OneToMany(mappedBy = "event")
    @ToString.Exclude
    @JsonIgnore
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    private List<EventMedia> eventMedia = new ArrayList<>();

    @ManyToMany
    @ToString.Exclude
    private List<MapFeature> mapFeatures;


    @ManyToOne()
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id",  scope = Trip.class)
    @JsonIdentityReference(alwaysAsId = true)
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean getCoordinatesAsBoolean(){
        return coordinates == null;
    }
}
