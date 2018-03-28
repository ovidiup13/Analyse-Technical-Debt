package com.td;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Playground.
 */
public class Test {

    public static void main(String[] args) {

        URI jiraUri = URI.create("https://issues.apache.org/jira/rest/api/latest/issue/");
        String expand = "?expand=changelog";
        String issueKey = "ZOOKEEPER-2933";

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(jiraUri + issueKey + expand);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);

            String body = convertStreamToString(response.getEntity().getContent());
            JSONObject json = new JSONObject(body);

            JSONObject changelog = json.getJSONObject("changelog");
            JSONArray histories = changelog.getJSONArray("histories");
            for (int i = 0; i < histories.length(); i++) {
                JSONObject history = histories.getJSONObject(i);
                String s = history.getString("created");
                LocalDateTime created = LocalDateTime.parse(s, df);
                JSONArray items = history.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    String field = item.getString("field");
                    String fromString = item.getString("fromString");
                    String toString = item.getString("toString");
                    System.out.println(toString);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}