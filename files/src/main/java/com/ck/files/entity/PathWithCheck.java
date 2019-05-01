package com.ck.files.entity;

import lombok.Data;

@Data
public class PathWithCheck {

    private String path;
    private Boolean isFile;

    public String getPath() {
        return path.replace('\\', '/');
    }
}
