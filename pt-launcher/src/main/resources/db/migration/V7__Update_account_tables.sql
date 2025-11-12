-- migration_001_drop_old_tables_and_create_sequences.sql

-- Удаляем существующие таблицы в правильном порядке (с учетом foreign keys)
DROP TABLE IF EXISTS acc_account_tokens CASCADE;
DROP TABLE IF EXISTS acc_account_logins CASCADE;
DROP TABLE IF EXISTS acc_product_roles CASCADE;
DROP TABLE IF EXISTS acc_logins CASCADE;
DROP TABLE IF EXISTS acc_accounts CASCADE;
DROP TABLE IF EXISTS acc_clients CASCADE;
DROP TABLE IF EXISTS acc_tenants CASCADE;

-- Удаляем существующие sequences если есть
DROP SEQUENCE IF EXISTS account_seq;
DROP SEQUENCE IF EXISTS acc_tenants_seq;
DROP SEQUENCE IF EXISTS acc_clients_seq;
DROP SEQUENCE IF EXISTS acc_accounts_seq;
DROP SEQUENCE IF EXISTS acc_product_roles_seq;
DROP SEQUENCE IF EXISTS acc_logins_seq;
DROP SEQUENCE IF EXISTS acc_account_logins_seq;
DROP SEQUENCE IF EXISTS acc_account_tokens_seq;

-- Создаем основную sequence для всех таблиц
CREATE SEQUENCE account_seq START WITH 1 INCREMENT BY 1;

-- Создаем отдельные sequences для каждой таблицы (альтернативный вариант)
CREATE SEQUENCE acc_tenants_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_clients_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_accounts_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_product_roles_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_logins_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_account_logins_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE acc_account_tokens_seq START WITH 1 INCREMENT BY 1;

-- Создаем таблицы заново
CREATE TABLE acc_tenants (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_tenants_seq'),
    name VARCHAR(250),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE acc_clients (
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_clients_seq'),
    client_id VARCHAR(255) NOT NULL,
    default_account_id BIGINT,
    name VARCHAR(250),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Main accounts table
CREATE TABLE acc_accounts (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_accounts_seq'),
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    client_id BIGINT REFERENCES acc_clients(id),
    parent_id BIGINT REFERENCES acc_accounts(id),
    node_type VARCHAR(10) NOT NULL,
    name VARCHAR(250),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product roles table
CREATE TABLE acc_product_roles (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_product_roles_seq'),
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    client_id BIGINT REFERENCES acc_clients(id),
    account_id BIGINT NOT NULL REFERENCES acc_accounts(id),
    role_product_id BIGINT NOT NULL,
    role_account_id BIGINT NOT NULL REFERENCES acc_accounts(id),
    is_deleted BOOLEAN DEFAULT FALSE,
    can_read BOOLEAN DEFAULT FALSE,
    can_quote BOOLEAN DEFAULT FALSE,
    can_policy BOOLEAN DEFAULT FALSE,
    can_addendum BOOLEAN DEFAULT FALSE,
    can_cancel BOOLEAN DEFAULT FALSE,
    can_prolongate BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Unique constraint for product roles
CREATE UNIQUE INDEX acc_product_roles_uk
ON acc_product_roles(account_id, role_product_id, role_account_id);

-- Account logins table
CREATE TABLE acc_logins (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_logins_seq'),
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    user_login VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Unique constraint for logins
CREATE UNIQUE INDEX acc_logins_uk ON acc_logins(user_login, tid);

-- Account logins table
CREATE TABLE acc_account_logins (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_account_logins_seq'),
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    client_id BIGINT NOT NULL REFERENCES acc_clients(id),
    account_id BIGINT NOT NULL REFERENCES acc_accounts(id),
    user_login VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    user_role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Foreign key for account_logins
ALTER TABLE acc_account_logins
ADD CONSTRAINT fk_acc_account_logins_login
FOREIGN KEY (tid, user_login)
REFERENCES acc_logins(tid, user_login);

-- Unique constraint for account_logins
CREATE UNIQUE INDEX acc_account_logins_uk ON acc_account_logins(user_login, account_id);

-- Account tokens table
CREATE TABLE acc_account_tokens (
    id BIGINT PRIMARY KEY DEFAULT nextval('acc_account_tokens_seq'),
    tid BIGINT NOT NULL REFERENCES acc_tenants(id),
    client_id BIGINT NOT NULL REFERENCES acc_clients(id),
    account_id BIGINT NOT NULL REFERENCES acc_accounts(id),
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Unique constraint for tokens
CREATE UNIQUE INDEX acc_account_tokens_uk ON acc_account_tokens(token, client_id);

-- Создаем индексы для улучшения производительности
CREATE INDEX idx_acc_clients_tid ON acc_clients(tid);
CREATE INDEX idx_acc_clients_client_id ON acc_clients(client_id);
CREATE INDEX idx_acc_accounts_tid ON acc_accounts(tid);
CREATE INDEX idx_acc_accounts_client_id ON acc_accounts(client_id);
CREATE INDEX idx_acc_accounts_parent_id ON acc_accounts(parent_id);
CREATE INDEX idx_acc_product_roles_tid ON acc_product_roles(tid);
CREATE INDEX idx_acc_product_roles_account_id ON acc_product_roles(account_id);
CREATE INDEX idx_acc_product_roles_role_account_id ON acc_product_roles(role_account_id);
CREATE INDEX idx_acc_logins_tid ON acc_logins(tid);
CREATE INDEX idx_acc_logins_user_login ON acc_logins(user_login);
CREATE INDEX idx_acc_account_logins_tid ON acc_account_logins(tid);
CREATE INDEX idx_acc_account_logins_client_id ON acc_account_logins(client_id);
CREATE INDEX idx_acc_account_logins_account_id ON acc_account_logins(account_id);
CREATE INDEX idx_acc_account_logins_user_login ON acc_account_logins(user_login);
CREATE INDEX idx_acc_account_tokens_tid ON acc_account_tokens(tid);
CREATE INDEX idx_acc_account_tokens_client_id ON acc_account_tokens(client_id);
CREATE INDEX idx_acc_account_tokens_account_id ON acc_account_tokens(account_id);
CREATE INDEX idx_acc_account_tokens_token ON acc_account_tokens(token);