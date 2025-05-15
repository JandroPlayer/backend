package com.hotelconnect.backend.noticies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Noticia {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
}

