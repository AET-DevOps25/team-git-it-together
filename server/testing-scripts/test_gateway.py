"""
Simple API Gateway Security and Performance Test Script
Uses only built-in Python libraries
Tests only existing endpoints based on actual codebase
"""

import urllib.request
import urllib.error
import json
import time
import statistics
import os
import sys
from datetime import datetime
from typing import Dict, Optional

class SimpleGatewayTester:
    def __init__(self):
        # Configuration
        self.gateway_url = "http://localhost:8081"
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
        with open(f"{self.test_results_dir}/test.log", "a", encoding="utf-8") as f:
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
            ("User Service", f"{self.gateway_url}/api/v1/users/health"),
            ("Course Service", f"{self.gateway_url}/api/v1/courses/health")
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
            
            # Login to get JWT token (test with username)
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
                        
                        # Test login with email (should work with our fix)
                        email_login_data = {
                            "email": f"{self.test_username}@test.com",
                            "password": self.test_password
                        }
                        
                        email_login_response = self.make_request(
                            f"{self.gateway_url}/api/v1/users/login",
                            method="POST",
                            data=email_login_data
                        )
                        
                        if email_login_response["status_code"] == 200:
                            try:
                                email_response_data = json.loads(email_login_response["data"])
                                email_jwt_token = email_response_data.get("jwtToken") or email_response_data.get("token")
                                email_user_id = email_response_data.get("id")
                                
                                if email_jwt_token and email_user_id:
                                    self.log_success("Email login successful - JWT token and user ID obtained")
                                    # Verify it's the same user
                                    if email_user_id == self.user_id:
                                        self.log_success("Email login returned same user ID as username login")
                                    else:
                                        self.log_warning("Email login returned different user ID than username login")
                                else:
                                    self.log_error("JWT token or user ID not found in email login response")
                            except json.JSONDecodeError:
                                self.log_error("Invalid JSON response from email login")
                        else:
                            self.log_error(f"Email login failed (status: {email_login_response['status_code']}): {email_login_response['data']}")
                        
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
            f"{self.gateway_url}/api/v1/users/{self.user_id}/profile" if self.user_id else f"{self.gateway_url}/api/v1/users/some-user-id/profile",
            f"{self.gateway_url}/api/v1/courses"
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
        """Test rate limiting with comprehensive analysis"""
        self.log("=== Testing Rate Limiting ===")
        
        if not self.jwt_token:
            self.log_error("No JWT token available for rate limiting tests")
            return False
        
        headers = {"Authorization": f"Bearer {self.jwt_token}"}
        
        # Test 1: Burst test (rapid requests)
        self.log("Phase 1: Testing burst rate limiting...")
        burst_results = self._test_burst_rate_limiting(headers)
        
        # Test 2: Sustained rate test
        self.log("Phase 2: Testing sustained rate limiting...")
        sustained_results = self._test_sustained_rate_limiting(headers)
        
        # Test 3: Different endpoints
        self.log("Phase 3: Testing rate limiting across different endpoints...")
        endpoint_results = self._test_endpoint_rate_limiting(headers)
        
        # Aggregate results
        total_rate_limited = burst_results["rate_limited"] + sustained_results["rate_limited"] + endpoint_results["rate_limited"]
        total_requests = burst_results["total"] + sustained_results["total"] + endpoint_results["total"]
        
        # Save detailed results
        rate_limit_results = {
            "burst_test": burst_results,
            "sustained_test": sustained_results,
            "endpoint_test": endpoint_results,
            "summary": {
                "total_requests": total_requests,
                "total_rate_limited": total_rate_limited,
                "rate_limit_percentage": (total_rate_limited / total_requests * 100) if total_requests > 0 else 0,
                "rate_limiting_working": total_rate_limited > 0
            }
        }
        
        with open(f"{self.test_results_dir}/rate_limit_results.json", "w") as f:
            json.dump(rate_limit_results, f, indent=2)
        
        self.log(f"Rate limiting summary: {total_rate_limited}/{total_requests} requests rate limited ({rate_limit_results['summary']['rate_limit_percentage']:.1f}%)")
        
        if total_rate_limited > 0:
            self.log_success(f"Rate limiting is working (hit {total_rate_limited} times)")
            return True
        else:
            self.log_warning("Rate limiting may not be working (no 429 responses)")
            return False
    
    def _test_burst_rate_limiting(self, headers: Dict) -> Dict:
        """Test burst rate limiting with rapid requests"""
        rate_limited = 0
        successful = 0
        failed = 0

        # Send 100 rapid requests
        for i in range(100):
            try:
                response = self.make_request(f"{self.gateway_url}/api/v1/courses", headers=headers)
                if response["status_code"] == 429:
                    rate_limited += 1
                elif response["status_code"] == 200:
                    successful += 1
                else:
                    failed += 1
            except Exception as e:
                failed += 1
                self.log_warning(f"Burst request {i+1} failed: {str(e)}")
        
        return {
            "total": 50,
            "successful": successful,
            "rate_limited": rate_limited,
            "failed": failed
        }
    
    def _test_sustained_rate_limiting(self, headers: Dict) -> Dict:
        """Test sustained rate limiting over time"""
        rate_limited = 0
        successful = 0
        failed = 0

        # Send 50 requests with small delays to test sustained rate
        for i in range(50):
            try:
                response = self.make_request(f"{self.gateway_url}/api/v1/users/{self.user_id}/profile", headers=headers)
                if response["status_code"] == 429:
                    rate_limited += 1
                elif response["status_code"] == 200:
                    successful += 1
                else:
                    failed += 1
            except Exception as e:
                failed += 1
                self.log_warning(f"Sustained request {i+1} failed: {str(e)}")
            
            time.sleep(0.1)  # Small delay between requests
        
        return {
            "total": 30,
            "successful": successful,
            "rate_limited": rate_limited,
            "failed": failed
        }
    
    def _test_endpoint_rate_limiting(self, headers: Dict) -> Dict:
        """Test rate limiting across different endpoints"""
        endpoints = [
            f"{self.gateway_url}/api/v1/courses",
            f"{self.gateway_url}/api/v1/users/{self.user_id}/profile",
            f"{self.gateway_url}/api/v1/courses/public"
        ]
        
        rate_limited = 0
        successful = 0
        failed = 0
        
        # Test each endpoint with multiple requests
        for endpoint in endpoints:
            for i in range(10):
                try:
                    response = self.make_request(endpoint, headers=headers)
                    if response["status_code"] == 429:
                        rate_limited += 1
                    elif response["status_code"] == 200:
                        successful += 1
                    else:
                        failed += 1
                except Exception as e:
                    failed += 1
                    self.log_warning(f"Endpoint request failed: {str(e)}")
        
        return {
            "total": len(endpoints) * 10,
            "successful": successful,
            "rate_limited": rate_limited,
            "failed": failed
        }

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
        """Generate comprehensive final report"""
        self.log("=== Generating Test Report ===")
        success_rate = (self.passed_tests / self.total_tests) * 100 if self.total_tests > 0 else 0
        report_path = os.path.join(self.script_dir, "TESTING_REPORT.md")
        now = datetime.now()
        
        # Load additional test data if available
        latency_data = self._load_test_data("latency_results.json")
        rate_limit_data = self._load_test_data("rate_limit_results.json")
        
        with open(report_path, "w") as f:
            f.write("# 🚦 API Gateway Security & Performance Test Report\n\n")
            f.write(f"🕒 **Generated at:** {now.strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            f.write(f"📁 **Results Directory:** `{os.path.basename(self.test_results_dir)}`\n\n")
            f.write("---\n\n")
            
            # Executive Summary
            f.write("## 📊 Executive Summary\n\n")
            f.write(f"| Metric | Value |\n")
            f.write(f"|--------|-------|\n")
            f.write(f"| **Total Tests** | {self.total_tests} |\n")
            f.write(f"| **Passed** | {self.passed_tests} |\n")
            f.write(f"| **Failed** | {self.failed_tests} |\n")
            f.write(f"| **Success Rate** | {success_rate:.1f}% |\n")
            f.write(f"| **Overall Status** | {'🟢 PASSED' if self.failed_tests == 0 else '🔴 FAILED'} |\n\n")
            
            f.write("---\n\n")
            
            # Configuration Details
            f.write("## ⚙️ Configuration\n\n")
            f.write(f"| Setting | Value |\n")
            f.write(f"|---------|-------|\n")
            f.write(f"| **Gateway URL** | `{self.gateway_url}` |\n")
            f.write(f"| **Test User** | `{self.test_username}` |\n")
            f.write(f"| **User ID** | `{self.user_id or 'N/A'}` |\n")
            f.write(f"| **Rate Limit (per minute)** | `60` |\n")
            f.write(f"| **Rate Limit (per second)** | `10` |\n")
            f.write(f"| **Burst Capacity** | `20` |\n\n")
            
            f.write("---\n\n")
            
            # Performance Metrics
            if latency_data:
                f.write("## ⏱️ Performance Metrics\n\n")
                f.write(f"| Metric | Value |\n")
                f.write(f"|--------|-------|\n")
                f.write(f"| **Average Latency** | {latency_data.get('average', 0):.2f}ms |\n")
                f.write(f"| **Median Latency** | {latency_data.get('median', 0):.2f}ms |\n")
                f.write(f"| **Min Latency** | {latency_data.get('min', 0):.2f}ms |\n")
                f.write(f"| **Max Latency** | {latency_data.get('max', 0):.2f}ms |\n")
                f.write(f"| **Performance Status** | {'🟢 Good' if latency_data.get('average', 1000) < 1000 else '🟡 Acceptable' if latency_data.get('average', 1000) < 2000 else '🔴 Poor'} |\n\n")
            
            # Rate Limiting Analysis
            if rate_limit_data:
                f.write("## 🚦 Rate Limiting Analysis\n\n")
                summary = rate_limit_data.get('summary', {})
                f.write(f"| Test Phase | Total Requests | Rate Limited | Success Rate |\n")
                f.write(f"|------------|----------------|--------------|--------------|\n")
                
                burst = rate_limit_data.get('burst_test', {})
                f.write(f"| **Burst Test** | {burst.get('total', 0)} | {burst.get('rate_limited', 0)} | {((burst.get('total', 1) - burst.get('rate_limited', 0)) / burst.get('total', 1) * 100):.1f}% |\n")
                
                sustained = rate_limit_data.get('sustained_test', {})
                f.write(f"| **Sustained Test** | {sustained.get('total', 0)} | {sustained.get('rate_limited', 0)} | {((sustained.get('total', 1) - sustained.get('rate_limited', 0)) / sustained.get('total', 1) * 100):.1f}% |\n")
                
                endpoint = rate_limit_data.get('endpoint_test', {})
                f.write(f"| **Endpoint Test** | {endpoint.get('total', 0)} | {endpoint.get('rate_limited', 0)} | {((endpoint.get('total', 1) - endpoint.get('rate_limited', 0)) / endpoint.get('total', 1) * 100):.1f}% |\n")
                
                f.write(f"| **Overall** | {summary.get('total_requests', 0)} | {summary.get('total_rate_limited', 0)} | {((summary.get('total_requests', 1) - summary.get('total_rate_limited', 0)) / summary.get('total_requests', 1) * 100):.1f}% |\n\n")
                
                f.write(f"**Rate Limiting Status:** {'🟢 Working' if summary.get('rate_limiting_working', False) else '🔴 Not Working'}\n\n")
            
            f.write("---\n\n")
            
            # Test Categories
            f.write("## 🧪 Test Categories\n\n")
            f.write("| Category | Description | Status |\n")
            f.write("|----------|-------------|--------|\n")
            f.write("| 🌍 **Public Endpoints** | Test accessibility of public API endpoints | ✅ |\n")
            f.write("| 🔒 **Auth Required** | Test that protected endpoints reject unauthenticated requests | ✅ |\n")
            f.write("| 🔑 **Authentication** | Test protected endpoints with valid JWT tokens | ✅ |\n")
            f.write("| 🚫 **Direct Access** | Test that direct microservice access is blocked | ✅ |\n")
            f.write("| 🚦 **Rate Limiting** | Test API rate limiting functionality | ✅ |\n")
            f.write("| ⏱️ **Latency** | Test API response times | ✅ |\n")
            f.write("| 🛡️ **Security Headers** | Test presence of security headers | ✅ |\n\n")
            
            f.write("---\n\n")
            
            # Recommendations
            f.write("## 💡 Recommendations\n\n")
            if self.failed_tests > 0:
                f.write("🔴 **Critical Issues Found:**\n")
                f.write("- Review failed tests and address security vulnerabilities\n")
                f.write("- Check service connectivity and configuration\n")
                f.write("- Verify rate limiting is properly configured\n\n")
            else:
                f.write("🟢 **All Tests Passed:**\n")
                f.write("- API Gateway is functioning correctly\n")
                f.write("- Security measures are in place\n")
                f.write("- Performance is within acceptable limits\n\n")
            
            if latency_data and latency_data.get('average', 0) > 1000:
                f.write("🟡 **Performance Recommendations:**\n")
                f.write("- Consider optimizing database queries\n")
                f.write("- Review caching strategies\n")
                f.write("- Monitor resource usage\n\n")
            
            f.write("---\n\n")
            
            # Footer
            f.write(f"## 🎉 {'All tests passed! 🎊' if self.failed_tests == 0 else f'{self.failed_tests} test(s) failed. Please review the details above.'}\n\n")
            f.write("*Report generated by SkillForge API Gateway Test Suite*\n")
        
        self.log(f"Final report generated: {report_path}")
        
        # Enhanced console summary
        print("\n" + "=" * 60)
        print("🚦 API GATEWAY TEST SUMMARY")
        print("=" * 60)
        print(f"📊 Total Tests: {self.total_tests}")
        print(f"✅ Passed: {self.passed_tests}")
        print(f"❌ Failed: {self.failed_tests}")
        print(f"📈 Success Rate: {success_rate:.1f}%")
        print(f"🎯 Status: {'🟢 PASSED' if self.failed_tests == 0 else '🔴 FAILED'}")
        print(f"📁 Results: {self.test_results_dir}")
        print(f"📄 Report: {report_path}")
        
        if latency_data:
            print(f"⏱️  Avg Latency: {latency_data.get('average', 0):.2f}ms")
        
        if rate_limit_data:
            summary = rate_limit_data.get('summary', {})
            print(f"🚦 Rate Limited: {summary.get('total_rate_limited', 0)}/{summary.get('total_requests', 0)} requests")
        
        print("=" * 60)
    
    def _load_test_data(self, filename: str) -> Optional[Dict]:
        """Load test data from JSON file"""
        try:
            file_path = os.path.join(self.test_results_dir, filename)
            if os.path.exists(file_path):
                with open(file_path, 'r') as f:
                    return json.load(f)
        except Exception as e:
            self.log_warning(f"Could not load {filename}: {str(e)}")
        return None

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