package com.xperia.xpense_tracker.models.entities.tracker;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "oauth2_tokens")
@NoArgsConstructor
@Getter
@Setter
public class Oauth2Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String accessToken;

    private String refreshToken;

    @OneToOne
    private TrackerUser user;

    public Oauth2Token(String accessToken, String refreshToken, TrackerUser user){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
