package com.xperia.xpense_tracker.models.entities.tracker;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "mail_details")
@NoArgsConstructor
@Getter
@Setter
public class MailDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    private TrackerUser user;

    private String historyId;

    private Long lastSynced;

}
