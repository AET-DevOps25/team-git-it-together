#!/bin/bash

# Usage:. ./copy-env.sh [input_file] [output_file]

INPUT=${1:-.env.dev.template}
OUTPUT=${2:-.env}

# Remove comments and empty lines
# - Remove lines starting with #
# - Remove everything after # on a line (but not inside quoted values)
awk '
{
  # Skip lines that are only comments or empty
  if ($0 ~ /^[[:space:]]*#/ || $0 ~ /^[[:space:]]*$/) next

  # Remove inline comments (unless inside double quotes)
  line = $0
  in_quotes = 0
  out = ""
  for (i = 1; i <= length(line); i++) {
    c = substr(line, i, 1)
    if (c == "\"") in_quotes = !in_quotes
    if (!in_quotes && c == "#") break
    out = out c
  }
  print out
}
' "$INPUT" | sed 's/[[:space:]]*$//' > "$OUTPUT"

echo "✅ Cleaned env file written to $OUTPUT"
