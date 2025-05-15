package com.hotelconnect.backend.noticies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/noticies")
@CrossOrigin(origins = "*")
public class NoticiesController {

    @Autowired
    private NoticiesService noticiesService;

    @GetMapping
    public NoticiaResponse getNoticies(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "12") int pageSize) {
        return noticiesService.obtenirNoticiesResponse(page, pageSize);
    }

}

