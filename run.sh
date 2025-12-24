#!/bin/bash

# Quick Start Script for Text QA System

echo "╔══════════════════════════════════════════════════════════╗"
echo "║  Text Question Answering System - Quick Start           ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null
then
    echo "❌ Java is not installed. Please install Java 21 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "⚠️  Warning: Java version is less than 21. Java 21 or higher is recommended."
fi

echo "✓ Java found: $(java -version 2>&1 | head -n 1)"
echo "✓ Gradle wrapper found: ./gradlew"
echo ""

# Build the project if build directory doesn't exist
if [ ! -d "build" ]; then
    echo "🔨 Building the project for the first time..."
    ./gradlew build --no-daemon
    echo ""
fi

# Run the application
echo "🚀 Starting the Text QA System..."
echo ""

./gradlew runApp --no-daemon --console=plain
