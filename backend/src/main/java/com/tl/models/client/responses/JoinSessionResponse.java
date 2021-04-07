package com.tl.models.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.validation.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinSessionResponse {
    @JsonProperty
    public String url;
}
