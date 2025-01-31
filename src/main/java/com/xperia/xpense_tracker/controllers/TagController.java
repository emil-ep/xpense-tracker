package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TagRequest;
import com.xperia.xpense_tracker.models.request.TagsEditRequest;
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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<AbstractResponse> addNewTag(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody TagRequest tagRequest){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            Tag tag = tagService.addNewTag(tagRequest, user);
            return ResponseEntity.ok(new SuccessResponse(tag));
        }catch (TrackerException ex){
            LOG.error("Error while creating tag : {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            LOG.error("Internal server error while creating tag : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error : Error creating tag"));
        }
    }

    @PutMapping
    public ResponseEntity<AbstractResponse> editTag(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody TagRequest tagRequest){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            Tag savedTag = tagService.editTag(tagRequest, user);
            return ResponseEntity.ok(new SuccessResponse(savedTag));
        }catch (TrackerException ex){
            LOG.error("Error while editing tag : {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            LOG.error("Internal server error while editing tag : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error : Error editing tag"));
        }
    }

    @PatchMapping("/multiple")
    public ResponseEntity<AbstractResponse> editTags(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody TagsEditRequest tagRequests){

        try{
            TrackerUser user = (TrackerUser) userDetails;
            List<Tag> savedTags = tagService.editTags(tagRequests, user);
            return ResponseEntity.ok(new SuccessResponse(savedTags));
        }catch (TrackerException ex){
            LOG.error("Error while editing tags : {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            LOG.error("Internal server error while editing tags : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error : Error editing tags"));
        }
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<AbstractResponse> deleteTags(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable("tagId") String tagId){
        try{
            TrackerUser user = (TrackerUser) userDetails;
            tagService.deleteTag(tagId, user);
            return ResponseEntity.ok(new SuccessResponse("Tag removed successfully"));
        }catch (TrackerException ex){
            LOG.error("Error while deleting tags : {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            LOG.error("Internal server error while deleting tags : {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error : Error deleting tags"));
        }
    }
}
