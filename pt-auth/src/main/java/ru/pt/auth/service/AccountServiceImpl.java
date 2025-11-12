package ru.pt.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.Account;
import ru.pt.api.dto.auth.ProductRole;
import ru.pt.api.dto.exception.BadRequestException;
import ru.pt.api.dto.exception.NotFoundException;
import ru.pt.api.service.auth.AccountService;
import ru.pt.auth.entity.AccountEntity;
import ru.pt.auth.entity.AccountLoginEntity;
import ru.pt.auth.entity.AccountNodeType;
import ru.pt.auth.repository.AccountLoginRepository;
import ru.pt.auth.repository.AccountRepository;
import ru.pt.auth.repository.ProductRoleRepository;
import ru.pt.auth.utils.*;

import java.util.*;

@Component
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ProductRoleRepository productRoleRepository;
    private final AccountLoginRepository accountLoginRepository;
    private final AccountMapper accountMapper;
    private final AccountLoginMapper accountLoginMapper;
    private final ClientMapper clientMapper;
    private final LoginMapper loginMapper;
    private final ProductRoleMapper productRoleMapper;
    private final TenantMapper tenantMapper;

    public AccountServiceImpl(AccountRepository accountRepository, ProductRoleRepository productRoleRepository, AccountLoginRepository accountLoginRepository, AccountMapper accountMapper, AccountLoginMapper accountLoginMapper, ClientMapper clientMapper, LoginMapper loginMapper, ProductRoleMapper productRoleMapper, TenantMapper tenantMapper) {
        this.accountRepository = accountRepository;
        this.productRoleRepository = productRoleRepository;
        this.accountLoginRepository = accountLoginRepository;
        this.accountMapper = accountMapper;
        this.accountLoginMapper = accountLoginMapper;
        this.clientMapper = clientMapper;
        this.loginMapper = loginMapper;
        this.productRoleMapper = productRoleMapper;
        this.tenantMapper = tenantMapper;
    }


    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Override
    public Account createClient(String name) {
        // TODO слабая логика
        AccountEntity account = new AccountEntity();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.CLIENT);

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    public Account createGroup(String name, Long parentId) {
        AccountEntity parentAccount = accountRepository.findById(parentId).orElseThrow();
        if (parentAccount.getNodeType() != AccountNodeType.CLIENT && parentAccount.getNodeType() != AccountNodeType.GROUP) {
            throw new BadRequestException("Parent ID must be a client or group account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }

        AccountEntity account = new AccountEntity();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.GROUP);
        account.setParent(parentAccount);

        AccountEntity savedAccount = accountRepository.save(account);


        return accountMapper.toDto(savedAccount);
    }

    @Override
    public Account createAccount(String name, Long parentId) {
        AccountEntity parentAccount = accountRepository.findById(parentId).orElseThrow();
        if (parentAccount.getNodeType() != AccountNodeType.GROUP) {
            throw new BadRequestException("Parent ID must be a group account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        AccountEntity account = new AccountEntity();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.ACCOUNT);
        account.setParent(parentAccount);
        return accountMapper.toDto(
                accountRepository.save(account)
        );
    }

    @Override
    public Account createSubaccount(String name, Long parentId) {
        AccountEntity parentAccount = accountRepository.findById(parentId).orElseThrow();
        if (parentAccount.getNodeType() != AccountNodeType.ACCOUNT) {
            throw new BadRequestException("Parent ID must be an account");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        AccountEntity account = new AccountEntity();
        account.setId(accountRepository.getNextAccountId());
        account.setName(name);
        account.setNodeType(AccountNodeType.SUBACCOUNT);
        account.setParent(parentAccount);
        return accountMapper.toDto(
                accountRepository.save(account)
        );
    }

    @Override
    public Account grantProduct(Long accountId, ProductRole productRole) {
        var account = accountRepository.findById(accountId).orElseThrow();
        account.getProductRoles().add(
                productRoleMapper.toEntity(productRole)
        );
        var saved = accountRepository.save(account);
        return accountMapper.toDto(saved);
    }

    @Override
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
                productRole.setRoleProductId((Long) role.get("roleProductId"));
                productRole.setRoleAccountId((Long) role.get("roleAccountId"));

                productRole.setCanRead(Boolean.TRUE.equals(role.get("canRead")));
                productRole.setCanQuote(Boolean.TRUE.equals(role.get("canQuote")));
                productRole.setCanPolicy(Boolean.TRUE.equals(role.get("canPolicy")));
                productRole.setCanAddendum(Boolean.TRUE.equals(role.get("canAddendum")));
                productRole.setCanCancel(Boolean.TRUE.equals(role.get("canCancel")));
                productRole.setCanProlongate(Boolean.TRUE.equals(role.get("canProlongate")));

                productRoles.add(productRole);
            }
        }
        return productRoles;
    }

    @Override
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

    @Override
    public ObjectNode getAccountLogin(String login, String client, Long accountId) {
        List<AccountLoginEntity> accountLogins = accountLoginRepository.findByClientAndLogin(client, login);
        AccountLoginEntity accountLogin = null;
        if (accountId != null) {
            accountLogin = accountLogins.stream()
                    .filter(al -> Objects.equals(al.getAccount().getId(), accountId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("AccountLogin not found"));
        } else {
            accountLogin = accountLogins.stream()
                    .filter(AccountLoginEntity::getDefault)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("AccountLogin not found"));
        }

        // create JSON  with fasterxml.jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("id", accountLogin.getId());
        jsonNode.put("login", accountLogin.getLogin().getUserLogin());
        jsonNode.put("client", accountLogin.getClient().getClientId());
        jsonNode.put("accountId", accountLogin.getAccount().getId());

        AccountEntity account = accountRepository.findById(accountLogin.getId())
                .orElseThrow(() -> new NotFoundException("Account not found"));
        ObjectNode ob1 = jsonNode.putObject("currentAccount");
        ob1.put("id", account.getId());
        ob1.put("name", account.getName());
        ob1.put("nodeType", account.getNodeType().name());

        ArrayNode arrayNode = jsonNode.putArray("otherAccounts");
        for (AccountLoginEntity al : accountLogins) {
            if (!Objects.equals(al.getAccount().getId(), accountLogin.getAccount().getId())) {
                account = accountRepository.findById(al.getAccount().getId())
                        .orElseThrow(() -> new NotFoundException("Account not found"));
                ob1 = arrayNode.addObject();
                ob1.put("id", account.getId());
                ob1.put("name", account.getName());
                ob1.put("nodeType", account.getNodeType().name());
            }
        }

        ArrayNode arrayNode2 = jsonNode.putArray("productRoles");

        List<Map<String, Object>> roles = productRoleRepository.findAllProductRolesByAccountId(accountLogin.getAccount().getId());
        HashSet<String> allRoles = new HashSet();
        for (Map<String, Object> role : roles) {
            String productName = role.get("roleProductCode").toString();
            if (!allRoles.contains(productName)) {
                ObjectNode ob3 = arrayNode2.addObject();
                ob3.put("name", productName);

                ob3.put("id", role.get("id").toString());
                // ob1.put( "accountId",   accountLogin.getAccountId().toString());
                ob3.put("roleProductId", role.get("roleProductId").toString());
                ob3.put("roleAccountId", role.get("roleAccountId").toString());

                ob3.put("canRead", Boolean.TRUE.equals(role.get("canRead")));
                ob3.put("canQuote", Boolean.TRUE.equals(role.get("canQuote")));
                ob3.put("canPolicy", Boolean.TRUE.equals(role.get("canPolicy")));
                ob3.put("canAddendum", Boolean.TRUE.equals(role.get("canAddendum")));
                ob3.put("canCancel", Boolean.TRUE.equals(role.get("canCancel")));
                ob3.put("canProlongate", Boolean.TRUE.equals(role.get("canProlongate")));

                // arrayNode2.add(ob3);
            }
        }


        return jsonNode;
    }
}
