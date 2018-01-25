package models;

import java.util.List;

public class RepositoryModel {

    private String id;
    private String name;
    private String author;
    private String URI;
    private List<CommitModel> commits;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the uRI
     */
    public String getURI() {
        return URI;
    }

    /**
     * @param uRI the uRI to set
     */
    public void setURI(String uRI) {
        this.URI = uRI;
    }

    /**
     * @return the commits
     */
    public List<CommitModel> getCommits() {
        return commits;
    }

    /**
     * @param commits the commits to set
     */
    public void setCommits(List<CommitModel> commits) {
        this.commits = commits;
    }

}