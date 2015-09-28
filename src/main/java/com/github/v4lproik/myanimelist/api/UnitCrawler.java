package com.github.v4lproik.myanimelist.api;

import com.github.v4lproik.myanimelist.api.models.Item;

import java.io.IOException;

public interface UnitCrawler<ITEM extends Item> {
    ITEM crawl(Integer id) throws IOException;
}