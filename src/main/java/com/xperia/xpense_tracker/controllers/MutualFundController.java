package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xperia.models.MFSchemeDetailSearchUIResponseModel;
import org.xperia.service.impl.MutualFundSchemeDetailServiceImpl;

@RestController
@RequestMapping("/v1/mf")
public class MutualFundController {

    @Autowired
    private MutualFundSchemeDetailServiceImpl service;

    private static final Logger LOGGER = LoggerFactory.getLogger(MutualFundController.class);

    @GetMapping("/search")
    public ResponseEntity<AbstractResponse> fetchSchemesByPagination(@AuthenticationPrincipal UserDetails userDetails,
                                                                     @RequestParam("page") int pageNo,
                                                                     @RequestParam("size") int size,
                                                                     @RequestParam(value = "searchKey", defaultValue = "", required = false) String search){
        try{
            Page<MFSchemeDetailSearchUIResponseModel> page = service.fetchDetails(pageNo, size, search);
            return ResponseEntity.ok(new SuccessResponse(page));
        }catch (Exception ex){
            LOGGER.error("Error fetching paged response : {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new ErrorResponse("Unable to fetch paged response"));
        }
    }
}
