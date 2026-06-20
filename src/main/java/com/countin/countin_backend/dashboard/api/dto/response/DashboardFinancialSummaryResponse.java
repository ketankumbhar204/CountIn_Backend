package com.countin.countin_backend.dashboard.api.dto.response;

import com.countin.countin_backend.dashboard.domain.model.DashboardFinancialSource;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardFinancialSummaryResponse {

    private BigDecimal expectedCharges;
    private BigDecimal collected;
    private BigDecimal pending;
    private String currencyCode;
    private DashboardFinancialSource source;
}
