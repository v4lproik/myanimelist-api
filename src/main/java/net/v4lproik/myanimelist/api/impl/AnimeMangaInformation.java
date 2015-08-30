package net.v4lproik.myanimelist.api.impl;

import net.v4lproik.myanimelist.api.AnimeManga;
import net.v4lproik.myanimelist.api.models.TypeEnum;
import net.v4lproik.myanimelist.entities.*;
import net.v4lproik.myanimelist.entities.Character;
import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.countMatches;

public class AnimeMangaInformation extends AbstractInformation implements AnimeManga {
    List<Integer> animeScrapped = new ArrayList<Integer>();
    List<Integer> animeErrorScrapped = new ArrayList<Integer>();

    Set<EntryDependency> animes = new HashSet<EntryDependency>();

    Entry root;

    EntryFactory entityFactory = new EntryFactory();
    EntryDependencyFactory entityFactoryDependency = new EntryDependencyFactory();

    @Deprecated
    public Anime crawl(String name, TypeEnum typeEnum) throws IOException {
        Anime anime = new Anime();

        final String type = typeEnum.toString();
        final String url = DOMAIN + type + ".php?q=" + name;

        log.debug("Trying to get result from " + url);

        final Connection.Response response = Jsoup.connect(url).userAgent(USER_AGENT).execute();

        //get list or anime ?
        if(response.url().toString().startsWith(DOMAIN + type + "/") && countMatches(response.url().toString(), "/") == 5){
            Document doc = response.parse();
            scrapGeneralInformation(doc, url, type, null);
        }

        return anime;
    }

    public Entry crawl(Integer id, TypeEnum typeEnum) throws IOException {

        final String type = typeEnum.toString();
        final Boolean dependency = false;

        if (id == null) {
            throw new IllegalArgumentException("Both type and id argument cannot be null");
        }

        if (id <= 0){
            throw new IllegalArgumentException("Both type and id argument cannot be empty");
        }

        root = entityFactory.getEntity(type, id);

        if (!dependency){

            String url = createEntryURL(id, typeEnum);
            Document doc = this.getResultFromJSoup(url, type);

            if (doc == null)
                throw new IOException(String.format("No data fetched for url %s", url));

            scrapGeneralInformation(doc, url, type, root);

            return root;
        }

        letsScrap(root);

        return root;
    }

    public Set<EntryDependency> crawlAndDependencies(Integer id, TypeEnum typeEnum) throws IOException {
        final String type = typeEnum.toString();

        if (id == null) {
            throw new IllegalArgumentException("Both type and id argument cannot be null");
        }

        if (id >= 0){
            throw new IllegalArgumentException("Both type and id argument cannot be empty");
        }

        root = entityFactory.getEntity(type, id);
        root.setType(type);

        letsScrap(root);

        return animes;
    }

    private Entry letsScrap(Entry toScrap) throws IOException {

        String url;
        Document doc;

        while (toScrap != null){

            String type = toScrap.getType();
            Integer id = toScrap.getId();

            if (type == null || id == null)
                throw new IllegalArgumentException("Both type and id argument cannot be null");

            if (type.isEmpty() || id.toString().isEmpty())
                throw new IllegalArgumentException("Both type and id argument cannot be empty");

            if (!animeScrapped.contains(toScrap.getId())) {

                url = createEntryURL(id, TypeEnum.fromValue(type));
                doc = getResultFromJSoup(url, type);

                if (doc == null) {
                    animeErrorScrapped.add(id);
                } else {
                    scrapGeneralInformation(doc, url, type, toScrap);
                    animeScrapped.add(id);

                    EntryDependency entryDependency = convertIntoDependencyObject(toScrap);

                    animes.add(entryDependency);
                }
            }

            Entry nextToScrap = whoSNext(toScrap);

            // Tricky part
            if (nextToScrap == null){
                letsScrap(toScrap.getParent());
            }

            toScrap = nextToScrap;
        }

        return root;
    }

