#!/bin/bash
if echo "$CLAUDE_TOOL_INPUT" | grep -q "application.yml"; then
  echo "application.yml은 직접 작성. 에이전트가 건드리지 말 것." >&2
  exit 2
fi
exit 0
