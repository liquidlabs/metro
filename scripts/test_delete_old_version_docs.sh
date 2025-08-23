#!/bin/bash

# test_delete_old_version_docs.sh
#
# Test script to simulate what delete_old_version_docs.sh would delete
# without actually running mike delete commands.
#
# Usage: ./test_delete_old_version_docs.sh

# Hardcoded test versions - modify this list to test different scenarios
test_versions=(
    "0.7.3"
    "0.7.2-SNAPSHOT [snapshot]"
    "0.7.1-SNAPSHOT [snapshot]"
    "0.7.0-SNAPSHOT [snapshot]"
    "0.6.2 [latest]"
    "0.6.1"
    "0.6.0"
    "0.6.0-SNAPSHOT"
    "0.5.5"
    "0.5.4"
    "0.5.3"
    "0.5.2"
    "0.5.1"
    "0.5.0"
)

echo "=== Testing with versions ==="
for v in "${test_versions[@]}"; do
    # Clean version string (remove mike annotations like [latest], [snapshot])
    clean_v=$(echo "$v" | sed 's/\s*\[.*\]$//')
    echo "  $clean_v"
done
echo

# Clean versions and simulate the same logic as the original script
versions=()
for v in "${test_versions[@]}"; do
    # Clean version string (remove mike annotations like [latest], [snapshot])
    clean_v=$(echo "$v" | sed 's/\s*\[.*\]$//')
    versions+=("$clean_v")
done

declare -A major_latest

# Find latest per X.Y (same logic as original script)
for v in "${versions[@]}"; do
  major="${v%.*}"
  if [[ -z "${major_latest[$major]}" ]]; then
    major_latest[$major]="$v"
  else
    latest="${major_latest[$major]}"
    # Use version sort (-V) to compare
    greater=$(printf "%s\n%s\n" "$latest" "$v" | sort -V | tail -n1)
    major_latest[$major]="$greater"
  fi
done

echo "=== Latest versions per major.minor series ==="
for major in "${(@k)major_latest}"; do
    echo "  $major -> ${major_latest[$major]}"
done
echo

to_delete=()
to_keep=()

for v in "${versions[@]}"; do
  major="${v%.*}"
  if [[ "$v" != "${major_latest[$major]}" ]]; then
    to_delete+=("$v")
  else
    to_keep+=("$v")
  fi
done

echo "=== WOULD KEEP ==="
for v in "${to_keep[@]}"; do
  echo "  ✓ $v"
done
echo

echo "=== WOULD DELETE ==="
for v in "${to_delete[@]}"; do
  echo "  ✗ $v"
done
echo

echo "Summary: ${#to_keep[@]} versions kept, ${#to_delete[@]} versions would be deleted"
