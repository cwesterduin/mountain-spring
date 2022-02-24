package com.mountainspring.mapFeature;

import com.mountainspring.event.Event;
import com.mountainspring.media.Media;
import com.mountainspring.models.Point;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class MapFeature {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "json")
    @Type(type = "json")
    private Point coordinate;

    @ManyToOne
    private Media image;

    @ManyToOne
    private Event event;

}
