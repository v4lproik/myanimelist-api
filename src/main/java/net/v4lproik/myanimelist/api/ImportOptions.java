package net.v4lproik.myanimelist.api;

/**
 * Created by joel on 13/05/2015.
 */
public class ImportOptions {

    private Boolean dependency;

    private String query;

    private String type;

    private Integer id;

    public ImportOptions(String query, String type) {
        this.query = query;
        this.type = type;
    }

    public ImportOptions(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public ImportOptions(String query, String type, Integer id) {
        this.query = query;
        this.type = type;
        this.id = id;
    }

    public ImportOptions(int id, String type, Boolean dependency) {
        this.dependency = dependency;
        this.type = type;
        this.id = id;
    }

    public ImportOptions(String query, String type, Boolean dependency) {
        this.dependency = dependency;
        this.type = type;
        this.query = query;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDependency() {
        return dependency;
    }

    public void setDependency(Boolean dependency) {
        this.dependency = dependency;
    }
}
