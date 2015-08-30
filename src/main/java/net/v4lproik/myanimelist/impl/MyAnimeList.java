package net.v4lproik.myanimelist.impl;

import net.v4lproik.myanimelist.api.ImportOptions;
import net.v4lproik.myanimelist.api.WebsiteAbstract;
import net.v4lproik.myanimelist.entities.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
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

public class MyAnimeList extends WebsiteAbstract {

    public static final String DOMAIN = "http://myanimelist.net/";
    public static final String USER_AGENT = "iMAL-iOS";
    static Logger log = Logger.getLogger(MyAnimeList.class.getName());
    List<Integer> animeScrapped = new ArrayList<Integer>();
    List<Integer> animeErrorScrapped = new ArrayList<Integer>();

    Set<MyAnimeListEntryDependency> animes = new HashSet<MyAnimeListEntryDependency>();

    MyAnimeListEntry root;

    MyAnimeListEntryFactory entityFactory = new MyAnimeListEntryFactory();
    MyAnimeListEntryDependencyFactory entityFactoryDependency = new MyAnimeListEntryDependencyFactory();

    @Override
    @Deprecated
    public MyAnimeListAnime crawl(ImportOptions opts) throws IOException {
        MyAnimeListAnime myAnimeListAnime = new MyAnimeListAnime();

        final String type = opts.getType();
        final String name = opts.getQuery();
        final String url = DOMAIN + type + ".php?q=" + name;

        log.debug("Trying to get result from " + url);

        final Connection.Response response = Jsoup.connect(url).userAgent(USER_AGENT).execute();

        //get list or anime ?
        if(response.url().toString().startsWith(DOMAIN + type + "/") && countMatches(response.url().toString(), "/") == 5){
            Document doc = response.parse();
            scrapGeneralInformation(doc, url, type, null);
        }

        return myAnimeListAnime;
    }

    @Override
    public MyAnimeListEntry crawlById(ImportOptions opts) throws IOException {

        final String type = opts.getType();
        final Integer id = opts.getId();
        final Boolean dependency = opts.getDependency();

        if (type == null || id == null)
            throw new IllegalArgumentException("Both type and id argument cannot be null");

        if (type.isEmpty() || id.toString().isEmpty())
            throw new IllegalArgumentException("Both type and id argument cannot be empty");


        root = entityFactory.getEntity(type, id);

        if (!dependency){

            String url = createEntryURL(id, type);
            Document doc = this.getResultFromJSoup(url, type);

            if (doc == null)
                throw new IOException(String.format("No data fetched for url %s", url));

            scrapGeneralInformation(doc, url, type, root);

            return root;
        }

        letsScrap(root);

        return root;
    }

    @Override
    public Set<MyAnimeListEntryDependency> crawlByIdList(ImportOptions opts) throws IOException {

        final String type = opts.getType();
        final Integer id = opts.getId();

        if (type == null || id == null)
            throw new IllegalArgumentException("Both type and id argument cannot be null");

        if (type.isEmpty() || id.toString().isEmpty())
            throw new IllegalArgumentException("Both type and id argument cannot be empty");

        root = entityFactory.getEntity(type, id);
        root.setType(type);

        letsScrap(root);

        return animes;
    }

    protected MyAnimeListEntry letsScrap(MyAnimeListEntry toScrap) throws IOException {

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

                url = createEntryURL(id, type);
                doc = getResultFromJSoup(url, type);

                if (doc == null) {
                    animeErrorScrapped.add(id);
                } else {
                    scrapGeneralInformation(doc, url, type, toScrap);
                    animeScrapped.add(id);

                    MyAnimeListEntryDependency myAnimeListEntryDependency = convertIntoDependencyObject(toScrap);

                    animes.add(myAnimeListEntryDependency);
                }

            }

            MyAnimeListEntry nextToScrap = whoSNext(toScrap);

            // Tricky part
            if (nextToScrap == null){
                letsScrap(toScrap.getParent());
            }

