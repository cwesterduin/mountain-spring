package com.mountainspring.mapFeature;

import com.mountainspring.aws.S3Object;
import com.mountainspring.models.Point;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "map_feature", schema = "public")
public class MapFeature {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    private String name;

    private String type;

    private String height;

    private String translation;

    private String pronunciation;

    private String munroOrder;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Point coordinate;

    @ManyToOne
    private S3Object primaryImage;

}
