package com.gitittogether.skillforge.server.gateway.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocsController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getDocsPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>SkillForge API Documentation</title>
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
                <style>
                    :root {
                        --primary: #6366f1;
                        --primary-dark: #4f46e5;
                        --secondary: #8b5cf6;
                        --accent: #06b6d4;
                        --background: #0f172a;
                        --surface: #1e293b;
                        --surface-light: #334155;
                        --text: #f8fafc;
                        --text-secondary: #cbd5e1;
                        --border: #475569;
                        --success: #10b981;
                        --warning: #f59e0b;
                        --error: #ef4444;
                    }
                    
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                        background: var(--background);
                        color: var(--text);
                        min-height: 100vh;
                        line-height: 1.6;
                        overflow-x: hidden;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 2rem;
                        min-height: 100vh;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                    }
                    
                    .header {
                        text-align: center;
                        margin-bottom: 4rem;
                        animation: fadeInUp 0.8s ease-out;
                    }
                    
                    .logo {
                        font-size: 3.5rem;
                        font-weight: 700;
                        background: linear-gradient(135deg, var(--primary), var(--secondary), var(--accent));
                        background-size: 200% 200%;
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: gradientShift 3s ease-in-out infinite;
                        margin-bottom: 1rem;
                    }
                    
                    .subtitle {
                        font-size: 1.25rem;
                        color: var(--text-secondary);
                        font-weight: 400;
                        margin-bottom: 0.5rem;
                    }
                    
                    .description {
                        font-size: 1rem;
                        color: var(--text-secondary);
                        max-width: 600px;
                        margin: 0 auto;
                    }
                    
                    .docs-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
                        gap: 2rem;
                        margin-bottom: 3rem;
                    }
                    
                    .doc-card {
                        background: var(--surface);
                        border: 1px solid var(--border);
                        border-radius: 16px;
                        padding: 2.5rem;
                        text-decoration: none;
                        color: inherit;
                        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                        position: relative;
                        overflow: hidden;
                        backdrop-filter: blur(10px);
                        animation: fadeInUp 0.8s ease-out 0.2s both;
                    }
                    
                    .doc-card:nth-child(2) {
                        animation-delay: 0.4s;
                    }
                    
                    .doc-card::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 3px;
                        background: linear-gradient(90deg, var(--primary), var(--secondary), var(--accent));
                        transform: scaleX(0);
                        transition: transform 0.3s ease;
                    }
                    
                    .doc-card::after {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                        background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.1));
                        opacity: 0;
                        transition: opacity 0.3s ease;
                    }
                    
                    .doc-card:hover {
                        transform: translateY(-8px) scale(1.02);
                        border-color: var(--primary);
                        box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
                    }
                    
                    .doc-card:hover::before {
                        transform: scaleX(1);
                    }
                    
                    .doc-card:hover::after {
                        opacity: 1;
                    }
                    
                    .doc-icon {
                        font-size: 3rem;
                        margin-bottom: 1.5rem;
                        display: block;
                        filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
                    }
                    
                    .doc-title {
                        font-size: 1.5rem;
                        font-weight: 600;
                        margin-bottom: 1rem;
                        color: var(--text);
                    }
                    
                    .doc-description {
                        color: var(--text-secondary);
                        font-size: 0.95rem;
                        line-height: 1.6;
                        margin-bottom: 1.5rem;
                    }
                    
                    .doc-link {
                        display: inline-flex;
                        align-items: center;
                        gap: 0.5rem;
                        color: var(--primary);
                        font-weight: 500;
                        font-size: 0.9rem;
                        text-transform: uppercase;
                        letter-spacing: 0.05em;
                        transition: all 0.3s ease;
                    }
                    
                    .doc-link:hover {
                        color: var(--accent);
                        transform: translateX(4px);
                    }
                    
                    .gateway-info {
                        background: var(--surface);
                        border: 1px solid var(--border);
                        border-radius: 16px;
                        padding: 2rem;
                        text-align: center;
                        backdrop-filter: blur(10px);
                        animation: fadeInUp 0.8s ease-out 0.6s both;
                    }
                    
                    .gateway-title {
                        font-weight: 600;
                        color: var(--text);
                        margin-bottom: 1rem;
                        font-size: 1.25rem;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 0.5rem;
                    }
                    
                    .gateway-text {
                        color: var(--text-secondary);
                        font-size: 0.95rem;
                        line-height: 1.6;
                        max-width: 800px;
                        margin: 0 auto;
                    }
                    
                    .features {
                        display: flex;
                        justify-content: center;
                        gap: 2rem;
                        margin-top: 1.5rem;
                        flex-wrap: wrap;
                    }
                    
                    .feature {
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                        color: var(--text-secondary);
                        font-size: 0.85rem;
                        padding: 0.5rem 1rem;
                        background: var(--surface-light);
                        border-radius: 20px;
                        border: 1px solid var(--border);
                    }
                    
                    @keyframes fadeInUp {
                        from {
                            opacity: 0;
                            transform: translateY(30px);
                        }
                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }
                    
                    @keyframes gradientShift {
                        0%, 100% {
                            background-position: 0% 50%;
                        }
                        50% {
                            background-position: 100% 50%;
                        }
                    }
                    
                    @media (max-width: 768px) {
                        .container {
                            padding: 1rem;
                        }
                        
                        .logo {
                            font-size: 2.5rem;
                        }
                        
                        .docs-grid {
                            grid-template-columns: 1fr;
                            gap: 1.5rem;
                        }
                        
                        .doc-card {
                            padding: 2rem;
                        }
                        
                        .features {
                            flex-direction: column;
                            align-items: center;
                        }
                    }
                    
                    @media (max-width: 480px) {
                        .logo {
                            font-size: 2rem;
                        }
                        
                        .subtitle {
                            font-size: 1.1rem;
                        }
                        
                        .doc-card {
                            padding: 1.5rem;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🚀 SkillForge</div>
                        <p class="subtitle">API Documentation Hub</p>
                        <p class="description">
                            Explore our comprehensive API documentation and discover the power of our microservices architecture
                        </p>
                    </div>
                    
                    <div class="docs-grid">
                        <a href="/api/v1/users/docs" class="doc-card">
                            <span class="doc-icon">👥</span>
                            <div class="doc-title">User Service API</div>
                            <div class="doc-description">
                                Complete user management system including authentication, registration, profile management, 
                                and user-specific operations with JWT token handling.
                            </div>
                            <div class="doc-link">
                                Explore API <span>→</span>
                            </div>
                        </a>
                        
                        <a href="/api/v1/courses/docs" class="doc-card">
                            <span class="doc-icon">📚</span>
                            <div class="doc-title">Course Service API</div>
                            <div class="doc-description">
                                Comprehensive course management platform featuring content delivery, progress tracking, 
                                learning analytics, and AI-powered course generation capabilities.
                            </div>
                            <div class="doc-link">
                                Explore API <span>→</span>
                            </div>
                        </a>
                    </div>
                    
                    <div class="gateway-info">
                        <div class="gateway-title">
                            <span>🔗</span> API Gateway
                        </div>
                        <div class="gateway-text">
                            This intelligent gateway serves as the central entry point for all API requests, providing 
                            seamless routing, authentication, rate limiting, and comprehensive request/response logging 
                            across our microservices architecture.
                        </div>
                        <div class="features">
                            <div class="feature">
                                <span>🔐</span> JWT Authentication
                            </div>
                            <div class="feature">
                                <span>⚡</span> Rate Limiting
                            </div>
                            <div class="feature">
                                <span>📊</span> Request Logging
                            </div>
                            <div class="feature">
                                <span>🛡️</span> Security Filtering
                            </div>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
} 