#!/bin/bash
# StudyFlow Backend - Test Runner
set -e

GREEN='\033[0;32m'; RED='\033[0;31m'; BLUE='\033[0;34m'; NC='\033[0m'

echo -e "${BLUE}"
echo "================================"
echo " StudyFlow Backend - Test Suite"
echo "================================"
echo -e "${NC}"

echo "[1/4] Cleaning..."
./mvnw clean -q

echo "[2/4] Compiling..."
./mvnw compile -q

echo "[3/4] Running tests..."
./mvnw test -q

echo "[4/4] Generating coverage report..."
./mvnw jacoco:report -q || true

echo -e "${GREEN}"
echo "================================"
echo " All tests passed!"
echo " Coverage: target/site/jacoco/index.html"
echo " Reports:  target/surefire-reports/"
echo "================================"
echo -e "${NC}"
