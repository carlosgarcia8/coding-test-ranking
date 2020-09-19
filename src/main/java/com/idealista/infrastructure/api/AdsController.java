package com.idealista.infrastructure.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idealista.application.service.AdService;

@RestController
public class AdsController {

    @Autowired
    private AdService adService;

    @GetMapping(value = "/ads/qualityListing")
    public ResponseEntity<List<QualityAd>> qualityListing(@RequestParam(required = false) Boolean irrelevantAds) {
        List<QualityAd> qualityAds = adService.getQualityListing(irrelevantAds);
        return ResponseEntity.ok(qualityAds);
    }

    @GetMapping(value = "/ads/publicListing")
    public ResponseEntity<List<PublicAd>> publicListing(@RequestParam(required = false) Boolean orderBy) {
        List<PublicAd> publicAds = adService.getPublicListing(orderBy);
        return ResponseEntity.ok(publicAds);
    }

    @GetMapping(value = "/ads/calculateScore")
    public ResponseEntity<Void> calculateScore() {
        adService.calculateScore();
        return ResponseEntity.noContent().build();
    }
}
