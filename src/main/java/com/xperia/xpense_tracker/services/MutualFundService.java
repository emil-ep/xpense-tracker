package com.xperia.xpense_tracker.services;

import org.springframework.data.domain.Page;
import org.xperia.models.MFSchemeDetailSearchUIResponseModel;

public interface MutualFundService {

    Page<MFSchemeDetailSearchUIResponseModel> searchMutualFunds(int pageNo, int size, String search);
}
