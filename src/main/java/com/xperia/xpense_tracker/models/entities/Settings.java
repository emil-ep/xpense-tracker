package com.xperia.xpense_tracker.models.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.models.converter.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "settings")
@Getter
@Setter
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private SettingsType type;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode payload;

}
