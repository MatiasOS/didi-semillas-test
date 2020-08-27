package com.atixlabs.semillasmiddleware.app.model.provider.service;

import com.atixlabs.semillasmiddleware.app.model.provider.dto.ProviderFilterDto;
import com.atixlabs.semillasmiddleware.app.model.provider.dto.ProviderUpdateRequest;
import com.atixlabs.semillasmiddleware.app.model.provider.exception.InexistentCategoryException;
import com.atixlabs.semillasmiddleware.app.model.provider.exception.InexistentProviderException;
import com.atixlabs.semillasmiddleware.app.model.provider.model.ProviderCategory;
import com.atixlabs.semillasmiddleware.app.model.provider.model.Provider;
import com.atixlabs.semillasmiddleware.app.model.provider.dto.ProviderCreateRequest;
import com.atixlabs.semillasmiddleware.app.model.provider.repository.ProviderRepository;
import com.google.common.collect.Lists;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ProviderService{

    @Autowired
    public ProviderService(ProviderRepository providerRepository,
                           ProviderCategoryService providerCategoryService){
        this.providerRepository = providerRepository;
        this.providerCategoryService = providerCategoryService;
    }

    @Value("${app.pageSize}")
    private String size;

    private ProviderRepository providerRepository;
    private ProviderCategoryService providerCategoryService;

    public Provider create(ProviderCreateRequest providerCreateRequest) {
        Provider provider = new Provider();
        ProviderCategory category = providerCategoryService.findById(providerCreateRequest.getCategoryId());

        provider.setProviderCategory(category);
        provider.setEmail(providerCreateRequest.getEmail());
        provider.setName(providerCreateRequest.getName());
        providerCreateRequest.getPhone().ifPresent(provider::setPhone);
        providerCreateRequest.getBenefit().ifPresent(provider::setBenefit);
        providerCreateRequest.getWhatsappNumber().ifPresent(provider::setWhatsappNumber);
        provider.setSpeciality(providerCreateRequest.getSpeciality());
        provider.setDescription(providerCreateRequest.getDescription());

        return providerRepository.save(provider);
    }

    private Specification<Provider> getProviderSpecification (ProviderFilterDto providerFilterDto) {
        return (Specification<Provider>) (root, query, cb) -> {
            Stream<Predicate> predicates = Stream.of(
                    providerFilterDto.getActivesOnly().map(value -> cb.equal(root.get("active"), value)),
                    providerFilterDto.getCategoryId().map(value -> cb.equal(root.get("providerCategory"), value)),
                    providerFilterDto.getCriteriaQuery().map(value -> {
                        String criteria = "%" + value.toUpperCase() + "%";
                        return cb.or(
                                cb.like(cb.upper(root.get("name")), criteria),
                                cb.like(cb.upper(root.get("email")), criteria)
                        );
                    })
            ).flatMap(Optional::stream);
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public Page<Provider> findAll(Integer page, ProviderFilterDto providerFilterDto){
        Pageable pageRequest = PageRequest.of(page, Integer.valueOf(size), Sort.by("name").ascending());
        return providerRepository.findAll(getProviderSpecification(providerFilterDto), pageRequest);
    }

    public List<Provider> findAll(){
        return providerRepository.findAll();
    }

    public void disable(Long providerId){
        Provider provider = providerRepository.findById(providerId).orElseThrow( () -> new InexistentProviderException());
        provider.setActive(false);
        providerRepository.save(provider);
    }

    public Provider findById(Long id){
        return providerRepository.findById(id).orElseThrow(InexistentProviderException::new);
    }

    public Provider update(Long id, ProviderUpdateRequest providerUpdateRequest){
        Provider provider = this.findById(id);
        providerUpdateRequest.getName().ifPresent(provider::setName);
        providerUpdateRequest.getBenefit().ifPresent(provider::setBenefit);
        providerUpdateRequest.getEmail().ifPresent(provider::setEmail);
        providerUpdateRequest.getPhone().ifPresent(provider::setPhone);
        providerUpdateRequest.getWhatsappNumber().ifPresent(provider::setWhatsappNumber);
        providerUpdateRequest.getSpeciality().ifPresent(provider::setSpeciality);
        providerUpdateRequest.getCategoryId().ifPresent( catId -> provider.setProviderCategory(providerCategoryService.findById(catId)));

        return providerRepository.save(provider);
    }
}
