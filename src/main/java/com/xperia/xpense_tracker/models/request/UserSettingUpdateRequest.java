package com.xperia.xpense_tracker.models.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSettingUpdateRequest {

    private String type;

    private Object payload;


}
