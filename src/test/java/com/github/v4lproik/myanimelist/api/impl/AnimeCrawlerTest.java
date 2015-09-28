package com.github.v4lproik.myanimelist.api.impl;

import com.github.v4lproik.myanimelist.api.models.Anime;
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
public class AnimeCrawlerTest {
    // Grab new data straight from myanimelist
    private final Boolean CRAWL_WEBSITE = true;

    @Spy
    private AnimeCrawler animeCrawler;

    @Test
    public void testCrawlById_withGoodOptionsAnime_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        Integer id = 2904;
        String url = animeCrawler.createEntryURL(id, type);

//        Document doc = null;
//        if (CRAWL_WEBSITE){
//            doc = animeCrawler.getResultFromJSoup("http://myanimelist.net/anime/2904/", "anime");
//            if (doc != null){
//                Path logFile = Paths.get("src/test/resource/code-geass-r2.anime");
//                try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_16)) {
//                    writer.write(doc.html());
//                }
//            }else{
//                throw new IOException("Unit Test cannot be performed");
//            }
//        }
//
//        File input = new File("src/test/resource/code-geass-r2.anime");
//        doc = Jsoup.parse(input, "UTF-16", url);
//
//        doReturn(doc).when(animeCrawler).getResultFromJSoup(url, type.toString());

        // When
        Anime anime = animeCrawler.crawl(id);

        //Then
        assertEquals("Code Geass: Hangyaku no Lelouch R2", anime.getTitle());
        assertEquals("2904", anime.getId().toString());
        assertEquals("anime", anime.getType());
        assertEquals("TV", anime.getShowType());
        assertEquals("Code Geass: Lelouch of the Rebellion R2", anime.getEnglishTitle());
        assertEquals("コードギアス 反逆のルルーシュ 続編", anime.getJapaneseTitle());
        assertEquals(true, anime.getSynopsis().startsWith("A year has passed since \"The Black Rebellion\" and the remaining Black Knights have vanished into the shadows, their leader and"));
        assertEquals("Apr 6, 2008", anime.getStartedAiringDate());
        assertEquals("Sep 28, 2008", anime.getFinishedAiringDate());
        assertEquals(true, NumberUtils.isNumber(anime.getRank().split("#")[1].split(" ")[0]));
        assertEquals(true, NumberUtils.isNumber(anime.getScore().split(" ")[0]));
        assertEquals(true, anime.getAgeRating().length() > 0);
        assertEquals(true, anime.getGenres().length > 0);
        assertEquals(true, anime.getPosterImage().length() > 0);
        assertEquals(true, anime.getAuthors().size() > 0);
        assertEquals(true, anime.getAdaptations().size() > 0);
        assertEquals(true, anime.getPrequels().size() > 0);
        assertEquals(true, anime.getSideStories().size() > 0);
        assertEquals(true, anime.getSummaries().size() > 0);
        assertEquals(true, anime.getCharacters().size() > 0);
    }

    @Test
    public void testCrawlById_withGoodOptionsAnime2_shouldBeOK() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        Integer id = 20;
        String url = animeCrawler.createEntryURL(id, type);

        Document doc = null;
        if (CRAWL_WEBSITE){
            doc = animeCrawler.getResultFromJSoup("http://myanimelist.net/anime/20/", "anime");
            if (doc != null){
                Files.write(Paths.get("src/test/resource/naruto.anime"), doc.html().getBytes());
            }else{
                throw new IOException("Unit Test cannot be performed");
            }
        }

        File input = new File("src/test/resource/naruto.anime");
        doc = Jsoup.parse(input, "UTF-8", url);

        doReturn(doc).when(animeCrawler).getResultFromJSoup(url, type.toString());

        // When
        Anime anime = animeCrawler.crawl(id);


        //Then
        assertEquals("Naruto", anime.getTitle());
        assertEquals("20", anime.getId().toString());
        assertEquals("anime", anime.getType());
        assertEquals(true, anime.getAgeRating().length() > 0);
        assertEquals(true, anime.getGenres().length > 0);
        assertEquals(true, anime.getPosterImage().length() > 0);
        assertEquals(true, anime.getAuthors().size() > 0);
        assertEquals(true, anime.getSequels().size() > 0);
        assertEquals(true, anime.getAdaptations().size() > 0);
        assertEquals(true, anime.getSideStories().size() > 0);
        assertEquals(true, anime.getCharacters().size() > 0);
    }

    @Test(expected = IOException.class)
    public void testCrawlById_withGoodOptions_withNoDataToCrawl_shouldThrowIOException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        final Integer id = 5081;
        String url = animeCrawler.createEntryURL(id, type);

        doReturn(null).when(animeCrawler).getResultFromJSoup(url, type.toString());

        // When
        Anime anime = animeCrawler.crawl(id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withMissingOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = null;
        final Integer id = 5081;
        String url = animeCrawler.createEntryURL(id, type);

        // When
        Anime anime = animeCrawler.crawl(id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrawlById_withIllegalIntegerOptions_shouldThrowIllegalArgumentException() throws Exception {
        // Given
        TypeEnum type = TypeEnum.ANIME;
        final Integer id = -1;
        String url = animeCrawler.createEntryURL(id, type);

        // When
        Anime anime = animeCrawler.crawl(id);
    }
}