package com.hotelconnect.backend.noticies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class NoticiesService {

    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public NoticiaResponse obtenirNoticiesResponse(int page, int pageSize) {

        String url = "https://newsapi.org/v2/everything?q=mallorca+turismo&language=es&sortBy=publishedAt&pageSize=" + pageSize + "&page=" + page + "&apiKey=" + apiKey;

        System.out.println("URL: " + url);

        NoticiaResponse response = restTemplate.getForObject(url, NoticiaResponse.class);

        assert response != null;
        System.out.println("Status: " + response.getStatus());
        System.out.println("TotalResults: " + response.getTotalResults());
        System.out.println("Articles: " + (response.getArticles() != null ? response.getArticles().size() : "null"));

        return response;
    }
}
