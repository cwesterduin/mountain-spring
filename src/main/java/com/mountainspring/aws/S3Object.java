package com.mountainspring.aws;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class S3Object {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    private String bucketName;
    private String path;
    private String classification;
    private String region;
    private String description;
    private String fileType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        S3Object s3Object = (S3Object) o;
        return id != null && Objects.equals(id, s3Object.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
