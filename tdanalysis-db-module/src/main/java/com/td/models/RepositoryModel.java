package com.td.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;

@Document(collection = "repos")
public class RepositoryModel {

    @Id
    private String id;
    private String name;
    private String author;

    private String URI;
    private String buildCommand;

    @Transient
    private File projectFolder;

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

    public File getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(File projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getBuildCommand() {
        return buildCommand;
    }

    public void setBuildCommand(String buildCommand) {
        this.buildCommand = buildCommand;
    }
}