package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tag")
public class TagController {

    @Autowired
    private TagService tagService;


    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @GetMapping
    public ResponseEntity<AbstractResponse> getTags(@AuthenticationPrincipal UserDetails userDetails){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            List<Tag> tags = tagService.findAllTagsForUser(user);
            return ResponseEntity.ok(new SuccessResponse(tags));
        }catch (Exception ex){
            LOG.error("Failed to fetch tags : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error fetching tag data"));
        }

    }
}
