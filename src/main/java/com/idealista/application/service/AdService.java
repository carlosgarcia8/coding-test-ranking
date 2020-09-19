package com.idealista.application.service;

import java.util.List;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.AdVO;

public interface AdService {

	public List<QualityAd> getQualityListing(Boolean irrelevantAds);

	public List<AdVO> calculateScore();

	public List<PublicAd> getPublicListing(Boolean orderBy);
	
}
