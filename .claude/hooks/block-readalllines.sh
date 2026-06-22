#!/bin/bash
if echo "$CLAUDE_TOOL_INPUT" | grep -q "Files.readAllLines"; then
  echo "Files.readAllLines() 금지. BufferedReader 스트리밍 사용." >&2
  exit 2
fi
exit 0
