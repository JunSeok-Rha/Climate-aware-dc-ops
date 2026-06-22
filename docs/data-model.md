# data-model

TODO

## 주요 설계 결정
- observed_at: TIMESTAMPTZ (UTC 통일)
- zone_id: VARCHAR(10), Z01~Z10
- score 컬럼: NUMERIC(6,2)
- UNIQUE(zone_id, aggregated_at): 중복 집계 방지
- raw 계층은 절대 수정하지 않음 (원본 보존)
