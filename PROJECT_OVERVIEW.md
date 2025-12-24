# Text QA System - Project Overview

## What This Project Does

This is a Java 21 application that allows users to:
1. Upload any text file (.txt)
2. Ask natural language questions about the content
3. Receive relevant answers extracted from the text

## Technology Stack

- **Java 21**: Modern Java with latest features
- **Gradle 8.5**: Modern build automation system
- **DeepLearning4j**: Deep learning library for NLP
- **Word2Vec**: Neural embeddings for semantic similarity

## Architecture

```
User Input (Text File)
         ↓
   TextProcessor (Parses & Cleans)
         ↓
   QuestionAnsweringEngine (Word2Vec Training)
         ↓
   [Question] → Semantic Similarity Calculation
         ↓
   [Answer] → Most Relevant Sentence
```

## Key Components

### 1. TextQAApplication.java
- Main entry point
- Handles user interaction
- Manages application flow

### 2. TextProcessor.java
- Reads and processes text files
- Splits text into sentences
- Extracts keywords
- Text cleaning utilities

### 3. QuestionAnsweringEngine.java
- Trains Word2Vec model on document
- Calculates semantic similarity
- Finds most relevant answers
- Combines multiple scoring strategies

## How It Works

1. **Document Processing**: 
   - Reads the text file
   - Splits into sentences
   - Trains Word2Vec model on content

2. **Question Processing**:
   - Extracts keywords from question
   - Calculates semantic similarity using Word2Vec
   - Computes keyword overlap score
   - Combines scores with weights (60% semantic, 40% keyword)

3. **Answer Selection**:
   - Ranks all sentences by relevance
   - Returns the highest scoring sentence
   - Falls back to message if no good match

## Features

✅ Interactive command-line interface
✅ Semantic understanding using Word2Vec
✅ Keyword-based matching
✅ Multi-strategy answer selection
✅ Support for any plain text file
✅ Document statistics
✅ Clean, modular architecture

## Quick Commands

```bash
# Build
./gradlew build

# Run
./run.sh

# Test
./gradlew test

# Run with more memory
java -Xmx4g -jar build/libs/text-qa-system-1.0-SNAPSHOT.jar
```

## Example Usage

```
Enter the path to your text file: sample_text.txt
✓ Text file loaded successfully!
✓ Document processed and ready for questions!

❓ Your question: What is artificial intelligence?
💡 Answer: Artificial intelligence is the simulation of human 
   intelligence processes by machines, especially computer systems.

❓ Your question: When was AI founded?
💡 Answer: The field of artificial intelligence was officially 
   founded in 1956 at a conference at Dartmouth College.
```

## File Structure

```
medhavi/
├── build.gradle                 # Gradle build configuration
├── settings.gradle              # Gradle settings
├── gradlew                     # Gradle wrapper script (Unix)
├── gradle/
│   └── wrapper/                # Gradle wrapper files
├── README.md                    # Full documentation
├── QUICKSTART.md               # Quick start guide
├── run.sh                      # Launch script
├── sample_text.txt             # Sample data for testing
├── .gitignore                  # Git ignore rules
└── src/
    ├── main/
    │   ├── java/com/medhavi/qa/
    │   │   ├── TextQAApplication.java
    │   │   ├── TextProcessor.java
    │   │   └── QuestionAnsweringEngine.java
    │   └── resources/
    │       └── simplelogger.properties
    └── test/
        └── java/com/medhavi/qa/
            └── TextProcessorTest.java
```

## Customization Options

You can modify these parameters in `QuestionAnsweringEngine.java`:

- **Layer Size**: Word2Vec embedding dimensions (default: 100)
- **Window Size**: Context window for training (default: 5)
- **Iterations**: Training iterations (default: 3)
- **Score Weights**: Semantic vs keyword weights (default: 0.6/0.4)

## Performance Considerations

- **First run**: Downloads Maven dependencies (~500MB)
- **Model training**: Takes a few seconds per document
- **Memory usage**: 2-4GB recommended for large documents
- **Best results**: Documents with clear sentence structure

## Future Enhancements

Possible improvements:
- Support for PDF/DOCX files
- Multi-document search
- Context window around answers
- REST API interface
- GUI application
- Pre-trained transformer models
- Answer ranking with confidence scores

## Support

For issues or questions:
1. Check README.md and QUICKSTART.md
2. Verify Java 21 and Maven are installed
3. Try the sample_text.txt first
4. Check logs for error messages

---
Created: December 2025
Java Version: 21
DeepLearning4j Version: 1.0.0-M2.1
