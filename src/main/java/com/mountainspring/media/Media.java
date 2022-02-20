package com.mountainspring.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Media {

    @Id
    @GeneratedValue
    private Long id;

    private String description;

    private Integer sortOrder;

}