    private Entry whoSNext(Entry lastScrapped){

        log.debug(String.format("Animes that have been scrapped %s ", Arrays.asList(animeScrapped)));
        log.debug(String.format("Animes that have been errorscrapped %s ", Arrays.asList(animeErrorScrapped)));


        for (Entry entry : lastScrapped.getAdaptations()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getAlternativeVersions()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getPrequels()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getSequels()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getSideStories()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getOthers()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getSummaries()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (Entry entry : lastScrapped.getSpinoff()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        return null;
    }

    private EntryDependency convertIntoDependencyObject(Entry from){

        EntryDependency entryDependency = entityFactoryDependency.getEntity(from.getType(), from.getId());

        EntryDependencyId myAnimeListAnimeDependencyId = new EntryDependencyId();
        Integer id;
        String title;
        String type;

        //Copy value into MyAnimeListDependency
        try {
            BeanUtils.copyProperties(from, entryDependency);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        for (Entry entity : from.getAdaptations()){

            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getAdaptations().add(myAnimeListAnimeDependencyId);

        }

        for (Entry entity : from.getAlternativeVersions()){
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getAlternativeVersions().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getPrequels()){
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getPrequels().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getSequels()){
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getSequels().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getSideStories()){
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getSideStories().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getOthers()) {
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getOthers().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getSummaries()) {
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getSummaries().add(myAnimeListAnimeDependencyId);
        }

        for (Entry entity : from.getSpinoff()) {
            Entry entry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = entry.getId();
            title = entry.getEnglishTitle();
            type = entry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            entryDependency.getSpinoff().add(myAnimeListAnimeDependencyId);
        }

        return entryDependency;
    }

    public String createEntryURL(Integer id, TypeEnum type){

        if (id == null || type == null){
            throw new IllegalArgumentException("Id or Type cannot be null");
        }

        return DOMAIN + type.toString() + "/" + id.toString() + "/";
    }

    public Entry scrapGeneralInformation(Document doc, String url, String type, Entry entry){

        String pattern;
        Integer id = this.getIdFromLink(url);

        if (doc == null || doc.text().isEmpty()){
            throw new IllegalArgumentException("Document cannot be null or empty");
        }

        if (entry == null){
            entry = entityFactory.getEntity(type, id);
        }

        //get type
        entry.setType(type);

        //get main title
        entry.setTitle(doc.select("h1").first().childNode(2).childNodes().get(0).toString());

        //get image
        entry.setPosterImage(doc.select("meta[property=og:image]").attr("content"));

        //parse for general information
        Elements tds = doc.select("td");
        for (Element td : tds) {
            if (td.text().startsWith("Edit Synopsis")) {
                entry.setSynopsis(td.text().substring(13, td.text().length()));
            }else {
                if (td.text().startsWith("Edit Related")) {

                    Elements elts = td.select("table").select("td");

                    for (Element elt:elts) {

                        String line = elt.text();

                        if (line.startsWith("Sequel:")) {
                            log.debug("Sequel has been found");

                            List<Node> links = elt.parentNode().childNodes();

                            Document docTmp2 = Jsoup.parse(links.get(1).toString());
                            Elements linksX = docTmp2.select("a");

                            for (Element link : linksX) {
                                String linkHref = link.attr("href");
                                String title = link.select("a").text();
                                id = this.getIdFromLink(linkHref);
                                type = this.getTypeFromLink(linkHref);

                                if (id != null) {
                                    List<Entry> sequels = new ArrayList<>();
                                    Entry sequel = entityFactory.getEntity(type, id);
                                    sequel.setId(id);
                                    sequel.setType(type);
                                    sequel.setTitle(title);
                                    sequel.setParent(entry);

                                    //get sequels
                                    sequels = entry.getSequels();
                                    sequels.add(sequel);
                                }
                            }
                        } else {
                            if (line.startsWith("Side story:")) {
                                log.debug("Side Stories have been found");

                                entry.setSideStories(getRelated(entry, elt));

                            } else {
                                if (line.startsWith("Spin-off:")) {
                                    log.debug("Spin Off have been found");

                                    entry.setSpinoff(getRelated(entry, elt));

                                } else {
                                    if (line.startsWith("Other:")) {
                                        log.debug("Others have been found");

                                        entry.setOthers(getRelated(entry, elt));

                                    } else {
                                        if (line.startsWith("Prequel:")) {
                                            log.debug("Prequels have been found");

                                            entry.setPrequels(getRelated(entry, elt));

                                        } else {
                                            if (line.startsWith("Alternative version:")) {
                                                log.debug("Alternative versions have been found");

                                                entry.setAlternativeVersions(getRelated(entry, elt));

                                            } else {
                                                if (line.startsWith("Adaptation")) {
                                                    log.debug("Adaptations have been found");

                                                    entry.setAdaptations(getRelated(entry, elt));

                                                } else {
                                                    if (line.startsWith("Summary")) {
                                                        log.debug("Summaries have been found");

                                                        entry.setSummaries(getRelated(entry, elt));

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

            if (div.text().startsWith("Synopsis: "))
                entry.setSynopsis(div.text().substring(9, div.text().length()));

            if (div.text().startsWith("Background: "))
                entry.setBackground(div.text().substring(12, div.text().length()));

            if (div.text().startsWith("Synonyms: "))
                entry.setSynonyms(div.text().substring(10, div.text().length()).replace(", ", ",").split(","));

            if (div.text().startsWith("English: "))
                entry.setEnglishTitle(div.text().substring(9, div.text().length()));

            if (div.text().startsWith("Japanese: "))
                entry.setJapaneseTitle(div.text().substring(10, div.text().length()));

            if (div.text().startsWith("Episodes: ")){
                Anime anime = (Anime) entry;
                anime.setEpisodeCount(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Serialization: ")){
                Manga manga = (Manga) entry;
                manga.setSerialization(div.text().substring(14, div.text().length()));
            }

            if (div.text().startsWith("Published: ")){
                String[] fullTime = div.text().substring(11, div.text().length()).split(" to ");

                try {
                    entry.setStartedAiringDate(fullTime[0]);
                    entry.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("Error parsing airing/finishing dates");
                }
            }

            if (div.text().startsWith("Aired: ")){
                String[] fullTime = div.text().substring(7, div.text().length()).split(" to ");

                try {
                    entry.setStartedAiringDate(fullTime[0]);
                    entry.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("Error parsing airing/finishing dates");
                }
            }

            if (div.text().startsWith("Producers: ")){
                Anime anime = (Anime) entry;
                anime.setProducers(div.text().substring(11, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("Genres: "))
                entry.setGenres(div.text().substring(8, div.text().length()).replace(", ", ",").split(","));

            if (div.text().startsWith("Volumes: ")){
                Manga manga = (Manga) entry;
                try{
                    manga.setNbVolumes(Integer.parseInt(div.text().substring(9, div.text().length())));
                }catch (Exception e){
                    log.debug("Error parsing volumes number");
                }
            }

            if (div.text().startsWith("Chapters: ")){
                Manga manga = (Manga) entry;
                try{
                    manga.setMbChapters(Integer.parseInt(div.text().substring(10, div.text().length())));
                }catch (Exception e){
                    log.debug("Error parsing chapters number");
                }
            }

            if (div.text().startsWith("Authors: ")){

                String parts[] = div.text().substring(9, div.text().length()).replace(", ", ",").split(",");

                if (!parts[0].equals("None")) {
                    List<Author> authors = entry.getAuthors();

                    for (int i = 0; i < parts.length; i = i + 2) {
                        Author author = new Author();
                        try {
                            author.setFirstName(parts[i]);
                            author.setLastName(parts[i + 1].split(" ")[0]);

                            try {
                                String[] jobsExtracted = parts[i + 1].substring(parts[i + 1].indexOf("(") + 1, parts[i + 1].indexOf(")")).split(" & ");
                                author.setJob(jobsExtracted);
                            }catch (Exception e1){
                                log.debug("Error trying to get author's jobs");
                            }

                        } catch (ArrayIndexOutOfBoundsException e) {
                            author.setLastName(parts[i]);

                            try {
                                String[] jobsExtracted = parts[i].substring(parts[i].indexOf("(") + 1, parts[i].indexOf(")")).split(" & ");
                                author.setJob(jobsExtracted);
                            }catch (Exception e2){
                                log.debug("Error trying to get author's jobs");
                            }
                        }

                        entry.getAuthors().add(author);
                        log.debug(String.format("Add new author %s", author.toString()));
                    }
                }
            }

            if (div.text().startsWith("Duration: ")) {
                Anime anime = (Anime) entry;
                anime.setEpisodeLength(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Rating: "))
                entry.setAgeRating(div.text().substring(8, div.text().length()));

            if (div.text().startsWith("Score: "))
                entry.setScore(div.text().substring(7, div.text().length()));

            if (div.text().startsWith("Ranked: "))
                entry.setRank(div.text().substring(8, div.text().length()));

            if (div.text().startsWith("Popularity: "))
                entry.setPopularity(div.text().substring(12, div.text().length()));

        }

        pattern = entry.getType().equals("manga") ? "More characters Characters" : "More characters Characters & Voice Actors";

        Elements h2s = doc.select("h2");
        for (Element h2 : h2s) {
            if (h2.text().equals("Popular Tags")){
                log.debug("Popular tags have been found");
                Elements els = h2.nextElementSibling().select("span").select("a");

                String[] tags = new String[els.size()];
                int i = 0;
                for (Element tag : els){
                    tags[i] = tag.text();
                    i++;
                }
                entry.setTags(tags);
            }else{
                if (h2.text().equals(pattern)) {
                    log.debug("Characters have been found");

                    entry.setCharacters(getCharactersBasicInfo(h2));

                }else {
                    //Add authors for anime
                    if (h2.text().startsWith("More staff Staff")) {
                        log.debug("Authors have been found");

                        entry.setAuthors(getAuthorsBasicInfo(h2));
                    }
                }
            }
        }

        return entry;
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
        Node current = h2.nextElementSibling();
        Element el = (Element) current;
        List<Character> characters = new ArrayList<>();

        Elements trs = el.select("tr");

        for (Element tr : trs){

            // Get character info
            Character character = new Character();
            try{
                String characterFullName = tr.select("td").get(1).select("a").text();
                Integer idCharacter = getIdFromLink(tr.select("td").get(1).select("a").attr("href"));
                String role = tr.select("td").get(1).select("small").text();

                String[] parts = characterFullName.split(", ");

                if (parts.length == 2){
                    character.setFirstName(parts[1]);
                    character.setLastName(parts[0]);
                }else {
                    if (parts.length == 1)
                        character.setLastName(characterFullName);
                }
                character.setRole(role);
                character.setId(idCharacter);

                characters.add(character);

                log.debug(String.format("Add new character %s", character.toString()));
            }catch (Exception e){
                log.debug("Error trying to get character's name");
            }

            current = current.nextSibling();
        }

        return characters;
    }

    private List<Entry> getRelated(Entry entry, Element elt) {
        String type;
        List<Node> links = elt.parentNode().childNodes().get(3).childNodes();
        List<Entry> relatedEntities = new ArrayList<Entry>();

        for (Node link : links) {
            String linkHref = link.attr("href");

            if (!linkHref.isEmpty()){
                String title = link.childNodes().get(0).toString();
                Integer id = this.getIdFromLink(linkHref);
                type = this.getTypeFromLink(linkHref);

                if (id != null) {

                    Entry related = entityFactory.getEntity(type, id);
                    related.setId(id);
                    related.setType(type);
                    related.setTitle(title);
                    related.setParent(entry);

                    //get sequels
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
