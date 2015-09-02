package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.UnitCrawler;
import com.github.v4lproik.myanimelist.api.models.Item;
import com.github.v4lproik.myanimelist.api.models.TypeEnum;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.countMatches;

public abstract class AbstractCrawler<ITEM extends Item> implements UnitCrawler<ITEM> {

    public static final String DOMAIN = "http://myanimelist.net/";
    public static final String USER_AGENT = "iMAL-iOS";
    static Logger log = Logger.getLogger(AbstractCrawler.class.getName());

    public abstract ITEM crawl(Integer id) throws IOException;

    public abstract Set<ITEM> crawl(Integer id, Boolean dependency) throws IOException;

    protected Integer getIdFromLink(String link) {
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

    protected String getTypeFromLink(String link) {
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

    protected String getInfoFromLink(String link) {
        try {
            if (link.startsWith("http") || link.startsWith("https"))
                return link.split("/")[3].split("/")[2];
            else
                return link.split("/")[1].split("/")[2];
        } catch (Exception e){
            log.debug(String.format("Error parsing type from link : %s", link));
        }

        return null;
    }

    protected String createEntryURL(Integer id, TypeEnum type){

        if (id == null || type == null){
            throw new IllegalArgumentException("Id or Type cannot be null");
        }

        return DOMAIN + type.toString() + "/" + id.toString() + "/";
    }

    public Document getResultFromJSoup(String url, String type) throws IOException {
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
}
