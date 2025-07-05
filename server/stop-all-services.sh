#!/bin/bash

echo "🛑 Stopping SkillForge Services"
echo "==============================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Function to stop service by PID file
stop_service_by_pid() {
    local service_name=$1
    local pid_file="$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            print_status "Stopping $service_name (PID: $pid)..."
            kill $pid
            sleep 2
            
            # Check if process is still running
            if ps -p $pid > /dev/null 2>&1; then
                print_warning "$service_name is still running, force killing..."
                kill -9 $pid
            fi
            
            print_success "$service_name stopped"
        else
            print_warning "$service_name was not running"
        fi
        rm -f "$pid_file"
    else
        print_warning "No PID file found for $service_name"
    fi
}

# Function to stop service by port
stop_service_by_port() {
    local port=$1
    local service_name=$2
    
    local pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        print_status "Stopping $service_name on port $port (PID: $pid)..."
        kill $pid
        sleep 2
        
        # Check if process is still running
        if lsof -ti:$port >/dev/null 2>&1; then
            print_warning "$service_name is still running on port $port, force killing..."
            kill -9 $pid
        fi
        
        print_success "$service_name stopped"
    else
        print_warning "$service_name was not running on port $port"
    fi
}

# Stop services by PID files first
print_status "Stopping services by PID files..."
stop_service_by_pid "Gateway"
stop_service_by_pid "User Service"
stop_service_by_pid "Course Service"

# Stop any remaining services by port
print_status "Checking for any remaining services on ports..."
stop_service_by_port 8081 "Gateway"
stop_service_by_port 8082 "User Service"
stop_service_by_port 8083 "Course Service"

# Stop any remaining gradlew processes
print_status "Stopping any remaining Gradle processes..."
pkill -f "gradlew.*bootRun" 2>/dev/null || true

# Clean up log files
print_status "Cleaning up log files..."
rm -f *.log

echo ""
print_success "🎉 All services stopped successfully!"
echo ""
print_status "To start services again, run: ./start-all-services.sh" 