package com.hotelconnect.backend.noticies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticiaResponse {
    private String status;
    private int totalResults;
    private List<Noticia> articles;
}


