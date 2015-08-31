package com.github.v4lproik.myanimelist.api;

import com.github.v4lproik.myanimelist.api.models.TypeEnum;
import com.github.v4lproik.myanimelist.entities.Entry;
import com.github.v4lproik.myanimelist.entities.EntryDependency;

import java.io.IOException;
import java.util.Set;

public interface AnimeManga {
    //-----------------
    // Anime or manga
    //-----------------

    Entry crawl(Integer id, TypeEnum type) throws IOException;

    Set<EntryDependency> crawlAndDependencies(Integer id, TypeEnum type) throws IOException;
}
