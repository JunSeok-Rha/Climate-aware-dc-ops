# deployment

TODO

## v1 서비스 구성
- app: Spring Boot (8080), db에 의존
- db: PostgreSQL 15, 외부 포트 미노출, dc-ops-net 내부만
- nginx: --profile proxy 선택 실행만

## network 격리 원칙
db는 외부 직접 접근 불가.
v2 AWS Private Subnet 구조와 논리적으로 대응한다.
