package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMealDeliveryLocationRequest {

    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean active;

    private Integer sortOrder;
}
