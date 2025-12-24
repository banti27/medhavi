# Quick Start Guide

## Fastest Way to Get Started

1. **Open Terminal** in the project directory

2. **Make the run script executable:**
   ```bash
   chmod +x run.sh
   ```

3. **Run the application:**
   ```bash
   ./run.sh
   ```

4. **When prompted, enter the sample file path:**
   ```
   sample_text.txt
   ```

5. **Try these example questions:**
   - What is artificial intelligence?
   - When was AI founded?
   - What are applications of AI?
   - What is machine learning?
   - What is natural language processing?
   - Tell me about deep learning
   - What are the ethical considerations of AI?

## Manual Build and Run

If you prefer to run manually:

```bash
# Build the project
./gradlew build

# Run the application
java -jar build/libs/text-qa-system-1.0-SNAPSHOT.jar
```

Or with Gradle:
```bash
./gradlew runApp
```

## Testing Your Own Files

Create a text file with any content you want to query, then provide its path when the application asks for it. The file should be plain text (.txt) format.

## Troubleshooting

**Problem:** "Java version too old"
**Solution:** Install Java 21 from https://www.oracle.com/java/technologies/downloads/

**Problem:** Build takes long time
**Solution:** This is normal for first build. DeepLearning4j downloads necessary dependencies.

**Problem:** Out of memory
**Solution:** Run with more memory: `java -Xmx4g -jar build/libs/text-qa-system-1.0-SNAPSHOT.jar`

## System Requirements

- **Java:** 21 or higher
- **Gradle:** 8.5+ (wrapper included, no manual installation needed)
- **RAM:** Minimum 2GB, recommended 4GB
- **Disk Space:** ~500MB for dependencies
