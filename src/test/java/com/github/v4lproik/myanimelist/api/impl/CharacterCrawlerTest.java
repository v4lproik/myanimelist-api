package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.models.Character;
import com.github.v4lproik.myanimelist.api.models.Manga;
import com.github.v4lproik.myanimelist.api.models.TypeEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CharacterCrawlerTest {

    @Spy
    private MangaCrawler mangaCrawler;

    @Spy
    private CharacterCrawler characterCrawler;


    @Test
    public void test_crawlCharacter_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = 11;
        String url = mangaCrawler.createEntryURL(id, type);

        File input = new File("src/test/resource/naruto.manga");
        Document doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(mangaCrawler).getResultFromJSoup(url, type.toString());

        // When
        Manga manga = mangaCrawler.crawl(id);

        input = new File("src/test/resource/naruto-uzumaki.character");
        doc = Jsoup.parse(input, "UTF-8", url);


        Integer idCharacter = manga.getCharacters().get(0).getId();
        url = characterCrawler.createCharacterURL(idCharacter);

        doReturn(doc).when(characterCrawler).getResultFromJSoup(url, characterCrawler.CHARACTER_TYPE);

        // When
        com.github.v4lproik.myanimelist.api.models.Character responseCharacter = characterCrawler.crawl(idCharacter);

        //Then
        assertEquals("Naruto", manga.getTitle());
        assertEquals("http://cdn.myanimelist.net/images/manga/3/117681.jpg", manga.getPosterImage());
        assertEquals("うずまきナルト", responseCharacter.getJapaneseName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_crawlCharacter_withBadInteger_shouldReturnNOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = -1;
        String url = mangaCrawler.createEntryURL(id, type);

        // When
        Character responseCharacter = characterCrawler.crawl(id);
    }
}