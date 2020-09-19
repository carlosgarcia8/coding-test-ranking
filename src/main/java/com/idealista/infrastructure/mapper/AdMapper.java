package com.idealista.infrastructure.mapper;

import org.springframework.stereotype.Service;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.AdVO;

@Service
public class AdMapper {

    public PublicAd convertAdVOToPublicAd(AdVO ad) {
        PublicAd publicAd = new PublicAd();

        publicAd.setGardenSize(ad.getGardenSize());
        publicAd.setDescription(ad.getDescription());
        publicAd.setHouseSize(ad.getHouseSize());
        publicAd.setId(ad.getId());
        publicAd.setTypology(ad.getTypology());

        return publicAd;
    }

    public QualityAd convertAdVOToQualityAd(AdVO ad) {
        QualityAd qualityAd = new QualityAd();

        qualityAd.setGardenSize(ad.getGardenSize());
        qualityAd.setDescription(ad.getDescription());
        qualityAd.setHouseSize(ad.getHouseSize());
        qualityAd.setId(ad.getId());
        qualityAd.setIrrelevantSince(ad.getIrrelevantSince());
        qualityAd.setScore(ad.getScore());
        qualityAd.setTypology(ad.getTypology());

        return qualityAd;
    }
}
