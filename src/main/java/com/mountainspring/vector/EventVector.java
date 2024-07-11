package com.mountainspring.vector;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mountainspring.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class EventVector {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @JoinColumn(name = "event_id")
    @ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    private Event event;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)
    private List<Double> embedding;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVector that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) && Objects.equals(embedding, that.embedding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, embedding);
    }
}
