package com.github.v4lproik.myanimelist.api.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ArtworkId {

    private int id;

    private String title;

    private String type;

    public ArtworkId() {
    }

    public ArtworkId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("type", type)
                .toString();
    }
}
