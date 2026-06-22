CREATE TABLE IF NOT EXISTS raw_cluster_node_data (
    id BIGSERIAL PRIMARY KEY,
    node_id VARCHAR(50) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    cpu_usage NUMERIC(5,2) NOT NULL,
    memory_usage NUMERIC(5,2) NOT NULL,
    workload_intensity NUMERIC(5,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_raw_cluster_node_data_node_id_observed_at
    ON raw_cluster_node_data(node_id, observed_at);
