package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.TierType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeTierRequest {

    @NotNull(message = "Target tier is required")
    private TierType targetTier;
}
