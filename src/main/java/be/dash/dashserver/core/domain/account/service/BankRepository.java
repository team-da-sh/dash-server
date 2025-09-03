package be.dash.dashserver.core.domain.account.service;

import java.util.List;
import be.dash.dashserver.core.domain.account.Bank;

public interface BankRepository {

    Bank findById(long bankId);

    List<Bank> findAll();
}
