package ru.pt.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.pt.domain.account.Account;
import ru.pt.domain.account.AccountLogin;
import ru.pt.domain.account.AccountNodeType;
import ru.pt.domain.account.ProductRole;
import ru.pt.exception.BadRequestException;
import ru.pt.exception.NotFoundException;
import ru.pt.repository.AccountRepository;
import ru.pt.repository.AccountLoginRepository;
import ru.pt.repository.ProductRoleRepository;


/**
 * Service for Account entity CRUD operations and policy CRUD
 */
@Service
public class AccountService {
    
    private AccountRepository accountRepository;
    private ProductRoleRepository productRoleRepository;
    private AccountLoginRepository accountLoginRepository;

    public AccountService(AccountRepository accountRepository, ProductRoleRepository productRoleRepository, AccountLoginRepository accountLoginRepository) {
        this.accountRepository = accountRepository;
        this.productRoleRepository = productRoleRepository;
        this.accountLoginRepository = accountLoginRepository;
    }

    // add method - return account by ID    
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
    }

    public Account createClient(String name) {
        Account account = new Account();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.CLIENT);
        account.setParentId(0L);

        return accountRepository.save(account);
    }

    public Account createGroup(String name, Long parentId) {
        Account parentAccount = getAccountById(parentId);
        if (parentAccount.getNodeType() != AccountNodeType.CLIENT && parentAccount.getNodeType() != AccountNodeType.GROUP) {
            throw new BadRequestException("Parent ID must be a client or group account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        
        Account account = new Account();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.GROUP);
        account.setParentId(parentId);

        Account savedAccount = accountRepository.save(account);


        return savedAccount;
    }

    public Account createAccount(String name, Long parentId) {
        Account parentAccount = getAccountById(parentId);
        if (parentAccount.getNodeType() != AccountNodeType.GROUP) {
            throw new BadRequestException("Parent ID must be a group account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        Account account = new Account();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.ACCOUNT);
        account.setParentId(parentId);
        return accountRepository.save(account);
    }

    public Account createSubaccount(String name, Long parentId) {
        Account parentAccount = getAccountById(parentId);
        if (parentAccount.getNodeType() != AccountNodeType.ACCOUNT) {
            throw new BadRequestException("Parent ID must be an account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        Account account = new Account();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.SUBACCOUNT);
        account.setParentId(parentId);
        return accountRepository.save(account);
    }

    public Account grantProduct(Long accountId, ProductRole productRole) {
        Account account = getAccountById(accountId);
        
        return account;
    }

    public List<ProductRole> getProductRolesByAccountId(Long accountId) {
        List<ProductRole> productRoles = new ArrayList<>();
        List<Map<String, Object>> roles = productRoleRepository.findAllProductRolesByAccountId(accountId);
        HashSet<String> allRoles = new HashSet();
        for (Map<String, Object> role : roles) {
            String productName = role.get("roleProductCode").toString();
            if (!allRoles.contains(productName)) {
                allRoles.add(productName);
                ProductRole productRole = new ProductRole();
                productRole.setId((Long) role.get("id"));
                productRole.setAccountId(accountId);
                productRole.setRoleProductId( (Long) role.get("roleProductId"));
                productRole.setRoleAccountId( (Long) role.get("roleAccountId"));

                productRole.setCanRead( Boolean.TRUE.equals(role.get("canRead")));
                productRole.setCanQuote( Boolean.TRUE.equals(role.get("canQuote")));
                productRole.setCanPolicy( Boolean.TRUE.equals(role.get("canPolicy")));
                productRole.setCanAddendum( Boolean.TRUE.equals(role.get("canAddendum")));
                productRole.setCanCancel( Boolean.TRUE.equals(role.get("canCancel")));
                productRole.setCanProlongate( Boolean.TRUE.equals(role.get("canProlongate")));

                productRoles.add(productRole);
            }
        }
        return productRoles;
    }

    public Set<String> getProductRoles(Long accountId) {
        List<Map<String, Object>> roles = productRoleRepository.findAllProductRolesByAccountId(accountId);
        HashSet<String> allRoles = new HashSet();
        for (Map<String, Object> role : roles) {
            String productName = role.get("roleProductCode").toString();
            if (!allRoles.contains(productName)) {
                allRoles.add(productName);
                if (role.get("canRead") != null && (boolean) role.get("canRead")) {
                    allRoles.add(productName + "_READ");
                }
                if (role.get("canQuote") != null && (boolean) role.get("canQuote")) {
                    allRoles.add(productName + "_QUOTE");
                }
                if (role.get("canPolicy") != null && (boolean) role.get("canPolicy")) {
                    allRoles.add(productName + "_POLICY");
                }
            }
        }
        return allRoles;
    }

    public ObjectNode getAccountLogin(String login, String client, Long accountId) {
        List<AccountLogin> accountLogins = accountLoginRepository.findByClientAndLogin(client, login);
        AccountLogin accountLogin = null;
        if ( accountId != null) {
            accountLogin = accountLogins.stream().filter(al -> Objects.equals(al.getAccountId(), accountId)).findFirst().orElseThrow(() -> new NotFoundException("AccountLogin not found"));
        } else {
            accountLogin = accountLogins.stream().filter(al -> al.getNr() == 0).findFirst().orElseThrow(() -> new NotFoundException("AccountLogin not found"));
        }

        // create JSON  with fasterxml.jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("id", accountLogin.getId());
        jsonNode.put("login", accountLogin.getLogin());
        jsonNode.put("client", accountLogin.getClient());
        jsonNode.put("accountId", accountLogin.getAccountId());

        Account account = accountRepository.findById(accountLogin.getAccountId()).orElseThrow(() -> new NotFoundException("Account not found"));
        ObjectNode ob1 = jsonNode.putObject("currentAccount");
        ob1.put("id", account.getId());
        ob1.put("name", account.getName());
        ob1.put("nodeType", account.getNodeType().name());

        ArrayNode arrayNode = jsonNode.putArray("otherAccounts");
        for (AccountLogin al : accountLogins) {
            if (!Objects.equals(al.getAccountId(), accountLogin.getAccountId())) {
                account = accountRepository.findById(al.getAccountId()).orElseThrow(() -> new NotFoundException("Account not found"));
                ob1 = arrayNode.addObject();
                ob1.put("id", account.getId());
                ob1.put("name", account.getName());
                ob1.put("nodeType", account.getNodeType().name());
            }
        }
        
        ArrayNode arrayNode2 = jsonNode.putArray("productRoles");

        List<Map<String, Object>> roles = productRoleRepository.findAllProductRolesByAccountId(accountLogin.getAccountId());
        HashSet<String> allRoles = new HashSet();
        for (Map<String, Object> role : roles) {
            String productName = role.get("roleProductCode").toString();
            if (!allRoles.contains(productName)) {
                ObjectNode ob3 = arrayNode2.addObject();
                ob3.put("name", productName);
                
                ob3.put( "id", role.get("id").toString());
                // ob1.put( "accountId",   accountLogin.getAccountId().toString());
                ob3.put("roleProductId",  role.get("roleProductId").toString());
                ob3.put("roleAccountId",  role.get("roleAccountId").toString());

                ob3.put( "canRead",  Boolean.TRUE.equals(role.get("canRead")));
                ob3.put( "canQuote",  Boolean.TRUE.equals(role.get("canQuote")));
                ob3.put( "canPolicy",  Boolean.TRUE.equals(role.get("canPolicy")));
                ob3.put( "canAddendum",  Boolean.TRUE.equals(role.get("canAddendum")));
                ob3.put( "canCancel",  Boolean.TRUE.equals(role.get("canCancel")));
                ob3.put( "canProlongate",  Boolean.TRUE.equals(role.get("canProlongate")));

                // arrayNode2.add(ob3);
            }
        }


        return jsonNode;
    }
}