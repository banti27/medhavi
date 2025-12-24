package com.medhavi.qa.engine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medhavi.qa.processor.TextProcessor;

/**
 * Immutable Question Answering Engine using DeepLearning4j's Word2Vec for semantic similarity.
 * 
 * This class is thread-safe and immutable. All instances must be created through the Builder pattern.
 * Once constructed, the state cannot be modified.
 */
public final class QuestionAnsweringEngine {

    private static final Logger log = LoggerFactory.getLogger(QuestionAnsweringEngine.class);

    /**
     * Word2Vec model for semantic similarity (immutable)
     */
    private final Word2Vec word2Vec;

    /**
     * List of sentences extracted from the document (immutable)
     */
    private final List<String> sentences;

    /**
     * Text processor for document analysis (immutable)
     */
    private final TextProcessor textProcessor;

    /**
     * Cache for storing sentences and their embeddings (immutable)
     */
    private final Map<String, String> sentenceCache;

    /**
     * How many top candidates to log for debugging.
     */
    private static final int DEBUG_TOP_K = 5;

    /**
     * Minimum score to consider an answer "confident".
     *
     * Note: The engine will still return the best match even below this threshold,
     * but it will be marked as low confidence.
     */
    private static final double CONFIDENT_SCORE_THRESHOLD = 0.10;

    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder with configuration
     */
    private QuestionAnsweringEngine(Builder builder) {
        this.textProcessor = TextProcessor.builder().build();
        
        // Always process the content to initialize sentences
        List<String> processedSentences = textProcessor.splitIntoSentences(builder.content);
        this.sentences = List.copyOf(processedSentences);  // Make immutable copy
        
        // Create immutable sentence cache
        Map<String, String> tempCache = new HashMap<>();
        for (int i = 0; i < sentences.size(); i++) {
            tempCache.put(String.valueOf(i), sentences.get(i));
        }
        this.sentenceCache = Map.copyOf(tempCache);  // Make immutable
        
        log.info("Processed {} sentences from document", sentences.size());

        // Check if model exists and load it, or train a new one
        this.word2Vec = initializeModel(builder.modelPath);
    }

