-- Снимаем foreign key constraint
ALTER TABLE policy_index DROP CONSTRAINT policy_index_id_fkey;

-- Создаем новые таблицы с UUID
CREATE TABLE policy_data_new (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy JSONB NOT NULL
);

CREATE TABLE policy_index_new (
    id UUID PRIMARY KEY,
    draft_id VARCHAR(30),
    policy_nr VARCHAR(30),
    version_no INTEGER,
    top_version BOOLEAN DEFAULT FALSE,
    product_code VARCHAR(30),
    create_date TIMESTAMP WITH TIME ZONE,
    issue_date TIMESTAMP WITH TIME ZONE,
    issue_timezone VARCHAR(50),
    payment_date TIMESTAMP WITH TIME ZONE,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    user_account_id BIGINT,
    client_account_id BIGINT,
    version_status VARCHAR(30),
    FOREIGN KEY (id) REFERENCES policy_data_new(id) ON DELETE CASCADE
);

-- Копируем данные из policy_data с преобразованием ID
INSERT INTO policy_data_new (id, policy)
SELECT
    gen_random_uuid(),
    policy
FROM policy_data;

-- Копируем данные из policy_index с сохранением связей
INSERT INTO policy_index_new (
    id, draft_id, policy_nr, version_no, top_version, product_code,
    create_date, issue_date, issue_timezone, payment_date, start_date,
    end_date, user_account_id, client_account_id, version_status
)
SELECT
    pd_new.id,
    pi.draft_id, pi.policy_nr, pi.version_no, pi.top_version, pi.product_code,
    pi.create_date, pi.issue_date, pi.issue_timezone, pi.payment_date, pi.start_date,
    pi.end_date, pi.user_account_id, pi.client_account_id, pi.version_status
FROM policy_index pi
JOIN policy_data pd_old ON pi.id = pd_old.id
JOIN policy_data_new pd_new ON pd_old.policy = pd_new.policy;

-- Удаляем старые таблицы
DROP TABLE policy_index;
DROP TABLE policy_data;

-- Переименовываем новые таблицы
ALTER TABLE policy_data_new RENAME TO policy_data;
ALTER TABLE policy_index_new RENAME TO policy_index;