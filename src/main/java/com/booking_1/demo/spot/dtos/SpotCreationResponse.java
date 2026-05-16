package com.booking_1.demo.spot.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after creating a spot, optionally includes a new token if the user was promoted to OWNER")
public record SpotCreationResponse(
    @Schema(description = "The created spot details")
    SpotDto spot,
    
    @Schema(description = "New access token with updated roles (only if user was promoted to OWNER)")
    String updatedToken
) {
}
