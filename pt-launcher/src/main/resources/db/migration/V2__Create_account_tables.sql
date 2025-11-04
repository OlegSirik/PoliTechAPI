-- Create account management tables
-- Migration V2: Account system implementation

-- Create sequence for accounts
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 1;

-- Main accounts table
CREATE TABLE IF NOT EXISTS acc_accounts (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    parent_id BIGINT,
    node_type VARCHAR(10) NOT NULL,
    name VARCHAR(250),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES acc_accounts(id)
);

-- Product roles table
CREATE TABLE IF NOT EXISTS acc_product_roles (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    account_id BIGINT NOT NULL,
    role_product_id BIGINT NOT NULL,
    role_account_id BIGINT NOT NULL,
    can_read BOOLEAN DEFAULT FALSE,
    can_quote BOOLEAN DEFAULT FALSE,
    can_policy BOOLEAN DEFAULT FALSE,
    can_addendum BOOLEAN DEFAULT FALSE,
    can_cancel BOOLEAN DEFAULT FALSE,
    can_prolongate BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES acc_accounts(id)
);

-- Unique constraint for product roles
CREATE UNIQUE INDEX IF NOT EXISTS acc_product_roles_uk 
ON acc_product_roles(account_id, role_product_id, role_account_id);


-- Account logins table
CREATE TABLE IF NOT EXISTS acc_logins (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    login VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Account logins table
CREATE TABLE IF NOT EXISTS acc_account_logins (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    login VARCHAR(255) NOT NULL,
    client VARCHAR(255) NOT NULL,
    nr BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES acc_accounts(id)
);

-- Unique constraint for account logins
--CREATE UNIQUE INDEX IF NOT EXISTS account_logins_uk 
--ON account_logins(login, client, nr);


-- Account tokens table
CREATE TABLE IF NOT EXISTS acc_account_tokens (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    token VARCHAR(255) NOT NULL,
    client VARCHAR(255) NOT NULL,
    account_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES acc_accounts(id)
);

-- Unique constraint for account tokens
--CREATE UNIQUE INDEX IF NOT EXISTS account_tokens_uk 
--ON account_tokens(token, client);

