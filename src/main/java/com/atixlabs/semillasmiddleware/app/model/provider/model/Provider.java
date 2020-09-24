package com.atixlabs.semillasmiddleware.app.model.provider.model;

import com.atixlabs.semillasmiddleware.app.model.provider.dto.ProviderCategoryDto;
import com.atixlabs.semillasmiddleware.app.model.provider.dto.ProviderDto;
import com.atixlabs.semillasmiddleware.security.model.AuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table
public class Provider extends AuditableEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "id_provider_category")
    @ManyToOne
    private ProviderCategory providerCategory;

    private String name;
    private String phone;
    private String whatsappNumber;
    private String email;

    @Column(length = 1024)
    private String description;

    @Min(0)
    @Max(100)
    private Integer benefit;
    private String speciality;

    private boolean active = true;

    public Provider(ProviderCategory providerCategory, String name, String phone, String email, Integer benefit, String speciality){
        this.providerCategory = providerCategory;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.benefit = benefit;
        this.speciality = speciality;
    }

    public Provider(ProviderCategory providerCategory, String name, String whatsappNumber, String phone, String email, Integer benefit, String speciality, boolean active){
        this.providerCategory = providerCategory;
        this.name = name;
        this.whatsappNumber = whatsappNumber;
        this.phone = phone;
        this.email = email;
        this.benefit = benefit;
        this.speciality = speciality;
        this.active = active;
    }

    public ProviderDto toDto(){
        return ProviderDto.builder()
                .name(this.name)
                .benefit(this.benefit)
                .email(this.email)
                .id(this.id)
                .phone(this.phone)
                .speciality(this.speciality)
                .whatsappNumber(this.whatsappNumber)
                .active(this.active)
                .providerCategoryDto(ProviderCategoryDto.builder()
                        .id(this.providerCategory.getId())
                        .name(this.providerCategory.getName())
                        .build())
                .description(this.description)
                .build();
    }
}
