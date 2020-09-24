package com.atixlabs.semillasmiddleware.app.model.provider.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Optional;

@Getter
@Builder
public class ProviderUpdateRequest {

    private Optional<Long> categoryId;
    private Optional<String> name;
    private Optional<String> phone;
    private Optional<String> whatsappNumber;
    private Optional<String> email;
    private Optional<String> description;
    private Optional<Boolean> active;
    @Min(0)
    @Max(100)
    private Optional<Integer> benefit;
    private Optional<String> speciality;
}
