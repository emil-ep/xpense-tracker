package com.xperia.xpense_tracker.services.impl;
import com.xperia.xpense_tracker.cache.CacheService;
import com.xperia.xpense_tracker.services.MutualFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.xperia.models.MFSchemeDetailSearchUIResponseModel;
import org.xperia.service.impl.MutualFundSchemeDetailServiceImpl;
import static com.xperia.xpense_tracker.cache.CacheNames.MUTUAL_FUND_CACHE_NAME;

@Service
public class MutualFundServiceImpl implements MutualFundService {

    @Autowired
    private MutualFundSchemeDetailServiceImpl service;

    @Autowired
    private CacheService cacheService;


    @Override
    @Cacheable(value = MUTUAL_FUND_CACHE_NAME,
            key = "#pageNo.toString() + ':' + #size.toString() + ':' + #search.toString()")
    public Page<MFSchemeDetailSearchUIResponseModel> searchMutualFunds(int pageNo, int size, String search) {
        String cacheKey = pageNo + ":" + size + ":" + search;
        cacheService.storeByCacheName(MUTUAL_FUND_CACHE_NAME, cacheKey);
        return service.fetchDetails(pageNo, size, search);
    }
}
