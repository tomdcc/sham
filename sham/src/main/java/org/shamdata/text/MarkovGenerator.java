package org.shamdata.text;

import org.shamdata.ShamGenerator;
import org.shamdata.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * A Markov text generator. Reads a resource file with text and can then
 * generate random sentences and paragraphs.
 */
public class MarkovGenerator implements ShamGenerator {
    private Random random;
    private String bundleName = "default";

    private static final String sentenceEndChars = ".!?";

    private Map<List<String>,List<String>> pairTable;
    private Map<String,List<String>> singleWordTable;

    /**
     * Initialiser. Should be called before calls to any of the
     * <code>nextXXX()</code> methods.
     */
    public void init() {
        parse(ResourceUtil.readResource(getClass(), bundleName, "txt"));
        if(random == null) {
            random = new Random();
        }
    }

    private void parse(InputStream stream) {
        new Parser().parse(stream);
    }

    /**
     * Returns a sentence guaranteed to not be any loner than the specified
     * number of characters. Will generate a complete sentence - if the
     * generator is not able to generate a complete sentence under the limit
     * after a number of attempts it will throw an {@link IllegalArgumentException}
     * and give up.
     *
     * @param maxChars the maximum number of characters to return
     * @return a sentence no longer than the specified number of characters
     * @throws IllegalArgumentException if unable to generate a sentence that short after a number of attempts
     */
    public String nextSentence(int maxChars) {
		for(int i = 0; i < 1000; i++) {
			String sentence = nextSentence();
			if(sentence.length() <= maxChars) {
				return sentence;
			}
		}
		throw new IllegalArgumentException("Unable to generate sentence smaller than " + maxChars + "characters. Try setting it higher.");
	}

    /**
     * Returns a randomly generated sentence.
     *
     * @return a random sentence
     */
    public String nextSentence() {
        return nextParagraph(1);
    }

    /**
     * Returns a randomly generated paragraph of text. There will be a random
     * number of sentences in the paragraph - somewhere between 2 and 8.
     *
     * @return a random paragraph of text
     */
    public String nextParagraph() {
        return nextParagraph(Math.min(Math.max(5 + (int) (random.nextGaussian() * 2d), 2), 8));
    }

    /**
     * Returns a random list of paragraphs. The number of paragraphs will be between
     * 1 and 8.
     *
     * @return a random list of paragraphs
     */
    public List<String> nextParagraphs() {
        return nextParagraphs(Math.min(Math.max(4 + (int) (random.nextGaussian() * 3d), 1), 8));
    }

    /**
     * Returns the given number of randomly generated paragraphs.
     *
     * @param num the number of paragraphs to generate
     * @return a list of randomly generated paragraphs, of the requested size
     */
    public List<String> nextParagraphs(int num) {
        List<String> paragraphs = new ArrayList<String>(num);
        for(int i = 0; i < num; i++) {
            paragraphs.add(nextParagraph());
        }
        return paragraphs;
    }

