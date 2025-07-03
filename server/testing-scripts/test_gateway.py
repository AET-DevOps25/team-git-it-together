#!/usr/bin/env python3
"""
Simple API Gateway Security and Performance Test Script
Uses only built-in Python libraries
Tests only existing endpoints based on actual codebase
"""

import urllib.request
import urllib.parse
import urllib.error
import json
import time
import threading
import statistics
import os
import sys
from datetime import datetime
from typing import Dict, List, Optional, Union

class SimpleGatewayTester:
    def __init__(self):
        # Configuration
        self.gateway_url = "http://localhost:8081"
        self.user_service_url = "http://localhost:8082"
        self.course_service_url = "http://localhost:8083"
        self.test_username = "testuser"
        self.test_password = "testpass123"
        self.jwt_token = None
        self.user_id = None
        # Ensure results are in the same directory as the script
        self.script_dir = os.path.dirname(os.path.abspath(__file__))
        self.test_results_dir = os.path.join(self.script_dir, f"test-results-{datetime.now().strftime('%Y%m%d-%H%M%S')}")
        
        # Test counters
        self.total_tests = 0
        self.passed_tests = 0
        self.failed_tests = 0
        
        # Create results directory
        os.makedirs(self.test_results_dir, exist_ok=True)

    def log(self, message: str):
        """Log message with timestamp"""
        timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        log_message = f"[{timestamp}] {message}"
        print(log_message)
        with open(f"{self.test_results_dir}/test.log", "a") as f:
            f.write(log_message + "\n")

    def log_success(self, message: str):
        """Log success message"""
        self.log(f"✓ {message}")

    def log_error(self, message: str):
        """Log error message"""
        self.log(f"✗ {message}")

    def log_warning(self, message: str):
        """Log warning message"""
        self.log(f"⚠ {message}")

    def make_request(self, url: str, method: str = "GET", data: Optional[Dict] = None, headers: Optional[Dict] = None) -> Dict:
        """Make HTTP request using urllib"""
        try:
            if headers is None:
                headers = {}
            
            request_data: Optional[bytes] = None
            if data:
                request_data = json.dumps(data).encode('utf-8')
                headers['Content-Type'] = 'application/json'
            
            req = urllib.request.Request(url, data=request_data, headers=headers, method=method)
            
            start_time = time.time()
            with urllib.request.urlopen(req, timeout=10) as response:
                end_time = time.time()
                response_data = response.read().decode('utf-8')
                
                return {
                    "status_code": response.status,
                    "headers": dict(response.headers),
                    "data": response_data,
                    "latency": (end_time - start_time) * 1000 if start_time else 0,
                    "success": True
                }
        except urllib.error.HTTPError as e:
            end_time = time.time()
            return {
                "status_code": e.code,
                "headers": dict(e.headers),
                "data": e.read().decode('utf-8'),
                "latency": (end_time - start_time) * 1000,
                "success": False,
                "error": str(e)
            }
        except Exception as e:
            end_time = time.time()
            return {
                "status_code": 0,
                "latency": (end_time - start_time) * 1000,
                "success": False,
                "error": str(e)
            }

    def run_test(self, test_name: str, test_func, *args, **kwargs) -> bool:
        """Run a test and track results"""
        self.total_tests += 1
        self.log(f"Running test: {test_name}")
        
        try:
            result = test_func(*args, **kwargs)
            if result:
                self.log_success(f"Test passed: {test_name}")
                self.passed_tests += 1
                return True
            else:
                self.log_error(f"Test failed: {test_name}")
                self.failed_tests += 1
                return False
        except Exception as e:
            self.log_error(f"Test failed: {test_name} - Exception: {str(e)}")
            self.failed_tests += 1
            return False

    def check_services(self) -> bool:
        """Check if all services are running"""
        self.log("Checking if all services are running...")
        
        services = [
            ("Gateway", f"{self.gateway_url}/actuator/health"),
            ("User Service", f"{self.user_service_url}/api/v1/users/health"),
            ("Course Service", f"{self.course_service_url}/api/v1/courses/health")
        ]
        
        all_running = True
        for service_name, url in services:
            try:
                response = self.make_request(url)
                if response["status_code"] == 200:
                    self.log_success(f"{service_name} is running")
                else:
                    self.log_error(f"{service_name} is not responding properly (status: {response['status_code']})")
                    all_running = False
            except Exception as e:
                self.log_error(f"{service_name} is not running: {str(e)}")
                all_running = False
        
        return all_running

    def get_jwt_token(self) -> bool:
        """Register and login to get JWT token"""
        self.log("Registering test user and getting JWT token...")
        
        try:
            # Register user
            register_data = {
                "username": self.test_username,
                "email": f"{self.test_username}@test.com",
                "password": self.test_password,
                "firstName": "Test",
                "lastName": "User"
            }
            
            register_response = self.make_request(
                f"{self.gateway_url}/api/v1/users/register",
                method="POST",
                data=register_data
            )
            
            if register_response["status_code"] in [201, 409]:
                self.log_success(f"User registration successful (status: {register_response['status_code']})")
            else:
                self.log_warning(f"User registration failed (status: {register_response['status_code']})")
            
            # Login to get JWT token
            login_data = {
                "username": self.test_username,
                "password": self.test_password
            }
            
            login_response = self.make_request(
                f"{self.gateway_url}/api/v1/users/login",
                method="POST",
                data=login_data
            )
            
            if login_response["status_code"] == 200:
                try:
                    response_data = json.loads(login_response["data"])
                    self.jwt_token = response_data.get("jwtToken") or response_data.get("token")
                    self.user_id = response_data.get("id")
                    
                    if self.jwt_token and self.user_id:
                        self.log_success("JWT token and user ID obtained successfully")
                        with open(f"{self.test_results_dir}/user_info.txt", "w") as f:
                            f.write(f"User ID: {self.user_id}\nUsername: {self.test_username}")
                        return True
                    else:
                        self.log_error("JWT token or user ID not found in response")
                        return False
                except json.JSONDecodeError:
                    self.log_error("Invalid JSON response from login")
                    return False
            else:
                self.log_error(f"Login failed (status: {login_response['status_code']}): {login_response['data']}")
                return False
                
        except Exception as e:
            self.log_error(f"Failed to get JWT token: {str(e)}")
            return False

    def test_public_endpoints(self) -> bool:
        """Test public endpoints accessibility"""
        self.log("=== Testing Public Endpoints Accessibility ===")
        
        # Based on actual codebase - these are the public endpoints
        public_endpoints = [
            f"{self.gateway_url}/api/v1/courses/public",
            f"{self.gateway_url}/api/v1/courses/public/published",
            f"{self.gateway_url}/actuator/health",
            f"{self.gateway_url}/api/v1/users/health",
            f"{self.gateway_url}/api/v1/courses/health"
        ]
        
        all_accessible = True
        for endpoint in public_endpoints:
            try:
                response = self.make_request(endpoint)
                if response["status_code"] == 200:
                    self.log_success(f"Public endpoint accessible: {endpoint}")
                else:
                    self.log_error(f"Public endpoint not accessible: {endpoint} (status: {response['status_code']})")
                    all_accessible = False
            except Exception as e:
                self.log_error(f"Public endpoint error: {endpoint} - {str(e)}")
                all_accessible = False
        
        return all_accessible

    def test_protected_endpoints_no_auth(self) -> bool:
        """Test protected endpoints without authentication"""
        self.log("=== Testing Protected Endpoints Without Authentication ===")
        
        # Based on actual codebase - these are protected endpoints
        protected_endpoints = [
            f"{self.gateway_url}/api/v1/users/{self.user_id}/profile" if self.user_id else f"{self.gateway_url}/api/v1/users/some-user-id/profile",
            f"{self.gateway_url}/api/v1/courses",
            f"{self.gateway_url}/api/v1/courses/some-course-id"  # Test with a course ID
        ]
        
        all_protected = True
        for endpoint in protected_endpoints:
            try:
                response = self.make_request(endpoint)
                if response["status_code"] in [401, 403]:
                    self.log_success(f"Protected endpoint correctly rejects unauthenticated requests: {endpoint}")
                else:
                    self.log_error(f"Protected endpoint should reject unauthenticated requests: {endpoint} (status: {response['status_code']})")
                    all_protected = False
            except Exception as e:
                self.log_error(f"Protected endpoint test error: {endpoint} - {str(e)}")
                all_protected = False
        
        return all_protected

    def test_protected_endpoints_with_auth(self) -> bool:
        """Test protected endpoints with authentication"""
        self.log("=== Testing Protected Endpoints With Authentication ===")
        
        if not self.jwt_token or not self.user_id:
            self.log_error("No JWT token or user ID available for authentication tests")
            return False
        
        headers = {"Authorization": f"Bearer {self.jwt_token}"}
        # Based on actual codebase - these are protected endpoints that should work with auth
        protected_endpoints = [
            f"{self.gateway_url}/api/v1/users/{self.user_id}/profile",  # Use actual user ID
            f"{self.gateway_url}/api/v1/courses"
        ]
        
        all_accessible = True
        for endpoint in protected_endpoints:
            try:
                response = self.make_request(endpoint, headers=headers)
                if response["status_code"] == 200:
                    self.log_success(f"Protected endpoint accessible with JWT: {endpoint}")
                else:
                    self.log_error(f"Protected endpoint not accessible with JWT: {endpoint} (status: {response['status_code']})")
                    all_accessible = False
            except Exception as e:
                self.log_error(f"Protected endpoint auth test error: {endpoint} - {str(e)}")
                all_accessible = False
        
        return all_accessible

    def test_direct_microservice_access(self) -> bool:
        """Test direct microservice access (should be blocked)"""
        self.log("=== Testing Direct Microservice Access (Should Be Blocked) ===")
        
        # Based on actual codebase - these are direct microservice endpoints
        direct_endpoints = [
            f"{self.user_service_url}/api/v1/users/{self.user_id}/profile" if self.user_id else f"{self.user_service_url}/api/v1/users/some-user-id/profile",
            f"{self.course_service_url}/api/v1/courses"
        ]
        
        all_blocked = True
        for endpoint in direct_endpoints:
            try:
                response = self.make_request(endpoint)
                # 401/403 responses indicate proper security blocking
                if response["status_code"] in [401, 403]:
                    self.log_success(f"Direct microservice access correctly blocked with auth error: {endpoint} (status: {response['status_code']})")
                else:
                    # If we get a 200 response, it means direct access is possible (security issue)
                    self.log_error(f"Direct microservice access should be blocked: {endpoint} (status: {response['status_code']})")
                    all_blocked = False
            except Exception as e:
                # Network-level blocking (also acceptable)
                self.log_success(f"Direct microservice access blocked at network level: {endpoint}")
        
        return all_blocked

    def test_rate_limiting(self) -> bool:
        """Test rate limiting"""
        self.log("=== Testing Rate Limiting ===")
        
        if not self.jwt_token:
            self.log_error("No JWT token available for rate limiting tests")
            return False
        
        headers = {"Authorization": f"Bearer {self.jwt_token}"}
        rate_limit_hit = 0
        successful_requests = 0
        
        self.log(f"Sending burst of 100 requests to trigger rate limiting...")
        
        # Send requests as fast as possible to trigger rate limiting
        for i in range(100):
            try:
                response = self.make_request(f"{self.gateway_url}/api/v1/courses", headers=headers)
                if response["status_code"] == 429:
                    rate_limit_hit += 1
                elif response["status_code"] == 200:
                    successful_requests += 1
                
                # No delay to maximize rate
            except Exception as e:
                self.log_warning(f"Request {i+1} failed: {str(e)}")
        
        self.log(f"Rate limiting results: {successful_requests} successful, {rate_limit_hit} rate limited")
        
        if rate_limit_hit > 0:
            self.log_success(f"Rate limiting is working (hit {rate_limit_hit} times)")
            return True
        else:
            self.log_warning("Rate limiting may not be working (no 429 responses)")
            return False

    def test_latency(self) -> bool:
        """Test latency"""
        self.log("=== Testing Latency ===")
        
        if not self.jwt_token:
            self.log_error("No JWT token available for latency testing")
            return False
        
        headers = {"Authorization": f"Bearer {self.jwt_token}"}
        latencies = []
        
        for i in range(20):
            try:
                response = self.make_request(f"{self.gateway_url}/api/v1/courses", headers=headers)
                if response["success"]:
                    latencies.append(response["latency"])
            except Exception as e:
                self.log_warning(f"Latency test request {i+1} failed: {str(e)}")
            
            time.sleep(0.1)
        
        if latencies:
            avg_latency = statistics.mean(latencies)
            median_latency = statistics.median(latencies)
            min_latency = min(latencies)
            max_latency = max(latencies)
            
            self.log(f"Latency test results:")
            self.log(f"Average latency: {avg_latency:.2f}ms")
            self.log(f"Median latency: {median_latency:.2f}ms")
            self.log(f"Min latency: {min_latency:.2f}ms")
            self.log(f"Max latency: {max_latency:.2f}ms")
            
            # Save latency results
            latency_results = {
                "average": avg_latency,
                "median": median_latency,
                "min": min_latency,
                "max": max_latency,
                "all_measurements": latencies
            }
            
            with open(f"{self.test_results_dir}/latency_results.json", "w") as f:
                json.dump(latency_results, f, indent=2)
            
            return avg_latency < 1000  # Consider good if average < 1 second
        else:
            self.log_error("No successful latency measurements")
            return False

    def test_security_headers(self) -> bool:
        """Test security headers"""
        self.log("=== Testing Security Headers ===")
        
        try:
            response = self.make_request(f"{self.gateway_url}/api/v1/courses/public")
            headers = response.get("headers", {})
            
            security_headers = [
                "X-Content-Type-Options",
                "X-Frame-Options",
                "X-XSS-Protection"
            ]
            
            headers_present = 0
            for header in security_headers:
                if header in headers:
                    self.log_success(f"{header} header present")
                    headers_present += 1
                else:
                    self.log_warning(f"{header} header missing")
            
            return headers_present >= 2  # At least 2 security headers should be present
        except Exception as e:
            self.log_error(f"Security headers test failed: {str(e)}")
            return False

    def generate_report(self):
        """Generate final report"""
        self.log("=== Generating Test Report ===")
        success_rate = (self.passed_tests / self.total_tests) * 100 if self.total_tests > 0 else 0
        report_path = os.path.join(self.script_dir, "TESTING_REPORT.md")
        now = datetime.now()
        with open(report_path, "w") as f:
            f.write("# 🚦 API Gateway Security & Performance Test Report\n\n")
            f.write(f"🕒 **Generated at:** {now.strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            f.write(f"📁 **Results Directory:** `{os.path.basename(self.test_results_dir)}`\n\n")
            f.write("---\n\n")
            f.write("## 📝 Test Summary\n\n")
            f.write(f"- 🧪 **Total Tests:** `{self.total_tests}`\n")
            f.write(f"- ✅ **Passed:** `{self.passed_tests}`\n")
            f.write(f"- ❌ **Failed:** `{self.failed_tests}`\n")
            f.write(f"- 📊 **Success Rate:** `{success_rate:.2f}%`\n\n")
            f.write("---\n\n")
            f.write("## ⚙️ Configuration\n\n")
            f.write(f"- 🌐 **Gateway URL:** `{self.gateway_url}`\n")
            f.write(f"- 👤 **User Service URL:** `{self.user_service_url}`\n")
            f.write(f"- 📚 **Course Service URL:** `{self.course_service_url}`\n")
            f.write("- 🚦 **Rate Limit (per minute):** `60`\n")
            f.write("- 🚦 **Rate Limit (per second):** `10`\n")
            f.write("- 🚦 **Burst Capacity:** `20`\n\n")
            f.write("---\n\n")
            f.write("## 🧪 Test Categories\n\n")
            f.write("1. 🌍 Public Endpoints Accessibility\n")
            f.write("2. 🔒 Protected Endpoints Without Authentication\n")
            f.write("3. 🔑 Protected Endpoints With Authentication\n")
            f.write("4. 🚫 Direct Microservice Access\n")
            f.write("5. 🚦 Rate Limiting\n")
            f.write("6. ⏱️ Latency\n")
            f.write("7. 🛡️ Security Headers\n\n")
            f.write("---\n\n")
            f.write(f"## 🎉 {'All tests passed!' if self.failed_tests == 0 else f'{self.failed_tests} test(s) failed. Please review the details above.'}" + "\n")
        self.log(f"Final report generated: {report_path}")
        # Print summary
        print("\n" + "=" * 50)
        print("TEST SUMMARY")
        print("=" * 50)
        print(f"Total Tests: {self.total_tests}")
        print(f"Passed: {self.passed_tests}")
        print(f"Failed: {self.failed_tests}")
        print(f"Success Rate: {success_rate:.2f}%")
        print(f"Results saved in: {self.test_results_dir}")
        print(f"Report: {report_path}")
        print("=" * 50)

    def run_all_tests(self):
        """Run all tests"""
        self.log("Starting API Gateway Security and Performance Tests")
        self.log(f"Results will be saved to: {self.test_results_dir}")
        
        # Check if required services are running
        if not self.check_services():
            self.log_error("Some services are not running. Please start all services first.")
            return False
        
        # Get JWT token
        if not self.get_jwt_token():
            self.log_error("Failed to get JWT token. Cannot proceed with authenticated tests.")
            return False
        
        # Run all tests
        tests = [
            ("Public Endpoints", self.test_public_endpoints),
            ("Protected Endpoints No Auth", self.test_protected_endpoints_no_auth),
            ("Protected Endpoints With Auth", self.test_protected_endpoints_with_auth),
            ("Direct Microservice Access", self.test_direct_microservice_access),
            ("Rate Limiting", self.test_rate_limiting),
            ("Latency", self.test_latency),
            ("Security Headers", self.test_security_headers)
        ]
        
        for test_name, test_func in tests:
            self.run_test(test_name, test_func)
        
        # Generate report
        self.generate_report()
        
        # Return overall success
        return self.failed_tests == 0

def main():
    tester = SimpleGatewayTester()
    success = tester.run_all_tests()
    
    if success:
        print("\n🎉 All tests passed!")
        sys.exit(0)
    else:
        print(f"\n❌ {tester.failed_tests} tests failed. Check the report for details.")
        sys.exit(1)

if __name__ == "__main__":
    main() 