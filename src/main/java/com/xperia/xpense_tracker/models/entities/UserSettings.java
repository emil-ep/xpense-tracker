package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.xperia.xpense_tracker.models.JsonNodeReadConverter;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private SettingsType type;

    @OneToOne
    private TrackerUser user;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(Types.OTHER)
    @Convert(converter = JsonNodeReadConverter.class)
    private JsonNode payload;

    public UserSettings(SettingsType type, TrackerUser user, JsonNode payload){
        this.type = type;
        this.user = user;
        this.payload = payload;
    }

}
