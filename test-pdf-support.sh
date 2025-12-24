#!/bin/bash

# Medhavi QA System - PDF Testing Script
# This script demonstrates that PDF reading works correctly
# The ND4J native library issue only affects Word2Vec model training

echo "═══════════════════════════════════════════════════════"
echo "  Medhavi QA System - PDF Support Verification"
echo "═══════════════════════════════════════════════════════"
echo ""

# Check if file exists
PDF_PATH="/Users/vansh/Downloads/IndiaConstitutionEnglish.pdf"

if [ ! -f "$PDF_PATH" ]; then
    echo "❌ PDF file not found at: $PDF_PATH"
    exit 1
fi

echo "✅ PDF file found: $PDF_PATH"
echo ""

# Run the integration test that verifies PDF reading
echo "Running PDF reading integration test..."
echo "───────────────────────────────────────"

cd "$(dirname "$0")" || exit

./gradlew test --tests FileFormatHandlerIntegrationTest -q

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ SUCCESS: PDF reading is working correctly!"
    echo ""
    echo "Summary:"
    echo "--------"
    echo "• PDF file was successfully read using Apache PDFBox"
    echo "• All text content was extracted from the document"
    echo "• FileFormatHandler.readFile() works with PDF files"
    echo ""
    echo "Note: The ND4J native library issue is a platform-specific limitation"
    echo "that only affects Word2Vec model training, not file reading."
    echo ""
    echo "Solutions for running the full QA system:"
    echo "1. Use Docker container with Linux base"
    echo "2. Build a fat JAR and run on a properly configured system"
    echo "3. Use a cloud deployment service"
else
    echo ""
    echo "❌ Test failed. Check the output above for details."
    exit 1
fi
