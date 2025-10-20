package com.xperia.xpense_tracker.models.entities.tracker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "statements")
@NoArgsConstructor
@Getter
@Setter
public class Statements {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fileName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private TrackerUser user;

    private long timestamp;

    public Statements(String fileName, TrackerUser user, long timestamp){
        this.fileName = fileName;
        this.user = user;
        this.timestamp = timestamp;
    }

}