            toScrap = nextToScrap;
        }

        return root;
    }

    protected Document getResultFromJSoup(String url, String type) throws IOException {
        log.debug("Trying to get result from " + url);

        try {
            //delay between 0 and 5s
            long delay = (0 + new Random().nextInt(5)) * 1000;
            Integer seconds = (int) (delay / 1000) % 60 ;
            log.debug(String.format("Delay : %ss", seconds.toString()));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Connection.Response response = Jsoup.connect(url).userAgent(USER_AGENT).execute();

        Document doc = null;

        if(response.url().toString().startsWith(DOMAIN + type + "/") && countMatches(response.url().toString(), "/") == 5){
            doc = response.parse();
        }

        log.debug(String.format("Response url %s", response.url()));

        return doc;
    }

    protected MyAnimeListEntry whoSNext(MyAnimeListEntry lastScrapped){

        log.debug(String.format("Animes that have been scrapped %s ", Arrays.asList(animeScrapped)));
        log.debug(String.format("Animes that have been errorscrapped %s ", Arrays.asList(animeErrorScrapped)));


        for (MyAnimeListEntry entry : lastScrapped.getAdaptations()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getAlternativeVersions()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getPrequels()){
            Integer id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getSequels()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getSideStories()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getOthers()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getSummaries()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        for (MyAnimeListEntry entry : lastScrapped.getSpinoff()){
            int id = entry.getId();

            if (!animeScrapped.contains(id) && !animeErrorScrapped.contains(id)){
                return entry;
            }
        }

        return null;
    }

    protected MyAnimeListEntryDependency convertIntoDependencyObject(MyAnimeListEntry from){

        MyAnimeListEntryDependency myAnimeListEntryDependency = entityFactoryDependency.getEntity(from.getType(), from.getId());

        MyAnimeListEntryDependencyId myAnimeListAnimeDependencyId = new MyAnimeListEntryDependencyId();
        Integer id;
        String title;
        String type;

        //Copy value into MyAnimeListDependency
        try {
            BeanUtils.copyProperties(from, myAnimeListEntryDependency);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        for (MyAnimeListEntry entity : from.getAdaptations()){

            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getAdaptations().add(myAnimeListAnimeDependencyId);

        }

        for (MyAnimeListEntry entity : from.getAlternativeVersions()){
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getAlternativeVersions().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getPrequels()){
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getPrequels().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getSequels()){
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getSequels().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getSideStories()){
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getSideStories().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getOthers()) {
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getOthers().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getSummaries()) {
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getSummaries().add(myAnimeListAnimeDependencyId);
        }

        for (MyAnimeListEntry entity : from.getSpinoff()) {
            MyAnimeListEntry myAnimeListEntry = entityFactory.getEntity(entity.getType(), entity.getId());

            id = myAnimeListEntry.getId();
            title = myAnimeListEntry.getEnglishTitle();
            type = myAnimeListEntry.getType();

            myAnimeListAnimeDependencyId.setId(id);
            myAnimeListAnimeDependencyId.setEnglishTitle(title);
            myAnimeListAnimeDependencyId.setType(type);

            myAnimeListEntryDependency.getSpinoff().add(myAnimeListAnimeDependencyId);
        }

        return myAnimeListEntryDependency;
    }

    public Integer getIdFromLink(String link) {
        try {
            if (link.startsWith("http") || link.startsWith("https"))
                return Integer.parseInt(link.split("/")[4].split("/")[0]);
            else
                return Integer.parseInt(link.split("/")[2].split("/")[0]);
        } catch (Exception e){
            log.debug(String.format("Error parsing id from link : %s", link));
        }

        return null;
    }

    public String getTypeFromLink(String link) {
        try {
            if (link.startsWith("http") || link.startsWith("https"))
                return link.split("/")[3].split("/")[0];
            else
                return link.split("/")[1].split("/")[0];
        } catch (Exception e){
            log.debug(String.format("Error parsing type from link : %s", link));
        }

        return null;
    }

    public String createEntryURL(Integer id, String type){

        if (id == null || type == null){
            throw new IllegalArgumentException("Id or Type cannot be null");
        }

        return DOMAIN + type.toString() + "/" + id.toString() + "/";
    }

    public String createCharacterURL(Integer id){

        if (id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return DOMAIN + "character" + "/" + id.toString() + "/";
    }

    public MyAnimeListEntry scrapGeneralInformation(Document doc, String url, String type, MyAnimeListEntry myAnimeListEntry){

        String pattern;
        Integer id = this.getIdFromLink(url);

        if (doc == null || doc.text().isEmpty()){
            throw new IllegalArgumentException("Document cannot be null or empty");
        }

        if (myAnimeListEntry == null){
            myAnimeListEntry = entityFactory.getEntity(type, id);
        }

        //get type
        myAnimeListEntry.setType(type);

        //get main title
        myAnimeListEntry.setTitle(doc.select("h1").first().childNode(2).childNodes().get(0).toString());

        //get image
        myAnimeListEntry.setPosterImage(doc.select("meta[property=og:image]").attr("content"));

        //parse for general information
        Elements tds = doc.select("td");
        for (Element td : tds) {
            if (td.text().startsWith("Edit Synopsis")) {
                myAnimeListEntry.setSynopsis(td.text().substring(13, td.text().length()));
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
                                    List<MyAnimeListEntry> sequels = new ArrayList<>();
                                    MyAnimeListEntry sequel = entityFactory.getEntity(type, id);
                                    sequel.setId(id);
                                    sequel.setType(type);
                                    sequel.setTitle(title);
                                    sequel.setParent(myAnimeListEntry);

                                    //get sequels
                                    sequels = myAnimeListEntry.getSequels();
                                    sequels.add(sequel);
                                }
                            }
                        } else {
                            if (line.startsWith("Side story:")) {
                                log.debug("Side Stories have been found");

                                myAnimeListEntry.setSideStories(getRelated(myAnimeListEntry, elt));

                            } else {
                                if (line.startsWith("Spin-off:")) {
                                    log.debug("Spin Off have been found");

                                    myAnimeListEntry.setSpinoff(getRelated(myAnimeListEntry, elt));

                                } else {
                                    if (line.startsWith("Other:")) {
                                        log.debug("Others have been found");

                                        myAnimeListEntry.setOthers(getRelated(myAnimeListEntry, elt));

                                    } else {
                                        if (line.startsWith("Prequel:")) {
                                            log.debug("Prequels have been found");

                                            myAnimeListEntry.setPrequels(getRelated(myAnimeListEntry, elt));

                                        } else {
                                            if (line.startsWith("Alternative version:")) {
                                                log.debug("Alternative versions have been found");

                                                myAnimeListEntry.setAlternativeVersions(getRelated(myAnimeListEntry, elt));

                                            } else {
                                                if (line.startsWith("Adaptation")) {
                                                    log.debug("Adaptations have been found");

                                                    myAnimeListEntry.setAdaptations(getRelated(myAnimeListEntry, elt));

                                                } else {
                                                    if (line.startsWith("Summary")) {
                                                        log.debug("Summaries have been found");

                                                        myAnimeListEntry.setSummaries(getRelated(myAnimeListEntry, elt));

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
                myAnimeListEntry.setSynopsis(div.text().substring(9, div.text().length()));

            if (div.text().startsWith("Background: "))
                myAnimeListEntry.setBackground(div.text().substring(12, div.text().length()));

            if (div.text().startsWith("Synonyms: "))
                myAnimeListEntry.setSynonyms(div.text().substring(10, div.text().length()).replace(", ", ",").split(","));

            if (div.text().startsWith("English: "))
                myAnimeListEntry.setEnglishTitle(div.text().substring(9, div.text().length()));

            if (div.text().startsWith("Japanese: "))
                myAnimeListEntry.setJapaneseTitle(div.text().substring(10, div.text().length()));

            if (div.text().startsWith("Episodes: ")){
                MyAnimeListAnime anime = (MyAnimeListAnime) myAnimeListEntry;
                anime.setEpisodeCount(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Serialization: ")){
                MyAnimeListManga manga = (MyAnimeListManga) myAnimeListEntry;
                manga.setSerialization(div.text().substring(14, div.text().length()));
            }

            if (div.text().startsWith("Published: ")){
                String[] fullTime = div.text().substring(11, div.text().length()).split(" to ");

                try {
                    myAnimeListEntry.setStartedAiringDate(fullTime[0]);
                    myAnimeListEntry.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("Error parsing airing/finishing dates");
                }
            }

            if (div.text().startsWith("Aired: ")){
                String[] fullTime = div.text().substring(7, div.text().length()).split(" to ");

                try {
                    myAnimeListEntry.setStartedAiringDate(fullTime[0]);
                    myAnimeListEntry.setFinishedAiringDate(fullTime[1]);
                }catch(Exception e){
                    log.debug("Error parsing airing/finishing dates");
                }
            }

            if (div.text().startsWith("Producers: ")){
                MyAnimeListAnime anime = (MyAnimeListAnime) myAnimeListEntry;
                anime.setProducers(div.text().substring(11, div.text().length()).replace(", ", ",").split(","));
            }

            if (div.text().startsWith("Genres: "))
                myAnimeListEntry.setGenres(div.text().substring(8, div.text().length()).replace(", ", ",").split(","));

            if (div.text().startsWith("Volumes: ")){
                MyAnimeListManga manga = (MyAnimeListManga) myAnimeListEntry;
                try{
                    manga.setNbVolumes(Integer.parseInt(div.text().substring(9, div.text().length())));
                }catch (Exception e){
                    log.debug("Error parsing volumes number");
                }
            }

            if (div.text().startsWith("Chapters: ")){
                MyAnimeListManga manga = (MyAnimeListManga) myAnimeListEntry;
                try{
                    manga.setMbChapters(Integer.parseInt(div.text().substring(10, div.text().length())));
                }catch (Exception e){
                    log.debug("Error parsing chapters number");
                }
            }

            if (div.text().startsWith("Authors: ")){

                String parts[] = div.text().substring(9, div.text().length()).replace(", ", ",").split(",");

                if (!parts[0].equals("None")) {
                    List<MyAnimeListAuthor> authors = myAnimeListEntry.getAuthors();

                    for (int i = 0; i < parts.length; i = i + 2) {
                        MyAnimeListAuthor author = new MyAnimeListAuthor();
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

                        myAnimeListEntry.getAuthors().add(author);
                        log.debug(String.format("Add new author %s", author.toString()));
                    }
                }
            }

            if (div.text().startsWith("Duration: ")) {
                MyAnimeListAnime anime = (MyAnimeListAnime) myAnimeListEntry;
                anime.setEpisodeLength(div.text().substring(10, div.text().length()));
            }

            if (div.text().startsWith("Rating: "))
                myAnimeListEntry.setAgeRating(div.text().substring(8, div.text().length()));

            if (div.text().startsWith("Score: "))
                myAnimeListEntry.setScore(div.text().substring(7, div.text().length()));

            if (div.text().startsWith("Ranked: "))
                myAnimeListEntry.setRank(div.text().substring(8, div.text().length()));

            if (div.text().startsWith("Popularity: "))
                myAnimeListEntry.setPopularity(div.text().substring(12, div.text().length()));

        }

        pattern = myAnimeListEntry.getType().equals("manga") ? "More characters Characters" : "More characters Characters & Voice Actors";

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
                myAnimeListEntry.setTags(tags);
            }else{
                if (h2.text().equals(pattern)) {
                    log.debug("Characters have been found");

                    myAnimeListEntry.setCharacters(getCharactersBasicInfo(h2));

                }else {
                    //Add authors for anime
                    if (h2.text().startsWith("More staff Staff")) {
                        log.debug("Authors have been found");

                        myAnimeListEntry.setAuthors(getAuthorsBasicInfo(h2));
                    }
                }
            }
        }

        return myAnimeListEntry;
    }

    private List<MyAnimeListAuthor> getAuthorsBasicInfo(Element h2) {
        Node current = h2.nextElementSibling();
        Element el = (Element) current;
        List<MyAnimeListAuthor> authors = new ArrayList<>();

        Elements trs = el.select("tr");

        for (Element tr : trs){

            // Get character info
            MyAnimeListAuthor author = new MyAnimeListAuthor();
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

    private List<MyAnimeListCharacter> getCharactersBasicInfo(Element h2) {
        Node current = h2.nextElementSibling();
        Element el = (Element) current;
        List<MyAnimeListCharacter> characters = new ArrayList<>();

        Elements trs = el.select("tr");

        for (Element tr : trs){

            // Get character info
            MyAnimeListCharacter character = new MyAnimeListCharacter();
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

    private List<MyAnimeListEntry> getRelated(MyAnimeListEntry myAnimeListEntry, Element elt) {
        String type;
        List<Node> links = elt.parentNode().childNodes().get(3).childNodes();
        List<MyAnimeListEntry> relatedEntities = new ArrayList<MyAnimeListEntry>();

        for (Node link : links) {
            String linkHref = link.attr("href");

            if (!linkHref.isEmpty()){
                String title = link.childNodes().get(0).toString();
                Integer id = this.getIdFromLink(linkHref);
                type = this.getTypeFromLink(linkHref);

                if (id != null) {

                    MyAnimeListEntry related = entityFactory.getEntity(type, id);
                    related.setId(id);
                    related.setType(type);
                    related.setTitle(title);
                    related.setParent(myAnimeListEntry);

                    //get sequels
                    relatedEntities.add(related);
                }
            }
        }

        return relatedEntities;
    }

    public MyAnimeListCharacter scrapCharacter(Document doc, String url, MyAnimeListCharacter character){

        if (doc == null){
            throw new IllegalArgumentException("Document cannot be null");
        }

        if (character == null){
            character = new MyAnimeListCharacter();
        }

        Elements base = doc.body().select("#myanimelist").select("#contentWrapper");

        String header = base.select("h1").first().text();

        try {
            String nicknames = header.split("\"")[1];
            character.setNickNames(nicknames.split(","));
        }catch (Exception e){
            log.debug("Error trying to get character's nicknames");
        }

        Elements content = base.select("#content").select("table").first().select("tbody").first().select("tr").first()
                .select("td[style=padding-left: 5px;]");

        String allNames = content.select(".normal_header").text();

        try {
            String japaneseName = allNames.split("\\(")[1].split("\\)")[0];
            character.setJapaneseName(japaneseName);
        }catch (Exception e){
            log.debug("Error trying to get character's japanese name");
        }

        return character;
    }

    @Override
    public void call() {
        throw new NotImplementedException();
    }
}
