#!/bin/bash

set -e

echo "🚀 SkillForge Full Stack Startup Script"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if a port is in use
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Port $port is already in use. $service might already be running."
        return 1
    else
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url" >/dev/null 2>&1; then
            print_success "$service_name is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service_name failed to start within $((max_attempts * 2)) seconds"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local health_url=$4
    
    print_status "Starting $service_name..."
    
    # Check if port is available
    if ! check_port $port $service_name; then
        return 1
    fi
    
    # Start the service in background using the service's gradlew
    cd "$service_dir" && ./gradlew bootRun > "../$service_name.log" 2>&1 &
    local pid=$!
    cd ..
    echo $pid > "$service_name.pid"
    
    print_status "$service_name started with PID $pid"
    
    # Wait for service to be ready
    if wait_for_service $health_url $service_name; then
        print_success "$service_name is running and healthy"
        return 0
    else
        print_error "$service_name failed to start properly"
        return 1
    fi
}

# Check if we're in the right directory
if [ ! -f "settings.gradle" ]; then
    print_error "Please run this script from the server root directory"
    exit 1
fi

# Check if dependencies are running
print_status "Checking dependencies..."

# Check Redis
if ! nc -z localhost 6379; then
    print_error "Redis is not running. Please start it first with: ./start-dev.sh"
    exit 1
fi
print_success "Redis is running"

# Check MongoDB
if ! nc -z localhost 27017; then
    print_error "MongoDB is not running. Please start it first with: ./start-dev.sh"
    exit 1
fi
print_success "MongoDB is running"

# Set JWT secret if not already set
if [ -z "$JWT_SECRET" ]; then
    # Generate a random secret
    JWT_SECRET=$(openssl rand -hex 256 | tr -dc 'a-zA-Z0-9')
    export JWT_SECRET=$JWT_SECRET
    print_warning "JWT_SECRET not set, using generated secret"
fi

# Set JWT expiration if not already set
if [ -z "$JWT_EXPIRATION_MS" ]; then
    JWT_EXPIRATION_MS=3600000
    export JWT_EXPIRATION_MS
    print_warning "JWT_EXPIRATION_MS not set, using default 3600000"
fi

# Clean up any existing PID files
rm -f *.pid

print_status "Starting all services..."

# Start Gateway
if start_service "Gateway" "skillforge-gateway" 8081 "http://localhost:8081/actuator/health"; then
    GATEWAY_STARTED=true
else
    GATEWAY_STARTED=false
fi

# Start User Service
if start_service "User Service" "./skillforge-user" 8082 "http://localhost:8082/actuator/health"; then
    USER_STARTED=true
else
    USER_STARTED=false
fi

# Start Course Service
if start_service "Course Service" "./skillforge-course" 8083 "http://localhost:8083/actuator/health"; then
    COURSE_STARTED=true
else
    COURSE_STARTED=false
fi

echo ""
echo "📊 Startup Summary"
echo "=================="

if [ "$GATEWAY_STARTED" = true ]; then
    print_success "✅ Gateway: http://localhost:8081"
else
    print_error "❌ Gateway: Failed to start"
fi

if [ "$USER_STARTED" = true ]; then
    print_success "✅ User Service: http://localhost:8082"
else
    print_error "❌ User Service: Failed to start"
fi

if [ "$COURSE_STARTED" = true ]; then
    print_success "✅ Course Service: http://localhost:8083"
else
    print_error "❌ Course Service: Failed to start"
fi

echo ""
echo "🔧 Useful Commands"
echo "=================="
echo "View logs:"
echo "  tail -f Gateway.log"
echo "  tail -f User\ Service.log"
echo "  tail -f Course\ Service.log"
echo ""
echo "Stop all services:"
echo "  ./stop-all-services.sh"
echo ""
echo "Test the system:"
echo "  ./test-rate-limiting.sh"
echo ""
echo "Health checks:"
echo "  curl http://localhost:8081/actuator/health"
echo "  curl http://localhost:8082/actuator/health"
echo "  curl http://localhost:8083/actuator/health"

# If all services started successfully, run a quick integration test
if [ "$GATEWAY_STARTED" = true ] && [ "$USER_STARTED" = true ] && [ "$COURSE_STARTED" = true ]; then
    echo ""
    print_success "🎉 All services started successfully!"
    echo ""
    print_status "Running quick integration test..."
    
    # Test gateway routing
    if curl -s http://localhost:8081/actuator/health | grep -q "UP"; then
        print_success "Gateway health check: OK"
    else
        print_error "Gateway health check: FAILED"
    fi
    
    # Test user service through gateway
    if curl -s http://localhost:8081/api/v1/users/login >/dev/null 2>&1; then
        print_success "Gateway routing to user service: OK"
    else
        print_warning "Gateway routing to user service: May need investigation"
    fi
    
    # Test course service through gateway
    if curl -s http://localhost:8081/api/v1/courses/public >/dev/null 2>&1; then
        print_success "Gateway routing to course service: OK"
    else
        print_warning "Gateway routing to course service: May need investigation"
    fi
else
    echo ""
    print_warning "⚠️  Some services failed to start. Check the logs for details."
fi

echo ""
print_status "Services are running in the background. Use 'ps aux | grep gradlew' to see running processes." 