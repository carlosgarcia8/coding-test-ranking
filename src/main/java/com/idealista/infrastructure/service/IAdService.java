package com.idealista.infrastructure.service;

import java.util.List;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.AdVO;

public interface IAdService {

	public List<QualityAd> getQualityListing(Boolean irrelevantAds);

	public List<AdVO> calculateScore();

	public List<PublicAd> getPublicListing(Boolean orderBy);
	
}
