package com.hotelconnect.backend.plats_tipics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
@CrossOrigin(origins = "*")
public class PlatTipicController {

    @Autowired
    private PlatTipicRepository platRepo;

    @GetMapping
    public List<PlatTipic> getAll() {
        return platRepo.findAll();
    }

    @PostMapping
    public PlatTipic create(@RequestBody PlatTipic plat) {
        return platRepo.save(plat);
    }
}

