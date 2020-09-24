package com.atixlabs.semillasmiddleware.app.model.provider.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class ProviderDto implements Serializable {
    private Long id;
    private Integer benefit;
    private String email;
    private String name;
    private String whatsappNumber;
    private String phone;
    private String speciality;
    private String description;
    private Boolean active;
    private ProviderCategoryDto providerCategoryDto;
}
