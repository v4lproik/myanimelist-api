package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.UnitCrawler;
import com.github.v4lproik.myanimelist.api.models.*;
import org.jsoup.nodes.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;

public class ArtWorksCrawler extends AbstractCrawler<ArtWork> implements UnitCrawler<ArtWork> {

    List<Integer> artworksScrapped = new ArrayList<Integer>();
    List<Integer> artworksErrorScrapped = new ArrayList<Integer>();
    Set<ArtWork> artworks = new HashSet<ArtWork>();
    ArtWork root;
    ArtworkFactory artworkFactory = new ArtworkFactory();

    @Override
    public Anime crawl(Integer id) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public Set<ArtWork> crawl(Integer id, Boolean dependency) throws IOException {
        final String type = TypeEnum.ANIME.toString();

        if (id == null) {
            throw new IllegalArgumentException("Both type and id argument cannot be null");
        }

        if (id <= 0){
            throw new IllegalArgumentException("Both type and id argument cannot be empty");
        }

        if (!dependency) {
            return new HashSet<ArtWork>() {{
                crawl(id);
            }};
        }

        root = artworkFactory.getEntity(type, id);
        root.setType(type);

        letsScrap(root);

        return artworks;
    }

    private ArtWork letsScrap(ArtWork toScrap) throws IOException {

        String url;
        Document doc;

        while (toScrap != null){

            String type = toScrap.getType();
            Integer id = toScrap.getId();

            if (type == null || id == null){
                throw new IllegalArgumentException("Both type and id argument cannot be null");
            }

            if (type.isEmpty() || id.toString().isEmpty()){
                throw new IllegalArgumentException("Both type and id argument cannot be empty");
            }

            if (!artworksScrapped.contains(toScrap.getId())) {

                ArtWork artwork = null;
                switch (TypeEnum.fromValue(type)){
                    case MANGA:
                        UnitCrawler<Anime> animeCrawler = new AnimeCrawler();
                        artwork = animeCrawler.crawl(id);
                        break;

                    case ANIME:
                        UnitCrawler<Manga> mangaCrawler = new MangaCrawler();
                        artwork = mangaCrawler.crawl(id);
                        break;
                }

                if (artwork == null){
                    artworksErrorScrapped.add(id);
                }else {
                    artworksScrapped.add(id);
                    artworks.add(artwork);
                }
            }

            ArtWork nextToScrap = whoSNext(toScrap);

            // Tricky part
            if (nextToScrap == null){
                letsScrap(toScrap.getParent());
            }

            toScrap = nextToScrap;
        }

        return root;
    }

    private ArtWork whoSNext(ArtWork lastScrapped){

        log.debug(String.format("Animes that have been scrapped %s ", Arrays.asList(artworksScrapped)));
        log.debug(String.format("Animes that have been errorscrapped %s ", Arrays.asList(artworksErrorScrapped)));
        Integer id;

        for (ArtworkId artWork : lastScrapped.getAdaptations()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getAlternativeVersions()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getPrequels()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getSequels()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getSideStories()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getOthers()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getSummaries()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        for (ArtworkId artWork : lastScrapped.getSpinoff()){
            id = artWork.getId();

            if (!artworksScrapped.contains(id) && !artworksErrorScrapped.contains(id)){
                return artworkFactory.getEntity(artWork.getType(), artWork.getId());
            }
        }

        return null;
    }

    public void call() {
        throw new NotImplementedException();
    }
}
