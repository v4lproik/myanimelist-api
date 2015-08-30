package net.v4lproik.myanimelist.api;

import java.io.IOException;

public interface Character {
    //-----------------
    // Character
    //-----------------

    net.v4lproik.myanimelist.entities.Character crawl(Integer id) throws IOException;
}
