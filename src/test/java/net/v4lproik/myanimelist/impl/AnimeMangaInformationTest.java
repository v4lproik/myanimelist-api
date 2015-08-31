package net.v4lproik.myanimelist.impl;

import net.v4lproik.myanimelist.api.impl.AnimeMangaInformation;
import net.v4lproik.myanimelist.api.models.TypeEnum;
import net.v4lproik.myanimelist.entities.Entry;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class AnimeMangaInformationTest {
    // Grab new data straight from myanimelist
    private final Boolean CRAWL_WEBSITE = false;

    @Spy
    private AnimeMangaInformation service;

    @Test
    public void testCrawlById_withGoodOptionsAnime_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        Integer id = 2904;
        String url = service.createEntryURL(id, type);

        Document doc = null;
        if (CRAWL_WEBSITE){
            doc = service.getResultFromJSoup("http://myanimelist.net/anime/2904/", "anime");
            if (doc != null){
                Files.write(Paths.get("src/test/resource/code-geass-r2.anime"), doc.html().getBytes());
            }else{
                throw new IOException("Unit Test cannot be performed");
            }
        }

        File input = new File("src/test/resource/code-geass-r2.anime");
        doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(service).getResultFromJSoup(url, type.toString());

        // When
        Entry response = service.crawl(id, type);


        //Then
        assertEquals("Code Geass: Hangyaku no Lelouch R2", response.getTitle());
        assertEquals("2904", response.getId().toString());
        assertEquals("anime", response.getType());
        assertEquals("Code Geass: Lelouch of the Rebellion R2", response.getEnglishTitle());
        assertEquals("コードギアス 反逆のルルーシュ 続編", response.getJapaneseTitle());
        assertEquals(true, response.getSynopsis().startsWith("A year has passed since \"The Black Rebellion\" and the remaining Black Knights have vanished into the shadows, their leader and"));
        assertEquals("Apr 6, 2008", response.getStartedAiringDate());
        assertEquals("Sep 28, 2008", response.getFinishedAiringDate());
        assertEquals(true, NumberUtils.isNumber(response.getRank().split("#")[1].split(" ")[0]));
        assertEquals(true, NumberUtils.isNumber(response.getScore().split(" ")[0]));
        assertEquals(true, response.getAgeRating().length() > 0);
        assertEquals(true, response.getGenres().length > 0);
        assertEquals(true, response.getPosterImage().length() > 0);
        assertEquals(true, response.getAuthors().size() > 0);
        assertEquals(true, response.getAdaptations().size() > 0);
        assertEquals(true, response.getPrequels().size() > 0);
        assertEquals(true, response.getSideStories().size() > 0);
        assertEquals(true, response.getSummaries().size() > 0);
        assertEquals(true, response.getCharacters().size() > 0);
    }

    @Test
    public void testCrawlById_withGoodOptionsAnime2_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        Integer id = 20;
        String url = service.createEntryURL(id, type);

        Document doc = null;
        if (CRAWL_WEBSITE){
            doc = service.getResultFromJSoup("http://myanimelist.net/anime/20/", "anime");
            if (doc != null){
                Files.write(Paths.get("src/test/resource/naruto.anime"), doc.html().getBytes());
            }else{
                throw new IOException("Unit Test cannot be performed");
            }
        }

        File input = new File("src/test/resource/naruto.anime");
        doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(service).getResultFromJSoup(url, type.toString());

        // When
        Entry response = service.crawl(id, type);


        //Then
        assertEquals("Naruto", response.getTitle());
        assertEquals("20", response.getId().toString());
        assertEquals("anime", response.getType());
    }


    @Test
    public void testCrawlById_withGoodOptionsManga_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = 11;
        String url = service.createEntryURL(id, type);

        Document doc = null;
        if (CRAWL_WEBSITE){
            doc = service.getResultFromJSoup("http://myanimelist.net/manga/11/", "manga");
            if (doc != null){
                Files.write(Paths.get("src/test/resource/naruto.manga"), doc.html().getBytes());
            }else{
                throw new IOException("Unit Test cannot be performed");
            }
        }

        File input = new File("src/test/resource/naruto.manga");
        doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(service).getResultFromJSoup(url, type.toString());

        // When
        Entry response = service.crawl(id, type);

        //Then
        assertEquals("Naruto", response.getTitle());
        assertEquals("manga", response.getType());
        assertEquals("http://cdn.myanimelist.net/images/manga/3/117681.jpg", response.getPosterImage());
        assertEquals("NARUTO -ナルト-", response.getJapaneseTitle());
        assertEquals(true, !response.getSynopsis().isEmpty());
        assertEquals("Sep 21, 1999", response.getStartedAiringDate());
        assertEquals("Nov 10, 2014", response.getFinishedAiringDate());
        assertEquals(true, NumberUtils.isNumber(response.getRank().split("#")[1].split(" ")[0]));
        assertEquals(true, NumberUtils.isNumber(response.getScore().split(" ")[0]));
        assertEquals(true, response.getAgeRating() == null);
        assertEquals(true, response.getGenres().length > 0);
        assertEquals(true, response.getPosterImage().length() > 0);
        assertEquals(true, response.getAuthors().size() > 0);
        assertEquals(true, response.getAlternativeVersions().size() > 0);
        assertEquals(true, response.getSpinoff().size() > 0);
        assertEquals(true, response.getSideStories().size() > 0);
        assertEquals(true, response.getCharacters().size() > 0);
    }

    @Test(expected = IOException.class)
    public void testCrawlById_withGoodOptions_withNoDataToCrawl_shouldThrowIOException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        final Integer id = 5081;
        String url = service.createEntryURL(id, type);

        doReturn(null).when(service).getResultFromJSoup(url, type.toString());

        // When
        Entry response = service.crawl(id, type);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withMissingOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = null;
        final Integer id = 5081;
        String url = service.createEntryURL(id, type);

        // When
        Entry response = service.crawl(id, type);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withIllegalIntegerOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        final Integer id = -1;
        String url = service.createEntryURL(id, type);

        // When
        Entry response = service.crawl(id, type);
    }
}