package chatgpt;

public class SentimentAnalyzer {
    public static String analyze(String text) {
        // Basic sentiment logic: replace with more advanced NLP models as needed
        if (text.contains("sad") || text.contains("depressed")) {
            return "sad";
        } else if (text.contains("happy") || text.contains("excited")) {
            return "happy";
        } else {
            return "neutral";
        }
    }
}

