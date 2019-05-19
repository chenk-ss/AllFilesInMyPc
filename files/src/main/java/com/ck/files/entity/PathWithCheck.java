package com.ck.files.entity;

import lombok.Data;

@Data
public class PathWithCheck {

    private String path;
    private Boolean file;

    public String getPath() {
        return path.replace('\\', '/');
    }
}
