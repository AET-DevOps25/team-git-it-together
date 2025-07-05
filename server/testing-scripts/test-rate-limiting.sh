#!/bin/bash

# Rate Limiting Test Script
# This script tests the rate limiting implementation in the API Gateway

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
GATEWAY_URL="http://localhost:8081"
TEST_ENDPOINT="/api/v1/courses/public"
REQUESTS_PER_MINUTE=80
REQUESTS_PER_SECOND=20
BURST_LIMIT=20

echo -e "${BLUE}🚀 Rate Limiting Test Script${NC}"
echo -e "${BLUE}========================${NC}"
echo ""

# Check if gateway is running
echo -e "${YELLOW}📡 Checking gateway availability...${NC}"
if ! curl -s -f "$GATEWAY_URL/actuator/health" > /dev/null; then
    echo -e "${RED}❌ Gateway is not running at $GATEWAY_URL${NC}"
    echo "Please start the gateway first:"
    echo "  cd skillforge-gateway && ./gradlew bootRun"
    exit 1
fi
echo -e "${GREEN}✅ Gateway is running${NC}"
echo ""

# Test 1: Basic rate limiting
echo -e "${YELLOW}🧪 Test 1: Basic Rate Limiting${NC}"
echo "Testing $REQUESTS_PER_MINUTE requests per minute limit..."
echo ""

rate_limit_hit=false
for i in {1..25}; do
    response=$(curl -s -w "%{http_code}" "$GATEWAY_URL$TEST_ENDPOINT" -o /dev/null)
    
    if [ "$response" = "429" ]; then
        echo -e "${RED}❌ Rate limit hit at request $i (HTTP $response)${NC}"
        rate_limit_hit=true
        break
    elif [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ Request $i: HTTP $response${NC}"
    else
        echo -e "${YELLOW}⚠️  Request $i: HTTP $response${NC}"
    fi
    
    # Small delay to see the progression
    sleep 0.1
done

if [ "$rate_limit_hit" = false ]; then
    echo -e "${YELLOW}⚠️  Rate limit not hit in first 25 requests${NC}"
fi
echo ""

# Test 3: Burst testing
echo -e "${YELLOW}🧪 Test 3: Burst Rate Limiting${NC}"
echo "Testing burst limit of $BURST_LIMIT requests..."
echo ""

burst_hit=false
for i in {1..25}; do
    response=$(curl -s -w "%{http_code}" \
        -H "X-Client-ID: burst-test" \
        "$GATEWAY_URL$TEST_ENDPOINT" -o /dev/null)
    
    if [ "$response" = "429" ]; then
        echo -e "${RED}❌ Burst limit hit at request $i (HTTP $response)${NC}"
        burst_hit=true
        break
    elif [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ Burst request $i: HTTP $response${NC}"
    else
        echo -e "${YELLOW}⚠️  Burst request $i: HTTP $response${NC}"
    fi
done

if [ "$burst_hit" = false ]; then
    echo -e "${YELLOW}⚠️  Burst limit not hit in 25 requests${NC}"
fi
echo ""

# Test 4: Rate limit response headers
echo -e "${YELLOW}🧪 Test 4: Rate Limit Response Headers${NC}"
echo "Testing rate limit response headers..."
echo ""

response=$(curl -s -I "$GATEWAY_URL$TEST_ENDPOINT" | grep -E "(X-RateLimit|Retry-After)" || true)

if [ -n "$response" ]; then
    echo -e "${GREEN}✅ Rate limit headers found:${NC}"
    echo "$response"
else
    echo -e "${YELLOW}⚠️  No rate limit headers found${NC}"
fi
echo ""

# Summary
echo -e "${BLUE}📊 Test Summary${NC}"
echo -e "${BLUE}==============${NC}"
echo ""

if [ "$rate_limit_hit" = true ] || [ "$burst_hit" = true ]; then
    echo -e "${GREEN}✅ Rate limiting is working correctly${NC}"
else
    echo -e "${YELLOW}⚠️  Rate limiting may not be working as expected${NC}"
    echo "   - Check Redis connection"
    echo "   - Verify rate limiting configuration"
    echo "   - Check gateway logs"
fi

echo ""
echo -e "${BLUE}🔧 Debugging Commands${NC}"
echo "Check Redis rate limiting keys:"
echo "  redis-cli keys 'rate_limit:*'"
echo ""
echo "Check gateway logs:"
echo "  docker logs skillforge-gateway | grep 'Rate limit'"
echo ""
echo "Check rate limiting configuration:"
echo "  curl $GATEWAY_URL/actuator/env | grep rate.limit"
echo ""

echo -e "${GREEN}🎉 Rate limiting test completed!${NC}" 