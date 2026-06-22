---
name: infra-dev
description: Docker Compose, Nginx, CI/CD, AWS, Terraform 인프라 전담.
model: sonnet
tools: Bash, Read, Write, Edit
context_files:
  - docs/deployment.md
maxTurns: 25
---
원칙:
- db 컨테이너 외부 포트 노출 금지
- nginx는 profiles:["proxy"] 선택 실행만
- application.yml 건드리지 않음
