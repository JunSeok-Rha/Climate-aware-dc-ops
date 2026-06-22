# climate-aware-dc-ops

## 프로젝트
데이터센터 운영 관측 플랫폼. 공개 데이터 기반 heuristic risk scoring.
score는 predictive truth가 아닌 heuristic operational indicator.

## 스택
Java 17, Spring Boot 3.3, Gradle, PostgreSQL 15, Docker Compose

## 패키지 구조
com.dcops.{ingestion, normalization, aggregation,
           scoring, classification, api, common, config}

## 핵심 원칙
- CSV: BufferedReader 스트리밍 (Files.readAllLines 절대 금지)
- 집계: SQL GROUP BY (Java stream 계산 금지)
- 배치 insert: 500행 단위
- 모든 API 응답에 disclaimer 필드 포함
- 가중치/threshold: application.yml 외부화

## 병렬 작업 원칙
- 동시 작업은 git worktree로만
- 병렬 에이전트 최대 2개 (Pro 쿼터 기준)

## 커밋 형식
feat: [무엇] — [왜]
에이전트 생성 코드는 커밋 메시지에 "에이전트 생성, 직접 검토 완료" 명시

## 하지 말 것
- Files.readAllLines() 사용 금지
- application.yml 에이전트가 직접 수정 금지 (인간 작성)
- 커밋 메시지 에이전트가 작성 금지
