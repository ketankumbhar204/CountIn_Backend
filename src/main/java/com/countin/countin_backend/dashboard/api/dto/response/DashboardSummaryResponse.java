package com.countin.countin_backend.dashboard.api.dto.response;

import com.countin.countin_backend.space.domain.model.SpaceType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {

    private SpaceType spaceType;
    private String month;
    private DashboardFinancialSummaryResponse financial;
    private DashboardMessOperationsResponse messOperations;
    private DashboardAccommodationOperationsResponse accommodationOperations;
    private List<DashboardAttentionItemResponse> attention;
}
