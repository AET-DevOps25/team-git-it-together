#!/bin/bash

# Minimal JWT Secret Generator & Copier
# Generates a secure JWT secret, prints it, and copies it to clipboard

set -e

echo "🔐 Generating a secure JWT secret..."

# Generate secret (32 random alphanumeric chars)
if command -v openssl &> /dev/null; then
    JWT_SECRET=$(openssl rand -base64 256 | tr -d "=+/" | cut -c1-32)
else
    JWT_SECRET=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
fi

echo ""
echo "Your JWT_SECRET is:"
echo ""
echo "$JWT_SECRET"
echo ""

# Try to copy to clipboard if pbcopy or xclip available
if command -v pbcopy &> /dev/null; then
    echo "$JWT_SECRET" | pbcopy
    echo "✅ Secret copied to clipboard (pbcopy)"
elif command -v xclip &> /dev/null; then
    echo "$JWT_SECRET" | xclip -selection clipboard
    echo "✅ Secret copied to clipboard (xclip)"
else
    echo "ℹ️  Could not copy to clipboard automatically. Please copy it manually."
fi

echo ""
echo "✅ Done. Use this JWT_SECRET in your .env or service configuration."