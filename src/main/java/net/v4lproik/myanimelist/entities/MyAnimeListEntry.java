package net.v4lproik.myanimelist.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class MyAnimeListEntry {

    private Integer id;

    private String type;

    private String title;

    private String background;

    private String[] synonyms;

    private String englishTitle;

    private String japaneseTitle;

    private String synopsis;

    private String startedAiringDate;

    private String finishedAiringDate;

    private String rank;

    private String popularity;

    private String score;

    private String[] genres;

    private String ageRating;

    private String posterImage;

    private MyAnimeListEntry parent;

    private List<MyAnimeListAuthor> authors = new ArrayList<MyAnimeListAuthor>();

    private String[] tags;

    private List<MyAnimeListEntry> sequels = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> alternativeVersions = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> prequels = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> spinoff = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> sideStories = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> others = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> summaries = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListEntry> adaptations = new ArrayList<MyAnimeListEntry>();

//    private List<MyAnimeListEntry> parentStories = new ArrayList<MyAnimeListEntry>();

    private List<MyAnimeListCharacter> characters = new ArrayList<MyAnimeListCharacter>();

    public MyAnimeListEntry(Integer id) {

        if (id <= 0){
            throw new NumberFormatException("An entry id cannot be <= 0");
        }

        this.id = id;
    }

    public MyAnimeListEntry() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    public String getJapaneseTitle() {
        return japaneseTitle;
    }

    public void setJapaneseTitle(String japaneseTitle) {
        this.japaneseTitle = japaneseTitle;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getStartedAiringDate() {
        return startedAiringDate;
    }

    public void setStartedAiringDate(String startedAiringDate) {
        this.startedAiringDate = startedAiringDate;
    }

    public String getFinishedAiringDate() {
        return finishedAiringDate;
    }

    public void setFinishedAiringDate(String finishedAiringDate) {
        this.finishedAiringDate = finishedAiringDate;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public List<MyAnimeListCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<MyAnimeListCharacter> characters) {
        this.characters = characters;
    }

    public List<MyAnimeListAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<MyAnimeListAuthor> authors) {
        this.authors = authors;
    }

    public List<MyAnimeListEntry> getSequels() {
        return sequels;
    }

    public void setSequels(List<MyAnimeListEntry> sequels) {
        this.sequels = sequels;
    }

    public List<MyAnimeListEntry> getAlternativeVersions() {
        return alternativeVersions;
    }

    public void setAlternativeVersions(List<MyAnimeListEntry> alternativeVersions) {
        this.alternativeVersions = alternativeVersions;
    }

    public List<MyAnimeListEntry> getPrequels() {
        return prequels;
    }

    public void setPrequels(List<MyAnimeListEntry> prequels) {
        this.prequels = prequels;
    }

    public List<MyAnimeListEntry> getSpinoff() {
        return spinoff;
    }

    public void setSpinoff(List<MyAnimeListEntry> spinoff) {
        this.spinoff = spinoff;
    }

    public List<MyAnimeListEntry> getSideStories() {
        return sideStories;
    }

    public void setSideStories(List<MyAnimeListEntry> sideStories) {
        this.sideStories = sideStories;
    }

    public List<MyAnimeListEntry> getOthers() {
        return others;
    }

    public void setOthers(List<MyAnimeListEntry> others) {
        this.others = others;
    }

    public List<MyAnimeListEntry> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<MyAnimeListEntry> summaries) {
        this.summaries = summaries;
    }

    public List<MyAnimeListEntry> getAdaptations() {
        return adaptations;
    }

    public void setAdaptations(List<MyAnimeListEntry> adaptations) {
        this.adaptations = adaptations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MyAnimeListEntry getParent() {
        return parent;
    }

    public void setParent(MyAnimeListEntry parent) {
        this.parent = parent;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MyAnimeListEntry that = (MyAnimeListEntry) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("type", type)
                .append("title", title)
                .append("background", background)
                .append("synonyms", synonyms)
                .append("englishTitle", englishTitle)
                .append("japaneseTitle", japaneseTitle)
                .append("synopsis", synopsis)
                .append("startedAiringDate", startedAiringDate)
                .append("finishedAiringDate", finishedAiringDate)
                .append("rank", rank)
                .append("popularity", popularity)
                .append("score", score)
                .append("genres", genres)
                .append("ageRating", ageRating)
                .append("posterImage", posterImage)
                .append("parent", parent)
                .append("authors", authors)
                .append("tags", tags)
                .append("sequels", sequels)
                .append("alternativeVersions", alternativeVersions)
                .append("prequels", prequels)
                .append("spinoff", spinoff)
                .append("sideStories", sideStories)
                .append("others", others)
                .append("summaries", summaries)
                .append("adaptations", adaptations)
                .append("characters", characters)
                .toString();
    }

    public abstract boolean isAnime();

    public abstract boolean isManga();
}
