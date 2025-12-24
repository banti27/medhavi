#!/bin/bash

echo "=== Gradle Build Diagnostic ==="
echo ""

echo "1. Stopping any running Gradle daemons..."
./gradlew --stop
sleep 2

echo ""
echo "2. Testing basic Gradle functionality..."
./gradlew --version

echo ""
echo "3. Listing available tasks..."
./gradlew tasks --no-daemon | grep "Build tasks" -A 10

echo ""
echo "4. Running clean..."
./gradlew clean --no-daemon

echo ""
echo "5. Running compileJava..."
./gradlew compileJava --no-daemon

echo ""
echo "6. Running test..."
./gradlew test --no-daemon

echo ""
echo "7. Running jar..."
./gradlew jar --no-daemon

echo ""
echo "8. Checking build output..."
ls -lh build/libs/ 2>/dev/null || echo "No libs directory yet"

echo ""
echo "=== Diagnostic Complete ==="
