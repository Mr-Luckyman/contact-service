package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.ListCompaniesUseCase;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CreateCompanyUseCase createCompanyUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final ListCompaniesUseCase listCompaniesUseCase;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        Company company = createCompanyUseCase.create(request.getName(), request.toCommands());
        return ResponseEntity
                .created(URI.create("/api/v1/companies/" + company.getId()))
                .body(CompanyResponse.fromDomain(company));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable UUID id) {
        Company company = getCompanyUseCase.getById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return ResponseEntity.ok(CompanyResponse.fromDomain(company));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> listCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<CompanyResponse> companies = listCompaniesUseCase.list(page, size).stream()
                .map(CompanyResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(companies);
    }
}
