package net.v4lproik.myanimelist.api.impl;

import net.v4lproik.myanimelist.api.Character;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CharacterInformation extends AbstractInformation implements Character {

    public final String CHARACTER_TYPE = "character";

    public net.v4lproik.myanimelist.entities.Character crawl(Integer id) throws IOException {

        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (id <= 0){
            throw new IllegalArgumentException("id cannot be empty");
        }

        final String type = CHARACTER_TYPE;
        final String url = createCharacterURL(id);
        Document doc = this.getResultFromJSoup(url, type);

        if (doc == null){
            throw new IOException(String.format("No data fetched for url %s", url));
        }

        return scrapCharacter(doc, null);
    }

    private net.v4lproik.myanimelist.entities.Character scrapCharacter(Document doc, net.v4lproik.myanimelist.entities.Character character){

        if (doc == null){
            throw new IllegalArgumentException("Document cannot be null");
        }

        if (character == null){
            character = new net.v4lproik.myanimelist.entities.Character();
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

    public String createCharacterURL(Integer id){

        if (id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return DOMAIN + CHARACTER_TYPE + "/" + id.toString() + "/";
    }
}
