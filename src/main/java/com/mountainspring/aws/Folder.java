package com.mountainspring.aws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Folder {
    private String name;
    private String path;
}
