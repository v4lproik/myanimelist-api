package com.github.v4lproik.myanimelist.api;

import com.github.v4lproik.myanimelist.api.models.ArtWork;
import com.github.v4lproik.myanimelist.api.models.Item;

import java.io.IOException;
import java.util.Set;

public interface UnitCrawler<ITEM extends Item> {
    ITEM crawl(Integer id) throws IOException;
    Set<ArtWork> crawl(Integer id, Boolean dependency) throws IOException;
}