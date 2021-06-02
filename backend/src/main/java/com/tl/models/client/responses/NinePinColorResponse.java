package com.tl.models.client.responses;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum NinePinColorResponse {
    @JsonProperty("red")
    Red,
    @JsonProperty("blue")
    Blue,
    @JsonProperty("green")
    Green,
    @JsonProperty("yellow")
    Yellow;
}
