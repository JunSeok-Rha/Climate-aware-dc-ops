-- 원시 기상 관측 데이터 수집 테이블
CREATE TABLE IF NOT EXISTS raw_weather_data (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    source VARCHAR(50) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    temperature NUMERIC(5,2),
    humidity NUMERIC(5,2),
    wind_speed NUMERIC(5,2),
    precipitation NUMERIC(5,2),
    raw_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_raw_weather_data_zone_id_observed_at ON raw_weather_data(zone_id, observed_at);

-- 원시 클러스터 운영 지표 수집 테이블
CREATE TABLE IF NOT EXISTS raw_cluster_data (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    source VARCHAR(50) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    cpu_utilization NUMERIC(5,2),
    power_usage_kw NUMERIC(8,2),
    cooling_load_kw NUMERIC(8,2),
    raw_payload TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_raw_cluster_data_zone_id_observed_at ON raw_cluster_data(zone_id, observed_at);

-- 정규화된 기상 데이터 저장 테이블
CREATE TABLE IF NOT EXISTS normalized_weather_data (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    temperature NUMERIC(5,2),
    humidity NUMERIC(5,2),
    wind_speed NUMERIC(5,2),
    precipitation NUMERIC(5,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_normalized_weather_data_zone_id_observed_at ON normalized_weather_data(zone_id, observed_at);

-- 정규화된 클러스터 운영 지표 저장 테이블
CREATE TABLE IF NOT EXISTS normalized_cluster_data (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    cpu_utilization NUMERIC(5,2),
    power_usage_kw NUMERIC(8,2),
    cooling_load_kw NUMERIC(8,2),
    pue NUMERIC(5,3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_normalized_cluster_data_zone_id_observed_at ON normalized_cluster_data(zone_id, observed_at);

-- 존별 시간 집계 지표 테이블
CREATE TABLE IF NOT EXISTS zone_aggregated_metrics (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    aggregated_at TIMESTAMPTZ NOT NULL,
    avg_temperature NUMERIC(5,2),
    avg_humidity NUMERIC(5,2),
    avg_cpu_utilization NUMERIC(5,2),
    avg_power_usage_kw NUMERIC(8,2),
    avg_pue NUMERIC(5,3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(zone_id, aggregated_at)
);

CREATE INDEX IF NOT EXISTS idx_zone_aggregated_metrics_zone_id_aggregated_at ON zone_aggregated_metrics(zone_id, aggregated_at);

-- 파생 운영 상태 및 heuristic score 테이블
CREATE TABLE IF NOT EXISTS derived_operational_status (
    id BIGSERIAL PRIMARY KEY,
    zone_id VARCHAR(10) NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL,
    status_level VARCHAR(20) NOT NULL,
    risk_score NUMERIC(6,2),
    weather_score NUMERIC(6,2),
    cluster_score NUMERIC(6,2),
    disclaimer TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_derived_operational_status_zone_id_evaluated_at ON derived_operational_status(zone_id, evaluated_at);
