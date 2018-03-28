package com.td;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.td.models.RepositoryModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RepositoryReader {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryReader.class);
    private static final String DEFAULT_REPOSITORIES_FILE = "repositories.csv";
    private static final String SEPARATOR = ",";

    /**
     * Reads repository list from classpath resource.
     */
    public List<RepositoryModel> readRepositories() {
        List<RepositoryModel> results = new ArrayList<>();

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_REPOSITORIES_FILE);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            // ignore first line
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                results.add(parseRepository(line));
            }
        } catch (IOException e) {
            logger.error("An error occurred when reading repositories from file", e);
        } catch (NullPointerException e) {
            logger.error("An error occurred when retrieving classpath resource", e);
        }

        return results;
    }

    /**
     * Parses a string object and returns an appropriate RepositoryModel object.
     * The string format should be:
     * id,author,name,uri,issueTracker,buildCommand
     */
    RepositoryModel parseRepository(String line) {
        String[] items = line.split(SEPARATOR);

        RepositoryModel model = new RepositoryModel();
        model.setId(items[0]);
        model.setAuthor(items[1]);
        model.setName(items[2]);
        model.setURI(items[3]);
        model.setIssueTrackerURI(items[4]);
        model.setBuildCommand(items[5]);

        return model;
    }
}