package com.tl.models.client.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class JoinSessionResponse {
    private UUID sessionId;
    private UUID userId;
}
