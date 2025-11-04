-- Create policy management tables
-- Migration V3: Policy storage and indexing implementation

-- Create sequence for policies
CREATE SEQUENCE IF NOT EXISTS policy_seq START WITH 1 INCREMENT BY 1;

-- Policy data table (stores the full policy JSON)
CREATE TABLE IF NOT EXISTS policy_data (
    id BIGINT PRIMARY KEY DEFAULT nextval('policy_seq'),
    policy JSONB NOT NULL
);

-- Policy index table (for fast searching and filtering)
CREATE TABLE IF NOT EXISTS policy_index (
    id BIGINT PRIMARY KEY,
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
    FOREIGN KEY (id) REFERENCES policy_data(id) ON DELETE CASCADE
);

-- Create indexes for common search patterns
CREATE INDEX IF NOT EXISTS policy_index_draft_id_idx ON policy_index(draft_id, top_version);
CREATE INDEX IF NOT EXISTS policy_index_policy_nr_idx ON policy_index(policy_nr, top_version);
CREATE INDEX IF NOT EXISTS policy_index_user_account_idx ON policy_index(user_account_id);



