package ru.pt.api.service.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.pt.api.dto.auth.Account;
import ru.pt.api.dto.auth.ProductRole;

import java.util.List;
import java.util.Set;

public interface AccountService {
    // add method - return account by ID
    Account getAccountById(Long id);

    Account createClient(String name);

    Account createGroup(String name, Long parentId);

    Account createAccount(String name, Long parentId);

    Account createSubaccount(String name, Long parentId);

    Account grantProduct(Long accountId, ProductRole productRole);

    List<ProductRole> getProductRolesByAccountId(Long accountId);

    Set<String> getProductRoles(Long accountId);

    ObjectNode getAccountLogin(String login, String client, Long accountId);
}