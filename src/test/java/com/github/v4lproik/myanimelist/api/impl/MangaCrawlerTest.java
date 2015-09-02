package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.models.Manga;
import com.github.v4lproik.myanimelist.api.models.TypeEnum;
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
public class MangaCrawlerTest {
    // Grab new data straight from myanimelist
    private final Boolean CRAWL_WEBSITE = false;

    @Spy
    private MangaCrawler mangaCrawler;

    @Test
    public void testCrawlById_withGoodOptionsManga_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = 11;
        String url = mangaCrawler.createEntryURL(id, type);

        Document doc = null;
        if (CRAWL_WEBSITE){
            doc = mangaCrawler.getResultFromJSoup("http://myanimelist.net/manga/11/", "manga");
            if (doc != null){
                Files.write(Paths.get("src/test/resource/naruto.manga"), doc.html().getBytes());
            }else{
                throw new IOException("Unit Test cannot be performed");
            }
        }

        File input = new File("src/test/resource/naruto.manga");
        doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(mangaCrawler).getResultFromJSoup(url, type.toString());

        // When
        Manga manga = mangaCrawler.crawl(id);


        //Then
        assertEquals("Naruto", manga.getTitle());
        assertEquals("manga", manga.getType());
        assertEquals(manga.getMbChapters(), new Integer(700));
        assertEquals("manga", manga.getType());
        assertEquals("http://cdn.myanimelist.net/images/manga/3/117681.jpg", manga.getPosterImage());
        assertEquals("NARUTO -ナルト-", manga.getJapaneseTitle());
        assertEquals(true, !manga.getSynopsis().isEmpty());
        assertEquals("Sep 21, 1999", manga.getStartedAiringDate());
        assertEquals("Nov 10, 2014", manga.getFinishedAiringDate());
        assertEquals(true, NumberUtils.isNumber(manga.getRank().split("#")[1].split(" ")[0]));
        assertEquals(true, NumberUtils.isNumber(manga.getScore().split(" ")[0]));
        assertEquals(true, manga.getAgeRating() == null);
        assertEquals(true, manga.getGenres().length > 0);
        assertEquals(true, manga.getPosterImage().length() > 0);
        assertEquals(true, manga.getAuthors().size() > 0);
        assertEquals(true, manga.getAlternativeVersions().size() > 0);
        assertEquals(true, manga.getSpinoff().size() > 0);
        assertEquals(true, manga.getSideStories().size() > 0);
        assertEquals(true, manga.getCharacters().size() > 0);
    }

    @Test(expected = IOException.class)
    public void testCrawlById_withGoodOptions_withNoDataToCrawl_shouldThrowIOException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = 5081;
        String url = mangaCrawler.createEntryURL(id, type);

        doReturn(null).when(mangaCrawler).getResultFromJSoup(url, type.toString());

        // When
        Manga manga = mangaCrawler.crawl(id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withMissingOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = null;
        final Integer id = 5081;
        String url = mangaCrawler.createEntryURL(id, type);

        // When
        Manga manga = mangaCrawler.crawl(id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withIllegalIntegerOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.MANGA;
        final Integer id = -1;
        String url = mangaCrawler.createEntryURL(id, type);

        // When
        Manga manga = mangaCrawler.crawl(id);
    }
}