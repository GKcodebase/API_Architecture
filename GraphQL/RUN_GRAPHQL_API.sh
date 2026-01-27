#!/bin/bash

# AquaWorld GraphQL API - Quick Start Script
# This script builds and runs the GraphQL API application

set -e

echo "=================================="
echo "🐠 AquaWorld GraphQL API 🐠"
echo "=================================="
echo ""

PROJECT_DIR="/Users/gokulg.k/Documents/GitHub/API_Architecture/GraphQL/aquaworld-graphql-api"

echo "📁 Navigating to project directory..."
cd "$PROJECT_DIR"

echo "🔨 Building the project..."
mvn clean install

echo ""
echo "🚀 Starting the application..."
mvn spring-boot:run

echo ""
echo "✅ Application started!"
echo "📊 GraphQL Endpoint: http://localhost:8080/aquaworld/graphql"
echo "🎮 GraphiQL Playground: http://localhost:8080/aquaworld/graphiql"
echo ""
echo "Sample credentials:"
echo "  Username: john"
echo "  Password: john@123"
echo ""
