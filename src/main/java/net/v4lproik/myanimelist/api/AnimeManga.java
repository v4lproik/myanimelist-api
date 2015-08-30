package net.v4lproik.myanimelist.api;

import net.v4lproik.myanimelist.api.models.TypeEnum;
import net.v4lproik.myanimelist.entities.Entry;
import net.v4lproik.myanimelist.entities.EntryDependency;

import java.io.IOException;
import java.util.Set;

public interface AnimeManga {
    //-----------------
    // Anime or manga
    //-----------------

    Entry crawl(String name, TypeEnum type) throws IOException;

    Entry crawl(Integer id, TypeEnum type) throws IOException;

    Set<EntryDependency> crawlAndDependencies(Integer id, TypeEnum type) throws IOException;
}
