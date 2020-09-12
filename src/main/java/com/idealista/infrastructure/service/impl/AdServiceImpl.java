package com.idealista.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.mapper.AdMapper;
import com.idealista.infrastructure.persistence.AdVO;
import com.idealista.infrastructure.persistence.InMemoryPersistence;
import com.idealista.infrastructure.persistence.PictureVO;
import com.idealista.infrastructure.service.IAdService;

@Service
public class AdServiceImpl implements IAdService {
	private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
	
	@Autowired
	private InMemoryPersistence inMemoryPersistance;
	@Autowired
	private AdMapper adMapper;

	@Override
	public List<PublicAd> getPublicListing(Boolean orderBy) {
		logger.info("getPublicListing");
		
		List<AdVO> calculateAds = calculateScore();
		List<PublicAd> publicListing = new ArrayList<PublicAd>();
		
		// sort from best to worst score
		if (orderBy != null) {
			calculateAds.sort(Comparator.comparing(AdVO::getScore));
		}
		
		calculateAds.stream()
			.filter(ad -> ad.getScore() > 40)
			.forEach(ad -> {
				publicListing.add(adMapper.convertAdVOToPublicAd(ad));
			});
		
		return publicListing;
	}
	
	@Override
	public List<QualityAd> getQualityListing(Boolean irrelevantAds) {
		logger.info("getQualityListing");
		
		List<AdVO> calculateAds = calculateScore();
		List<QualityAd> qualityListing = new ArrayList<QualityAd>();
		
		calculateAds.stream()
			.filter(ad -> irrelevantAds != null && irrelevantAds ? true : ad.getScore() > 40 )
			.forEach(ad -> {
				qualityListing.add(adMapper.convertAdVOToQualityAd(ad));
			});
		
		return qualityListing;
		
//		return retrieveAds(true).stream().sorted((o1, o2) -> o1.getScore().compareTo(o2.getScore())).collect(Collectors.toList());
	}

	@Override
	public List<AdVO> calculateScore() {
		logger.info("calculateScore");
		
		List<AdVO> ads = inMemoryPersistance.findAds();
		
		return ads.stream().map(ad -> {
			calculateAdScore(ad);
		    return ad;
	    }).collect(Collectors.toList());
	}

	private void calculateAdScore(AdVO ad) {
		Integer score = 0;
		
		score += obtainPicturesScore(ad);
		score += obtainDescriptionScore(ad);
		score += obtainDescriptionSizeScore(ad);
		score += obtainSpecialWordsScore(ad);
		score += obtainFullAdScore(ad);
		
		if (score < 0)
			score = 0;
		else if (score > 100)
			score = 100;
		
		ad.setScore(score);
		
		if (score < 40) {
	    	ad.setIrrelevantSince(new Date());
	    }
	}
	
	private Integer obtainDescriptionScore(AdVO ad) {
		if (ad.getDescription() == null) {
			return 0;
		} else {
			return ad.getDescription().length() > 0 ? 5 : 0;
		}
	}
	
	private Integer obtainDescriptionSizeScore(AdVO ad) {
		if (ad.getTypology() == null || ad.getDescription() == null 
				|| !inMemoryPersistance.findTypologyTypesAllowed().contains(ad.getTypology())) {
			return 0;
		}
		
		if (ad.getTypology().equals(inMemoryPersistance.FLAT)) {
			if (ad.getDescription().length() >= 20 && ad.getDescription().length() < 50) {
				return 10;
	        } else if (ad.getDescription().length() >= 50) {
	        	return 30;
	        }
		}
		
		if (ad.getTypology().equals(inMemoryPersistance.CHALET)) {
			if (ad.getDescription().length() > 50) {
				return 20;
			}
		}
		
		return 0;
	}
	
	private List<String> obtainSpecialWords() {
		List<String> specialWords = new ArrayList<String>();
        specialWords.add("Luminoso");
        specialWords.add("Ático");
        specialWords.add("Reformado");
        specialWords.add("Céntrico");
        specialWords.add("Nuevo");
        
        return specialWords;
	}
	
	private Integer obtainSpecialWordsScore(AdVO ad) {
		if (ad.getDescription() == null) {
			return 0;
		}
		
		List<String> specialWords = obtainSpecialWords();
		
		specialWords.removeIf(specialWord -> !ad.getDescription().toLowerCase().contains(specialWord.toLowerCase()));
		
		return specialWords.size() * 5;
	}
	
	private Integer obtainPicturesScore(AdVO ad) {
		if (ad.getPictures() == null || ad.getPictures().isEmpty()) {
	      return -10;
	    }

	    return ad.getPictures().stream()
	        .map(this::getPictureScore)
	        .mapToInt(Integer::intValue)
	        .sum();
	}
	
	private Integer getPictureScore(Integer picId) {
		Optional<PictureVO> pictureOpt = inMemoryPersistance.fintPictureById(picId);
		
		return pictureOpt.map(picture -> picture.getQuality().equals("HD") ? 20 : 10).orElse(0);
    }
	
	private Integer obtainFullAdScore(AdVO ad) {
		// check if it has description when it is not a GARAGE
		if (ad.getDescription() == null && !inMemoryPersistance.GARAGE.equals(ad.getTypology())) {
			return 0;
		}
		
		// check if it has pictures
		if (ad.getPictures() == null || ad.getPictures().isEmpty()) {
			return 0;
		}
		
		if (inMemoryPersistance.GARAGE.equals(ad.getTypology())) {
			return 40;
		}
		
		if (inMemoryPersistance.CHALET.equals(ad.getTypology()) && ad.getHouseSize() != null  && ad.getGardenSize() != null) {
			return 40;
		}
		
		if (inMemoryPersistance.FLAT.equals(ad.getTypology()) && ad.getHouseSize() != null) {
			return 40;
		}
		
		return 0;
	}
}
