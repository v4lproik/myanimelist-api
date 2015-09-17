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
import java.util.List;
import java.util.Set;

public class MangaCrawler extends AbstractCrawler<Manga> implements UnitCrawler<Manga> {

    @Override
    public Manga crawl(Integer id) throws IOException {
        final TypeEnum type = TypeEnum.MANGA;

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

        return (Manga) scrap(doc, url, type.toString());
    }

    @Override
    public Set<Manga> crawl(Integer id, Boolean dependency) throws IOException {
        return null;
    }

    public ArtWork scrap(Document doc, String url, String type){

        String pattern;
        Integer id = this.getIdFromLink(url);

        if (doc == null || doc.text().isEmpty()){
            throw new IllegalArgumentException("Document cannot be null or empty");
        }

        Manga manga = new Manga(id);
        manga.setType(type);

        try {
            manga.setTitle(getMainTitle(doc));
        }catch (Exception e){
            log.debug("[myanimelist-api] Cannot parse main title", e);
        }

        try {
            manga.setPosterImage(getPosterImageLink(doc));
        }catch (Exception e){
            log.debug("[myanimelist-api] Cannot parse poster link", e);
        }

        Elements tds = doc.select("td");
        for (Element td : tds) {
            if (td.text().startsWith("Edit Synopsis")) {
                manga.setSynopsis(td.text().substring(13, td.text().length()));
            }else {
                if (td.text().startsWith("Edit Related")) {

                    Elements elts = td.select("table").select("td");

                    for (Element elt:elts) {

                        String line = elt.text();

                        if (line.startsWith("Sequel:")) {
                            log.debug("Sequel has been found");

                            try {
                                manga.setSequels(getRelated(manga, elt));
                            } catch (Exception e) {
                                log.debug("[myanimelist-api] Cannot parse sequels", e);
                            }

                        } else {
                            if (line.startsWith("Side story:")) {
                                log.debug("Side Stories have been found");

                                try {
                                    manga.setSideStories(getRelated(manga, elt));
                                } catch (Exception e) {
                                    log.debug("[myanimelist-api] Cannot parse side stories", e);
                                }

                            } else {
                                if (line.startsWith("Spin-off:")) {
                                    log.debug("Spin Off have been found");

                                    try {
                                        manga.setSpinoff(getRelated(manga, elt));
                                    } catch (Exception e) {
                                        log.debug("[myanimelist-api] Cannot parse spin off", e);
                                    }

                                } else {
                                    if (line.startsWith("Other:")) {
                                        log.debug("Others have been found");

                                        try {
                                            manga.setOthers(getRelated(manga, elt));
                                        } catch (Exception e) {
                                            log.debug("[myanimelist-api] Cannot parse others", e);
                                        }

                                    } else {
                                        if (line.startsWith("Prequel:")) {
                                            log.debug("Prequels have been found");

                                            try {
                                                manga.setPrequels(getRelated(manga, elt));
                                            } catch (Exception e) {
                                                log.debug("[myanimelist-api] Cannot parse prequels", e);
                                            }

                                        } else {
                                            if (line.startsWith("Alternative version:")) {
                                                log.debug("Alternative versions have been found");

                                                try {
                                                    manga.setAlternativeVersions(getRelated(manga, elt));
                                                } catch (Exception e) {
                                                    log.debug("[myanimelist-api] Cannot parse alternative versions", e);
                                                }

                                            } else {
                                                if (line.startsWith("Adaptation")) {
                                                    log.debug("Adaptations have been found");

                                                    try {
                                                        manga.setAdaptations(getRelated(manga, elt));
                                                    } catch (Exception e) {
                                                        log.debug("[myanimelist-api] Cannot parse adaptations", e);
                                                    }

                                                } else {
                                                    if (line.startsWith("Summary")) {
                                                        log.debug("Summaries have been found");

                                                        try {
                                                            manga.setSummaries(getRelated(manga, elt));
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
                manga.setSynopsis(div.text().substring(9, div.text().length()));
            }

            if (div.text().startsWith("Background: ")){
                manga.setBackground(div.text().substring(12, div.text().length()));
            }

            if (div.text().startsWith("Synonyms: ")){
                manga.setSynonyms(div.text().substring(10, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("English: ")){
                manga.setEnglishTitle(div.text().substring(9, div.text().length()));
            }

            if (div.text().startsWith("Japanese: ")){
                manga.setJapaneseTitle(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Serialization: ")){
                ((Manga) manga).setSerialization(div.text().substring(14, div.text().length()));
            }

            if (div.text().startsWith("Published: ")){
                String[] fullTime = div.text().substring(11, div.text().length()).split(" to ");

                try {
                    manga.setStartedAiringDate(fullTime[0]);
                    manga.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("[myanimelist-api] Cannot parse airing/finishing dates", e);
                }
            }

            if (div.text().startsWith("Aired: ")){
                String[] fullTime = div.text().substring(7, div.text().length()).split(" to ");

                try {
                    manga.setStartedAiringDate(fullTime[0]);
                    manga.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("[myanimelist-api] Cannot parse airing/finishing dates", e);
                }
            }

            if (div.text().startsWith("Genres: ")){
                manga.setGenres(div.text().substring(8, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("Volumes: ")){
                try{
                    ((Manga) manga).setNbVolumes(Integer.parseInt(div.text().substring(9, div.text().length())));
                }catch (Exception e){
                    log.debug("[myanimelist-api] Cannot parse volumes", e);
                }
            }

            if (div.text().startsWith("Chapters: ")){
                try{
                    ((Manga) manga).setMbChapters(Integer.parseInt(div.text().substring(10, div.text().length())));
                }catch (Exception e){
                    log.debug("[myanimelist-api] Cannot parse chapters", e);
                }
            }

            if (div.text().startsWith("Authors: ")){

                String parts[] = div.text().substring(9, div.text().length()).replace(", ", ",").split(",");

                if (!parts[0].equals("None")) {
                    List<Author> authors = manga.getAuthors();

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

                    manga.setAuthors(authors);
                }
            }

            if (div.text().startsWith("Rating: ")) {
                manga.setAgeRating(div.text().substring(8, div.text().length()));
            }

            if (div.text().startsWith("Score: ")) {
                manga.setScore(div.text().substring(7, div.text().length()));
            }

            if (div.text().startsWith("Ranked: ")) {
                manga.setRank(div.text().substring(8, div.text().length()));
            }

            if (div.text().startsWith("Popularity: ")) {
                manga.setPopularity(div.text().substring(12, div.text().length()));
            }
        }

        pattern = manga.getType().equals("manga") ? "More characters Characters" : "More characters Characters & Voice Actors";

        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().equals("Popular Tags")){
                log.debug("Popular tags have been found");

                manga.setTags(getTags(h2));

            }else{
                if (h2.text().equals(pattern)) {
                    log.debug("Characters have been found");

                    manga.setCharacters(getCharactersBasicInfo(h2));

                }else {
                    //Add authors for anime
                    if (h2.text().startsWith("More staff Staff")) {
                        log.debug("Authors have been found");

                        manga.setAuthors(getAuthorsBasicInfo(h2));
                    }
                }
            }
        }

        return manga;
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
