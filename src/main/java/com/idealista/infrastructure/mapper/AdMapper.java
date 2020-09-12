package com.idealista.infrastructure.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.AdVO;
import com.idealista.infrastructure.persistence.InMemoryPersistence;

@Service
public class AdMapper {
	
	@Autowired
	private InMemoryPersistence inMemoryPersistance;

	public PublicAd convertAdVOToPublicAd(AdVO ad) {
		PublicAd publicAd = new PublicAd();
		
		publicAd.setGardenSize(ad.getGardenSize());
		publicAd.setDescription(ad.getDescription());
		publicAd.setHouseSize(ad.getHouseSize());
		publicAd.setId(ad.getId());
		publicAd.setPictureUrls(obtainPictureUrl(ad));
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
		qualityAd.setPictureUrls(obtainPictureUrl(ad));
		qualityAd.setScore(ad.getScore());
		qualityAd.setTypology(ad.getTypology());
		
		return qualityAd;
	}

	private List<String> obtainPictureUrl(AdVO ad) {
		List<String> pictureUrls = new ArrayList<String>();
		
		ad.getPictures().forEach(pictureId -> {
			inMemoryPersistance.fintPictureById(pictureId)
				.ifPresent((picture) -> {
					pictureUrls.add(picture.getUrl());
				});
		});
		
		return pictureUrls;
	}
}
