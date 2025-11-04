package be.dash.dashserver.api.core.account;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import be.dash.dashserver.core.domain.account.Bank;
import be.dash.dashserver.core.domain.account.service.BankService;
import be.dash.dashserver.core.log.annotation.Trace;
import lombok.RequiredArgsConstructor;

@Trace
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bank")
public class BankController implements BankControllerDocs {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<List<Bank>> findAll() {
        return ResponseEntity.ok(bankService.findAll());
    }
}
