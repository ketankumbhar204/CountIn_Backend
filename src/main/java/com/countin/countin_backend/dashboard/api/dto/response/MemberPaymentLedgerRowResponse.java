package com.countin.countin_backend.dashboard.api.dto.response;

import com.countin.countin_backend.dashboard.domain.model.MemberPaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberPaymentLedgerRowResponse {

    private UUID memberId;
    private String memberName;
    private BigDecimal expectedCharges;
    private BigDecimal collected;
    private BigDecimal pending;
    private String currencyCode;
    private MemberPaymentStatus status;
}