    /**
     * Returns a paragraph of text with the given number of sentences.
     *
     * @param totalSentences the number of sentences to generate
     * @return a paragraph with the requested number of sentences
     */
    public String nextParagraph(int totalSentences) {
        StringBuilder out = new StringBuilder();

        List<String> lastWords = new ArrayList<String>();
        lastWords.add(null);
        lastWords.add(null);
        int numSentences = 0;
        boolean inSentence = false;
        boolean inQuote = false;
        while(numSentences < totalSentences) {
            List<String> words = pairTable.get(lastWords);
            if(words == null) {
                // no hit for the digram, try just the last word
                words = singleWordTable.get(lastWords.get(1));
            }
            if(words == null) {
                // hit end of paragraph pair, nothing directly following. start again
                words = pairTable.get(Arrays.<String>asList(null, null));
            }
            String nextWord = words.get(random.nextInt(words.size()));
            if(nextWord.length() == 1 && sentenceEndChars.indexOf(nextWord.charAt(0)) != -1) {
                out.append(nextWord);
                if(inQuote) {
                    out.append('"');
                }
                numSentences++;
                inSentence = false;
                inQuote = false;
                lastWords.remove(0);
                lastWords.add(null); // look up
            } else {
                if(!inSentence) {
                    // start a new sentence
                    if(out.length() > 0) {
                        out.append(" ");
                    }
                    inSentence = true;
                } else {
                    // need a word separator
                    out.append(" ");
                }
                out.append(nextWord);
                if(nextWord.indexOf('"') != -1) {
                    inQuote = true;
                }
            }
            lastWords.remove(0);
            lastWords.add(nextWord);

        }
        return out.toString();
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    private class Parser {

        String w1, w2;

        void parse(InputStream stream) {
            MarkovGenerator.this.singleWordTable = new HashMap<String, List<String>>();
            MarkovGenerator.this.pairTable = new HashMap<List<String>, List<String>>();

            for(List<String> paragraph : readParagraphs(stream)) {
                w1 = null;
                w2 = null;
                for(String line : paragraph) {
                    for(String word : line.split(" ")) {
                        if(word.isEmpty()) {
                            continue;
                        }
                        if(word.endsWith("\"")) {
                            // strip off ending quote, mucks up end of sentence detection
                            word = word.substring(0, word.length() - 1);
                        }
                        int lastCharPlace = word.length() - 1;
                        char lastChar = word.charAt(lastCharPlace);
                        if(sentenceEndChars.indexOf(lastChar) != -1) {
                            // treat the punctuation as its own word, reset context
                            String actualWord = word.substring(0, lastCharPlace);
                            addWord(actualWord);
                            w1 = w2;
                            w2 = actualWord;
                            String end = String.valueOf(lastChar);
                            addWord(end);
                            w1 = null; // set this to null rather than old w2 to reset context at sentence end
                            w2 = end;
                        } else {
                            addWord(word);
                            w1 = w2;
                            w2 = word;
                        }
                    }
                }
            }
        }

        private void addWord(String word) {
            addWordToSingleWordTable(word);
            addWordToPairTable(word);
        }

        private void addWordToSingleWordTable(String word) {
            List<String> value = MarkovGenerator.this.singleWordTable.get(w2);
            if(value == null) {
                value = new ArrayList<String>();
                MarkovGenerator.this.singleWordTable.put(w2, value);
            }
            value.add(word);
        }

        private void addWordToPairTable(String word) {
            List<String> key = Arrays.asList(w1, w2);
            List<String> value = MarkovGenerator.this.pairTable.get(key);
            if(value == null) {
                value = new ArrayList<String>();
                MarkovGenerator.this.pairTable.put(key, value);
            }
            value.add(word);
        }
        private List<List<String>> readParagraphs(InputStream stream) {
            List<List<String>> paragraphs = new ArrayList<List<String>>();
            List<String> currentParagraph = null;
            boolean inParagraph = false;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stream));
                for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if(line.startsWith("#")) {
                        continue;
                    }
                    line = line.replaceAll("\n", "");
                    line = line.trim();
                    if(line.isEmpty()) {
                        inParagraph = false;
                        continue;
                    }
                    if(!inParagraph) {
                        currentParagraph = new ArrayList<String>();
                        paragraphs.add(currentParagraph);
                        inParagraph = true;
                    }
                    currentParagraph.add(line);
                }
                return paragraphs;
            } catch(IOException e) {
                throw new IllegalArgumentException(e);
            } finally {
                try { if(reader != null) reader.close(); } catch(IOException e) { /* swallow */ }
            }
        }
    }

    /**
     * Set the bundle name to use. By default, Sham will look for a text bundle
     * named <code>"default"</code>. Set this to make a generator use a different
     * resource for its text. Should be called before {@link #init()}.
     *
     * @param bundleName the bundle name to use
     */
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
}
