package com.mountainspring.event;

import com.fasterxml.jackson.annotation.*;
import com.mountainspring.models.Point;
import com.mountainspring.media.Media;
import com.mountainspring.trip.Trip;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    private Date date;

    @Column(columnDefinition = "json")
    @Type(type = "json")
    private List<Point> coordinates;

    private String descriptionId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer rating;

    private Double elevation;

    private Double distance;

    @ManyToMany
    private List<Media> media;

    @OneToOne
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
}
