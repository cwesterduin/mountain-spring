package com.mountainspring.mapFeature;

import com.mountainspring.aws.S3Object;
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

    private String name;

    private String type;

    private String height;

    private String translation;

    private String pronunciation;

    private String munroOrder;

    @Column(columnDefinition = "json")
    @Type(type = "json")
    private Point coordinate;

    @ManyToOne
    private S3Object primaryImage;

}
