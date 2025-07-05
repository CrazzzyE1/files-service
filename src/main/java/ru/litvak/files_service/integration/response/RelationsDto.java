package ru.litvak.files_service.integration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.litvak.files_service.enumerated.PrivacyLevel;

@Getter
@Setter
public class RelationsDto {

    private PrivacyLevel privacyLevel;
    @JsonProperty("isFriends")
    private boolean isFriends;
}