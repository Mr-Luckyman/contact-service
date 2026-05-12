package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.List;
import ru.mentee.power.crm.contact.domain.model.Company;

public interface ListCompaniesUseCase {
  List<Company> list(int page, int size);
}
