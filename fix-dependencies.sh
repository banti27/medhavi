#!/bin/bash

echo "=== Fixing Gradle Dependencies and IDE Sync ==="
echo ""

echo "1. Stopping Gradle daemon..."
./gradlew --stop

echo ""
echo "2. Cleaning build directory..."
rm -rf build .gradle

echo ""
echo "3. Refreshing dependencies..."
./gradlew clean --refresh-dependencies

echo ""
echo "4. Building project..."
./gradlew build --no-daemon

echo ""
echo "5. Checking compilation..."
./gradlew compileJava --no-daemon

echo ""
echo "=== Fix Complete ==="
echo ""
echo "Next steps:"
echo "1. In VS Code, reload the Java project:"
echo "   - Press Cmd+Shift+P"
echo "   - Type 'Java: Clean Java Language Server Workspace'"
echo "   - Select it and reload window"
echo ""
echo "2. Or simply close and reopen VS Code"
