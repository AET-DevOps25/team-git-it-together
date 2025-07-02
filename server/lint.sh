#!/bin/bash

# Linting script for SkillForge Server
# This script runs code quality checks across all modules

set -e

echo "🔍 Running code quality checks across all modules..."

# Run SpotBugs and Checkstyle for all modules
./gradlew codeQualityCheck

echo "✅ Code quality checks completed!"
echo ""
echo "📊 Reports generated:"
echo "  - SpotBugs reports: build/reports/spotbugs/"
echo "  - Checkstyle reports: build/reports/checkstyle/"
echo ""
echo "💡 To view HTML reports, open the files in your browser:"
echo "  - Course service: skillforge-course/build/reports/spotbugs/main/spotbugs.html"
echo "  - User service: skillforge-user/build/reports/spotbugs/main/spotbugs.html"
echo "  - Gateway service: skillforge-gateway/build/reports/spotbugs/main/spotbugs.html" 