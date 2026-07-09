#!/bin/bash
# Fast Android APK Build Script for Legal Recall App
set -e

echo "=========================================="
echo "Initializing Legal Recall APK compilation..."
echo "=========================================="

# Check if gradle is installed and run assembly build
if command -v gradle >/dev/null 2>&1; then
    gradle :app:assembleDebug
else
    # Fallback to local gradlew if present
    if [ -f "./gradlew" ]; then
        chmod +x ./gradlew
        ./gradlew :app:assembleDebug
    else
        echo "Error: Gradle is not pre-installed or configured in this environment!"
        exit 1
    fi
fi

echo "=========================================="
echo "Build Successful!"
echo "Your offline APK is generated at: app/build/outputs/apk/debug/app-debug.apk"
echo "=========================================="
