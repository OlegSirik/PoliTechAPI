package ru.pt.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// INSERT_YOUR_CODE
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.pt.domain.account.Account;
import ru.pt.service.AccountService;
import ru.pt.service.PolicyService;
import ru.pt.service.FileService;

@RestController
@RequestMapping("/test")
public class Test {

    private final AccountService accountService;
    private final PolicyService policyService;
    private final FileService fileService;

    public Test(AccountService accountService, PolicyService policyService, FileService fileService) {
        this.accountService = accountService;
        this.policyService = policyService;
        this.fileService = fileService;
    }

    @PostMapping("/create-client")
    public ResponseEntity<Account> createClient(@RequestBody Account account) {
        // Replace Object with the actual DTO class for createClient if available
        Account result = accountService.createClient(account.getName());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/create-group")
    public ResponseEntity<Account> createGroup(@RequestBody Account account) {
        Account result = accountService.createGroup(account.getName(), account.getParentId());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/create-account")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account result = accountService.createAccount(account.getName(), account.getParentId());
        return ResponseEntity.ok(result);
    }
    
    
    @PostMapping("/create-subaccount")
    public ResponseEntity<Account> createSubaccount(@RequestBody Account account) {
        Account result = accountService.createSubaccount(account.getName(), account.getParentId());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get-product-roles/{accountId}")
    public ResponseEntity<Object> getProductRoles(@PathVariable("accountId") long accountId) {
        Set<String> result = accountService.getProductRoles(accountId);
        return ResponseEntity.ok(result.toArray());
    }

    @GetMapping("/get-account-login")
    public ResponseEntity<ObjectNode> getAccountLogin(
            @RequestHeader("login") String login,
            @RequestHeader("client") String client,
            @RequestHeader(value = "accountNr", required = false) Long accountNr) {
        
        ObjectNode result = accountService.getAccountLogin(login, client,accountNr);
        return ResponseEntity.ok(result);
    }   


    //create post endpoint for PolicyService.quoteValidator and PolicyService.saveValidator
    //put request body to this methods
    //return response entity with result

    @PostMapping("/quote/validator")
    public ResponseEntity<ObjectNode> quoteValidator(@RequestBody String requestBody) {
        ObjectNode result = policyService.quoteValidator(requestBody);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/policy/validator")
    public ResponseEntity<ObjectNode> saveValidator(@RequestBody String requestBody) {
        ObjectNode result = policyService.saveValidator(requestBody);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/policy/printpf/{pf-type}")
    public byte[] printPolicy(@RequestBody String requestBody, @PathVariable("pf-type") String pfType) {
        ObjectNode result = policyService.saveValidator(requestBody);
        // get context from result
        ArrayNode context = (ArrayNode) result.get("context");
        Map<String, String> keyValues = new HashMap<>();

        for (JsonNode node : context) {
            String key = node.get("varCode").asText();
            String value = node.get("varValue").asText();
            System.out.println(key + " " + value);
            keyValues.put(key, value);
        }

        return fileService.getFile(pfType, keyValues);

        //return null;
    }
}
