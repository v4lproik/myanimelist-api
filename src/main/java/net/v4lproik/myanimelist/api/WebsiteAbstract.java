package net.v4lproik.myanimelist.api;


import net.v4lproik.myanimelist.entities.MyAnimeListEntry;
import net.v4lproik.myanimelist.entities.MyAnimeListEntryDependency;

import java.io.IOException;
import java.util.Set;

public abstract class WebsiteAbstract {
    /**
     * This functions aims to crawl an anime through the website
     * @param opts contains the type manga or anime and the query's name
     * @return a MyAnimeListAnimes. Its one object that contains all the depedencies
     * @throws IOException
     */
    public abstract MyAnimeListEntry crawl(ImportOptions opts) throws IOException;

    /**
     * This functions aims to crawl an anime through the website by id
     * @param opts contains the type manga or anime and the query's name
     * @return
     * @throws IOException
     */
    public abstract MyAnimeListEntry crawlById(ImportOptions opts) throws IOException;

    /**
     * This functions aims to crawl an anime through the website by id
     * @param opts contains the type manga or anime and the query's name
     * @return
     * @throws IOException
     */
    public abstract Set<MyAnimeListEntryDependency> crawlByIdList(ImportOptions opts) throws IOException;

    /**
     * This function aims to call an API
     */
    public abstract void call();

}
