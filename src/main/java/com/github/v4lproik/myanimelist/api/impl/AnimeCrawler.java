package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.UnitCrawler;
import com.github.v4lproik.myanimelist.api.models.*;
import com.github.v4lproik.myanimelist.api.models.Character;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimeCrawler extends AbstractCrawler<Anime> implements UnitCrawler<Anime> {

    List<Integer> animeScrapped = new ArrayList<Integer>();
    List<Integer> animeErrorScrapped = new ArrayList<Integer>();
    Set<ArtWork> animes = new HashSet<ArtWork>();
    ArtWork root;
    ArtworkFactory artworkFactory = new ArtworkFactory();

    @Override
    public Anime crawl(Integer id) throws IOException {
        final TypeEnum type = TypeEnum.ANIME;

        if (id == null) {
            throw new IllegalArgumentException("Both type and id argument cannot be null");
        }

        if (id <= 0){
            throw new IllegalArgumentException("Both type and id argument cannot be empty");
        }

        final String url = createEntryURL(id, type);
        final Document doc = this.getResultFromJSoup(url, type.toString());
        if (doc == null) {
            throw new IOException(String.format("No data fetched for url %s", url));
        }

        return (Anime) scrap(doc, url, type.toString());
    }

    @Override
    public Set<Anime> crawl(Integer id, Boolean dependency) throws IOException {
        return null;
    }

    public Anime scrap(Document doc, String url, String type){

        String pattern;
        Integer id = this.getIdFromLink(url);

        if (doc == null || doc.text().isEmpty()){
            throw new IllegalArgumentException("Document cannot be null or empty");
        }

        Anime anime = new Anime();
        anime.setId(id);
        anime.setType(type);

        try {
            anime.setTitle(getMainTitle(doc));
        }catch (Exception e){
            log.debug("[myanimelist-api] Cannot parse main title", e);
        }

        try {
            anime.setPosterImage(getPosterImageLink(doc));
        }catch (Exception e){
            log.debug("[myanimelist-api] Cannot parse poster link", e);
        }

        Elements tds = doc.select("td");
        for (Element td : tds) {
            if (td.text().startsWith("Edit Synopsis")) {
                anime.setSynopsis(td.text().substring(13, td.text().length()));
            }else {
                if (td.text().startsWith("Edit Related")) {

                    Elements elts = td.select("table").select("td");

                    for (Element elt:elts) {

                        String line = elt.text();

                        if (line.startsWith("Sequel:")) {
                            log.debug("Sequel has been found");

                            try {
                                anime.setSequels(getRelated(anime, elt));
                            } catch (Exception e) {
                                log.debug("[myanimelist-api] Cannot parse sequels", e);
                            }

                        } else {
                            if (line.startsWith("Side story:")) {
                                log.debug("Side Stories have been found");

                                try {
                                    anime.setSideStories(getRelated(anime, elt));
                                } catch (Exception e) {
                                    log.debug("[myanimelist-api] Cannot parse side stories", e);
                                }

                            } else {
                                if (line.startsWith("Spin-off:")) {
                                    log.debug("Spin Off have been found");

                                    try {
                                        anime.setSpinoff(getRelated(anime, elt));
                                    } catch (Exception e) {
                                        log.debug("[myanimelist-api] Cannot parse spin off", e);
                                    }

                                } else {
                                    if (line.startsWith("Other:")) {
                                        log.debug("Others have been found");

                                        try {
                                            anime.setOthers(getRelated(anime, elt));
                                        } catch (Exception e) {
                                            log.debug("[myanimelist-api] Cannot parse others", e);
                                        }

                                    } else {
                                        if (line.startsWith("Prequel:")) {
                                            log.debug("Prequels have been found");

                                            try {
                                                anime.setPrequels(getRelated(anime, elt));
                                            } catch (Exception e) {
                                                log.debug("[myanimelist-api] Cannot parse prequels", e);
                                            }

                                        } else {
                                            if (line.startsWith("Alternative version:")) {
                                                log.debug("Alternative versions have been found");

                                                try {
                                                    anime.setAlternativeVersions(getRelated(anime, elt));
                                                } catch (Exception e) {
                                                    log.debug("[myanimelist-api] Cannot parse alternative versions", e);
                                                }

                                            } else {
                                                if (line.startsWith("Adaptation")) {
                                                    log.debug("Adaptations have been found");

                                                    try {
                                                        anime.setAdaptations(getRelated(anime, elt));
                                                    } catch (Exception e) {
                                                        log.debug("[myanimelist-api] Cannot parse adaptations", e);
                                                    }

                                                } else {
                                                    if (line.startsWith("Summary")) {
                                                        log.debug("Summaries have been found");

                                                        try {
                                                            anime.setSummaries(getRelated(anime, elt));
                                                        } catch (Exception e) {
                                                            log.debug("[myanimelist-api] Cannot parse summaries", e);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Elements divs = doc.select("div");

        for (Element div : divs) {

            if (div.text().startsWith("Synopsis: ")) {
                anime.setSynopsis(div.text().substring(9, div.text().length()));
            }

            if (div.text().startsWith("Background: ")){
                anime.setBackground(div.text().substring(12, div.text().length()));
            }

            if (div.text().startsWith("Synonyms: ")){
                anime.setSynonyms(div.text().substring(10, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("English: ")){
                anime.setEnglishTitle(div.text().substring(9, div.text().length()));
            }

            if (div.text().startsWith("Japanese: ")){
                anime.setJapaneseTitle(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Episodes: ")){
                anime.setEpisodeCount(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Published: ")){
                String[] fullTime = div.text().substring(11, div.text().length()).split(" to ");

                try {
                    anime.setStartedAiringDate(fullTime[0]);
                    anime.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("[myanimelist-api] Cannot parse airing/finishing dates", e);
                }
            }

            if (div.text().startsWith("Aired: ")){
                String[] fullTime = div.text().substring(7, div.text().length()).split(" to ");

                try {
                    anime.setStartedAiringDate(fullTime[0]);
                    anime.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("[myanimelist-api] Cannot parse airing/finishing dates", e);
                }
            }

            if (div.text().startsWith("Producers: ")){
                anime.setProducers(div.text().substring(11, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("Genres: ")){
                anime.setGenres(div.text().substring(8, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("Authors: ")){

                String parts[] = div.text().substring(9, div.text().length()).replace(", ", ",").split(",");

                if (!parts[0].equals("None")) {
                    List<Author> authors = anime.getAuthors();

                    for (int i = 0; i < parts.length; i = i + 2) {
                        Author author = new Author();
                        try {
                            author.setFirstName(parts[i]);
                            author.setLastName(parts[i + 1].split(" ")[0]);

                            try {
                                String[] jobsExtracted = parts[i + 1].substring(parts[i + 1].indexOf("(") + 1, parts[i + 1].indexOf(")")).split(" & ");
                                author.setJob(jobsExtracted);
                            }catch (Exception e1){
                                log.debug("[myanimelist-api] Cannot parse author's job", e1);
                            }

                        } catch (ArrayIndexOutOfBoundsException e) {
                            author.setLastName(parts[i]);

                            try {
                                String[] jobsExtracted = parts[i].substring(parts[i].indexOf("(") + 1, parts[i].indexOf(")")).split(" & ");
                                author.setJob(jobsExtracted);
                            }catch (Exception e2){
                                log.debug("[myanimelist-api] Cannot parse author's job", e2);
                            }
                        }

                        authors.add(author);
                        log.debug(String.format("Add new author %s", author.toString()));
                    }

                    anime.setAuthors(authors);
                }
            }

            if (div.text().startsWith("Duration: ")) {
                 ((Anime) anime).setEpisodeLength(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Rating: ")) {
                anime.setAgeRating(div.text().substring(8, div.text().length()));
            }

            if (div.text().startsWith("Score: ")) {
                anime.setScore(div.text().substring(7, div.text().length()));
            }

            if (div.text().startsWith("Ranked: ")) {
                anime.setRank(div.text().substring(8, div.text().length()));
            }

            if (div.text().startsWith("Popularity: ")) {
                anime.setPopularity(div.text().substring(12, div.text().length()));
            }

            if (div.text().startsWith("Type: ")){
                if (anime.isAnime()){
                    anime.setShowType(div.text().substring(6, div.text().length()));
                }
            }
        }

        pattern = "More characters Characters & Voice Actors";

        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().equals("Popular Tags")){
                log.debug("Popular tags have been found");

                anime.setTags(getTags(h2));

            }else{
                if (h2.text().equals(pattern)) {
                    log.debug("Characters have been found");

                    anime.setCharacters(getCharactersBasicInfo(h2));

                }else {
                    if (h2.text().startsWith("More staff Staff")) {
                        log.debug("Authors have been found");

                        anime.setAuthors(getAuthorsBasicInfo(h2));
                    }
                }
            }
        }

        return anime;
    }

    private String[] getTags(Element h2) {
        Elements els = h2.nextElementSibling().select("span").select("a");

        try {
            String[] tags = new String[els.size()];
            int i = 0;
            for (Element tag : els){
                tags[i] = tag.text();
                i++;
            }
            return tags;

        }catch (Exception e){
            log.debug("[myanimelist-api] Cannot parse tags", e);
        }

        return new String[]{};
    }

    private String getPosterImageLink(Document doc) {
        return doc.select("meta[property=og:image]").attr("content");
    }

    private String getMainTitle(Document doc) {
        Element firstH1 = doc.select("h1").first();
        return firstH1.childNodes().get(2).childNodes().get(0).toString();
    }

    private List<Author> getAuthorsBasicInfo(Element h2) {
        Node current = h2.nextElementSibling();
        Element el = (Element) current;
        List<Author> authors = new ArrayList<>();

        Elements trs = el.select("tr");

        for (Element tr : trs){

            // Get character info
            Author author = new Author();
            try{
                String characterFullName = tr.select("td").get(1).select("a").text();
                String[] jobs = tr.select("td").get(1).select("small").text().split(" ,");
                String[] parts = characterFullName.split(", ");

                if (parts.length == 2){
                    author.setFirstName(parts[0]);
                    author.setLastName(parts[1]);
                }else {
                    if (parts.length == 1)
                        author.setLastName(characterFullName);
                }
                author.setJob(jobs);
                authors.add(author);

                log.info(String.format("Add new author %s", author.toString()));
            }catch (Exception e){
                log.debug("Error trying to get author's name");
            }
        }

        return authors;
    }

    private List<Character> getCharactersBasicInfo(Element h2) {
        Node current = h2.parent();
        Element el = (Element) current;
        List<Node> userinfo;
        List<Character> characters = new ArrayList<>();
        Character character;

        Elements trs = el.select("a[href*=/character/]");

        for (Element tr : trs){

            try{
                // remove duplicate url
                if (tr.parentNode().childNodes().size() > 3) {
                    userinfo = tr.parentNode().childNodes();

                    String characterFullName = userinfo.get(1).childNodes().get(0).toString();
                    Integer idCharacter = getIdFromLink(tr.attr("href"));
                    String role = userinfo.get(3).childNodes().get(1).childNodes().get(0).toString();

                    String[] parts = characterFullName.split(", ");

                    character = new Character();
                    if (parts.length == 2) {
                        character.setFirstName(parts[1]);
                        character.setLastName(parts[0]);
                    } else {
                        if (parts.length == 1)
                            character.setLastName(characterFullName);
                        else
                            continue;
                    }
                    character.setRole(role);
                    character.setId(idCharacter);

                    characters.add(character);

                    log.debug(String.format("Add new character %s", character.toString()));
                }
            }catch (Exception e){
                log.debug("[myanimelist-api] Cannot parse character basic info", e);
            }
        }


        return characters;
    }

    private List<ArtworkId> getRelated(ArtWork artWork, Element elt) throws Exception{
        String type;
        List<Node> links = elt.parentNode().childNodes().get(3).childNodes();
        List<ArtworkId> relatedEntities = new ArrayList<>();

        for (Node link : links) {
            String linkHref = link.attr("href");

            if (!linkHref.isEmpty()){
                String title = link.childNodes().get(0).toString();
                Integer id = this.getIdFromLink(linkHref);
                type = this.getTypeFromLink(linkHref);

                if (id != null) {

                    ArtworkId related = new ArtworkId();
                    related.setId(id);
                    related.setType(type);
                    related.setTitle(title);
//                    related.setParent(artWork);

                    relatedEntities.add(related);
                }
            }
        }

        return relatedEntities;
    }

    public void call() {
        throw new NotImplementedException();
    }
}
