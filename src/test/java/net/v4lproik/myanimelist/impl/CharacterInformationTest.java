package net.v4lproik.myanimelist.impl;

import net.v4lproik.myanimelist.api.impl.AnimeMangaInformation;
import net.v4lproik.myanimelist.api.impl.CharacterInformation;
import net.v4lproik.myanimelist.api.models.TypeEnum;
import net.v4lproik.myanimelist.entities.Entry;
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
public class CharacterInformationTest{

    @Spy
    private AnimeMangaInformation serviceAnimeManga;

    @Spy
    private CharacterInformation serviceCharacter;

    @Test
    public void test_crawlCharacter_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = 11;
        String url = serviceAnimeManga.createEntryURL(id, type);

        File input = new File("src/test/resource/naruto.manga");
        Document doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(serviceAnimeManga).getResultFromJSoup(url, type.toString());

        // When
        Entry response = serviceAnimeManga.crawl(id, type);


        input = new File("src/test/resource/naruto-uzumaki.character");
        doc = Jsoup.parse(input, "UTF-8", url);


        Integer idCharacter = response.getCharacters().get(0).getId();
        url = serviceCharacter.createCharacterURL(idCharacter);

        doReturn(doc).when(serviceCharacter).getResultFromJSoup(url, serviceCharacter.CHARACTER_TYPE);

        // When
        net.v4lproik.myanimelist.entities.Character responseCharacter = serviceCharacter.crawl(idCharacter);

        //Then
        assertEquals("Naruto", response.getTitle());
        assertEquals("http://cdn.myanimelist.net/images/manga/3/117681.jpg", response.getPosterImage());
        assertEquals("うずまきナルト", responseCharacter.getJapaneseName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_crawlCharacter_withBadInteger_shouldReturnNOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = -1;
        String url = serviceAnimeManga.createEntryURL(id, type);

        // When
        net.v4lproik.myanimelist.entities.Character responseCharacter = serviceCharacter.crawl(id);
    }
}