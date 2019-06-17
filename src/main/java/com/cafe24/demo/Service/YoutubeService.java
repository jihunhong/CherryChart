package com.cafe24.demo.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class YoutubeService {

    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static YouTube youtube;

    @Value("${apikey}")
    private String apikey = "";

    public ArrayList<Map<String, String>> SearchOnYoutube(String searchQuery) {

        List<SearchResult> searchResultList = null;
        SearchListResponse searchResponse = null;

        try {

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("ChartCrawler").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            /*
             * It is important to set your developer key from the Google Developer Console
             * for non-authenticated requests (found under the API Access tab at this link:
             * c). This is good practice and increased your quota.
             */

            String apiKey = apikey;
            search.setKey(apiKey);

            search.setQ(searchQuery);
            // 검색어 설정

            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/high/url)");
            search.setMaxResults((long) 3);
            searchResponse = search.execute();

            searchResultList = searchResponse.getItems();
            System.out.println(searchResponse.toString());

        } catch (Throwable t) {
            t.printStackTrace();
        }
        ResourceId rId;
        Thumbnail thumbnail;
        ArrayList<Map<String, String>> output = new ArrayList<>();

        for (SearchResult result : searchResponse.getItems()) {
            SearchResultSnippet snippet = result.getSnippet();
            rId = result.getId();

            thumbnail = result.getSnippet().getThumbnails().getHigh();

            Map<String, String> element = new HashMap<String, String>();
            element.put("title", snippet.getTitle());
            element.put("url", rId.getVideoId());
            element.put("thumbnail", thumbnail.getUrl());
            output.add(element);
        }
        return output;
    }

    
    public ArrayList<Map<String, String>> GetPlayListMine(){
        
        List<Playlist> searchResultList = null;
        PlaylistListResponse searchResponse = null;

        try {

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("ChartCrawler").build();

            YouTube.Playlists.List list = youtube.playlists().list("snippet,contentDetails");

            /*
             * It is important to set your developer key from the Google Developer Console
             * for non-authenticated requests (found under the API Access tab at this link:
             * c). This is good practice and increased your quota.
             */

            String apiKey = apikey;
            list.setKey(apikey);

            

            list.setMine(true);
            
            searchResponse = list.execute();

            searchResultList = searchResponse.getItems();
            System.out.println(searchResponse.toString());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        ArrayList<Map<String, String>> output = new ArrayList<>();

        for (Playlist result : searchResponse.getItems()) {
            PlaylistSnippet snippet = result.getSnippet();


            Map<String, String> element = new HashMap<String, String>();
            element.put("title", snippet.getTitle());
            element.put("description", snippet.getDescription());

            output.add(element);
        }
        return output;
    }


    
}
