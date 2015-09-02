package com.github.v4lproik.myanimelist.api.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ArtWork extends Item{

    protected Integer id;

    protected String type;

    protected String title;

    protected String[] synonyms;

    protected String englishTitle;

    protected String japaneseTitle;

    protected String synopsis;

    protected String background;

    protected String startedAiringDate;

    protected String finishedAiringDate;

    protected String rank;

    protected String popularity;

    protected String score;

    protected String[] genres;

    protected String ageRating;

    protected String posterImage;

    protected ArtWork parent;

    protected List<Author> authors = new ArrayList<Author>();

    protected String[] tags;

    protected List<ArtworkId> sequels = new ArrayList<ArtworkId>();

    protected List<ArtworkId> alternativeVersions = new ArrayList<ArtworkId>();

    protected List<ArtworkId> prequels = new ArrayList<ArtworkId>();

    protected List<ArtworkId> spinoff = new ArrayList<ArtworkId>();

    protected List<ArtworkId> sideStories = new ArrayList<ArtworkId>();

    protected List<ArtworkId> others = new ArrayList<ArtworkId>();

    protected List<ArtworkId> summaries = new ArrayList<ArtworkId>();

    protected List<ArtworkId> adaptations = new ArrayList<ArtworkId>();

    protected List<ArtworkId> parentStories = new ArrayList<ArtworkId>();

    protected List<Character> characters = new ArrayList<Character>();

    public ArtWork(Integer id) {

        if (id <= 0){
            throw new NumberFormatException("An entry id cannot be <= 0");
        }

        this.id = id;
    }

    public ArtWork() {
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

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<ArtworkId> getSequels() {
        return sequels;
    }

    public void setSequels(List<ArtworkId> sequels) {
        this.sequels = sequels;
    }

    public List<ArtworkId> getAlternativeVersions() {
        return alternativeVersions;
    }

    public void setAlternativeVersions(List<ArtworkId> alternativeVersions) {
        this.alternativeVersions = alternativeVersions;
    }

    public List<ArtworkId> getPrequels() {
        return prequels;
    }

    public void setPrequels(List<ArtworkId> prequels) {
        this.prequels = prequels;
    }

    public List<ArtworkId> getSpinoff() {
        return spinoff;
    }

    public void setSpinoff(List<ArtworkId> spinoff) {
        this.spinoff = spinoff;
    }

    public List<ArtworkId> getSideStories() {
        return sideStories;
    }

    public void setSideStories(List<ArtworkId> sideStories) {
        this.sideStories = sideStories;
    }

    public List<ArtworkId> getOthers() {
        return others;
    }

    public void setOthers(List<ArtworkId> others) {
        this.others = others;
    }

    public List<ArtworkId> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<ArtworkId> summaries) {
        this.summaries = summaries;
    }

    public List<ArtworkId> getAdaptations() {
        return adaptations;
    }

    public void setAdaptations(List<ArtworkId> adaptations) {
        this.adaptations = adaptations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArtWork getParent() {
        return parent;
    }

    public void setParent(ArtWork parent) {
        this.parent = parent;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<ArtworkId> getParentStories() {
        return parentStories;
    }

    public void setParentStories(List<ArtworkId> parentStories) {
        this.parentStories = parentStories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtWork that = (ArtWork) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, rank);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("type", type)
                .append("title", title)
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
