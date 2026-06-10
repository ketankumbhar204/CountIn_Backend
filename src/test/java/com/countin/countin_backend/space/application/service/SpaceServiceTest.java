package com.countin.countin_backend.space.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.countin.countin_backend.member.infrastructure.persistence.repository.SpaceMembershipRepository;
import com.countin.countin_backend.space.api.dto.request.CreateSpaceRequest;
import com.countin.countin_backend.space.api.dto.response.SpaceResponse;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import com.countin.countin_backend.user.infrastructure.persistence.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpaceMembershipRepository spaceMembershipRepository;

    @InjectMocks
    private SpaceService spaceService;

    private UUID ownerId;
    private UserEntity owner;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        owner = UserEntity.builder()
                .mobileNumber("9876543210")
                .fullName("Ketan")
                .build();
        owner.setId(ownerId);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createSpace_withRentalType_persistsRentalSpace() {
        CreateSpaceRequest request = mock(CreateSpaceRequest.class);
        when(request.getName()).thenReturn("Sunrise Apartments");
        when(request.getType()).thenReturn(SpaceType.RENTAL);
        when(request.getOwnerId()).thenReturn(ownerId);

        when(userRepository.findByIdAndIsActiveTrue(ownerId)).thenReturn(Optional.of(owner));
        when(spaceRepository.save(any(SpaceEntity.class))).thenAnswer(invocation -> {
            SpaceEntity space = invocation.getArgument(0);
            space.setId(UUID.randomUUID());
            space.setCreatedAt(LocalDateTime.now());
            space.setUpdatedAt(LocalDateTime.now());
            return space;
        });

        SpaceResponse response = spaceService.createSpace(request);

        ArgumentCaptor<SpaceEntity> spaceCaptor = ArgumentCaptor.forClass(SpaceEntity.class);
        verify(spaceRepository).save(spaceCaptor.capture());

        assertThat(spaceCaptor.getValue().getType()).isEqualTo(SpaceType.RENTAL);
        assertThat(spaceCaptor.getValue().getName()).isEqualTo("Sunrise Apartments");
        assertThat(response.getType()).isEqualTo(SpaceType.RENTAL);
        assertThat(response.getName()).isEqualTo("Sunrise Apartments");
    }
}
