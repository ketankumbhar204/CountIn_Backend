package com.countin.countin_backend.space.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Space {

    private UUID id;
    private String name;
    private SpaceType type;
    private String address;
    private String contactNumber;
    private UUID ownerId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
