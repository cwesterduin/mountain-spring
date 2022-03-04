package com.mountainspring.aws;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private UUID id;

    private String bucketName;
    private String path;
    private String classification;
    private String region;

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