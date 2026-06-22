package com.countin.countin_backend.meal.api.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSubscriptionHistoryResponse {

    private MemberSubscriptionLifetimeSummaryResponse summary;
    private List<MemberMealBalanceActivityEventResponse> events;
}
