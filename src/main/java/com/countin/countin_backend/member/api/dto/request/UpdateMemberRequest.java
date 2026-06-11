package com.countin.countin_backend.member.api.dto.request;

import com.countin.countin_backend.member.domain.model.MembershipRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Request body for updating a member")
public class UpdateMemberRequest {

    @NotBlank(message = "Full name is required")
    @Schema(description = "Display name of the member", example = "Rahul Sharma")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Schema(description = "Contact mobile number", example = "9876543210")
    private String mobileNumber;

    @NotNull(message = "Role is required")
    @Schema(description = "Member role", example = "TENANT", implementation = MembershipRole.class)
    private MembershipRole role;
}