    /**
     * Initializes the Word2Vec model by either loading from disk or training a new one.
     * 
     * @param modelPath Path to the model file
     * @return Initialized Word2Vec model
     */
    private Word2Vec initializeModel(String modelPath) {
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            try {
                Word2Vec model = WordVectorSerializer.readWord2VecModel(modelFile);
                log.info("✅ Word2Vec model loaded from: {}", modelPath);
                log.info("   Vocabulary size: {}", model.getVocab().numWords());
                return model;
            } catch (Exception e) {
                log.error("Failed to load model, training new one", e);
                return trainNewModel(modelPath);
            }
        } else {
            log.info("No saved model found. Training new Word2Vec model...");
            return trainNewModel(modelPath);
        }
    }

    /**
     * Trains a new Word2Vec model on the processed sentences.
     * 
     * @param modelPath Path to save the trained model
     * @return Trained Word2Vec model
     */
    private Word2Vec trainNewModel(String modelPath) {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        CollectionSentenceIterator iterator = new CollectionSentenceIterator(sentences);

        log.info("Training Word2Vec model...");
        Word2Vec model = new Word2Vec.Builder()
                .minWordFrequency(1) // Include words appearing at least once
                .iterations(3) // Train for 3 epochs
                .layerSize(100) // 100-dimensional word vectors
                .seed(42) // Random seed for reproducibility
                .windowSize(5) // Context window of 5 words
                .iterate(iterator) // Feed sentences to model
                .tokenizerFactory(tokenizerFactory)
                .build();

        model.fit();

        log.info("✅ Word2Vec model trained successfully");
        log.info("   Vocabulary size: {}", model.getVocab().numWords());

        // Save the model
        saveModel(model, modelPath);

        return model;
    }

    /**
     * Answers a question based on the processed document.
     * 
     * @param question The question to answer
     * @return The answer (most relevant sentence from the document)
     */
    public String answerQuestion(String question) {
        if (sentences == null || sentences.isEmpty()) {
            return "No document has been processed yet.";
        }

        if (question == null || question.trim().isEmpty()) {
            log.warn("Empty question received");
            return "Please ask a valid question.";
        }

        Candidate best = findMostRelevantSentence(question);
        if (best == null || best.sentence == null || best.sentence.isBlank()) {
            return "I couldn't find a relevant answer in the document. Please try rephrasing your question.";
        }

        // Always return the best sentence, but mark low-confidence matches.
        if (best.score >= CONFIDENT_SCORE_THRESHOLD) {
            return best.sentence;
        }

        return String.format("(low confidence, score=%.4f) %s", best.score, best.sentence);
    }

    /**
     * Save the trained Word2Vec model to a file.
     *
     * @param model     The Word2Vec model to save
     * @param modelPath The file path to save the model
     * @return True if the model was saved successfully, false otherwise
     */
    private boolean saveModel(Word2Vec model, String modelPath) {
        try {
            File modelFile = new File(modelPath);
            // Create parent directories if they don't exist
            modelFile.getParentFile().mkdirs();

            WordVectorSerializer.writeWord2VecModel(model, modelFile);
            log.info("💾 Word2Vec model saved to: {}", modelPath);
            return true;
        } catch (Exception e) {
            log.error("Failed to save Word2Vec model to: {}", modelPath, e);
            return false;
        }
    }

    /**
     * Finds the most relevant sentence to the question using multiple strategies.
     * 
     * @param question The question to match
     * @return The most relevant sentence
     */
    private Candidate findMostRelevantSentence(String question) {
        double maxScore = -1.0;
        String bestSentence = null;

        // Extract keywords from question
        List<String> questionKeywords = textProcessor.extractKeywords(question.toLowerCase());

        Candidate[] top = new Candidate[DEBUG_TOP_K];

        for (String sentence : sentences) {
            double score = calculateSimilarityScore(question, sentence, questionKeywords);

            if (score > maxScore) {
                maxScore = score;
                bestSentence = sentence;
            }

            // Maintain a tiny top-K list for debug logging.
            maybeInsertTop(top, new Candidate(sentence, score));
        }

        log.info("Best match score: {}", maxScore);
        if (log.isDebugEnabled()) {
            log.debug("Top {} candidates:", DEBUG_TOP_K);
            for (int i = 0; i < top.length; i++) {
                Candidate c = top[i];
                if (c == null) {
                    continue;
                }
                log.debug("  #{} score={} sentence={}", i + 1, String.format("%.6f", c.score), abbreviate(c.sentence, 220));
            }
        }

        return new Candidate(bestSentence, maxScore);
    }

    private static void maybeInsertTop(Candidate[] top, Candidate candidate) {
        if (candidate == null || candidate.sentence == null) {
            return;
        }

        for (int i = 0; i < top.length; i++) {
            if (top[i] == null || candidate.score > top[i].score) {
                // shift down
                for (int j = top.length - 1; j > i; j--) {
                    top[j] = top[j - 1];
                }
                top[i] = candidate;
                return;
            }
        }
    }

    private static String abbreviate(String s, int maxLen) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim().replaceAll("\\s+", " ");
        if (trimmed.length() <= maxLen) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(0, maxLen - 3)) + "...";
    }

    private static final class Candidate {
        private final String sentence;
        private final double score;

        private Candidate(String sentence, double score) {
            this.sentence = sentence;
            this.score = score;
        }
    }

    /**
     * Calculates similarity score between question and sentence.
     * Combines Word2Vec semantic similarity with keyword matching.
     * 
     * @param question         The question
     * @param sentence         The sentence to compare
     * @param questionKeywords Keywords extracted from the question
     * @return Similarity score
     */
    private double calculateSimilarityScore(String question, String sentence,
            List<String> questionKeywords) {
        double semanticScore = calculateSemanticSimilarity(question, sentence);
        double keywordScore = calculateKeywordOverlap(questionKeywords, sentence);

        // Weighted combination of scores
        return (0.6 * semanticScore) + (0.4 * keywordScore);
    }

    /**
     * Calculates semantic similarity using Word2Vec.
     * 
     * @param question The question
     * @param sentence The sentence
     * @return Semantic similarity score (0-1)
     */
    private double calculateSemanticSimilarity(String question, String sentence) {
        try {
            // Tokenize and get words that exist in vocabulary
            String[] questionWords = question.toLowerCase().split("\\W+");
            String[] sentenceWords = sentence.toLowerCase().split("\\W+");

            double totalSimilarity = 0.0;
            int comparisons = 0;

            for (String qWord : questionWords) {
                if (qWord.length() < 2 || !word2Vec.hasWord(qWord))
                    continue;

                for (String sWord : sentenceWords) {
                    if (sWord.length() < 2 || !word2Vec.hasWord(sWord))
                        continue;

                    double similarity = word2Vec.similarity(qWord, sWord);
                    if (!Double.isNaN(similarity)) {
                        totalSimilarity += similarity;
                        comparisons++;
                    }
                }
            }

            return comparisons > 0 ? totalSimilarity / comparisons : 0.0;
        } catch (Exception e) {
            log.error("Error calculating semantic similarity", e);
            return 0.0;
        }
    }

    /**
     * Calculates keyword overlap score.
     * 
     * @param questionKeywords Keywords from the question
     * @param sentence         The sentence to check
     * @return Keyword overlap score (0-1)
     */
    private double calculateKeywordOverlap(List<String> questionKeywords, String sentence) {
        if (questionKeywords.isEmpty())
            return 0.0;

        String lowerSentence = sentence.toLowerCase();
        long matchCount = questionKeywords.stream()
                .filter(lowerSentence::contains)
                .count();

        return (double) matchCount / questionKeywords.size();
    }

    /**
     * Gets statistics about the processed document.
     * 
     * @return Statistics string
     */
    public String getDocumentStats() {
        if (sentences == null) {
            return "No document processed.";
        }

        return String.format(
                "Document Statistics:\n" +
                        "  - Total sentences: %d\n" +
                        "  - Vocabulary size: %d\n" +
                        "  - Model trained: %s",
                sentences.size(),
                word2Vec != null ? word2Vec.getVocab().numWords() : 0,
                word2Vec != null ? "Yes" : "No");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private String modelPath = "cache/trained/text/model.bin";

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder modelPath(String modelPath) {
            this.modelPath = modelPath;
            return this;
        }

        public QuestionAnsweringEngine build() {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be null or empty");
            }
            return new QuestionAnsweringEngine(this);
        }
    }
}
