package org.shamdata.text;

import org.shamdata.ShamGenerator;
import org.shamdata.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MarkovGenerator implements ShamGenerator {
    private Random random;
    private String bundleName = "default";

    private static final String sentenceEndChars = ".!?";

    private Map<List<String>,List<String>> table;

    public void init() {
        parse(ResourceUtil.readResource(getClass(), bundleName, "txt"));
        if(random == null) {
            random = new Random();
        }
    }

    private void parse(InputStream stream) {
        table = new Parser().parse(stream);
    }

    public String nextSentence(int maxChars) {
		for(int i = 0; i < 1000; i++) {
			String sentence = nextSentence();
			if(sentence.length() <= maxChars) {
				return sentence;
			}
		}
		throw new IllegalArgumentException("Unable to generate sentence smaller than " + maxChars + "characters. Try setting it higher.");
	}

    public String nextSentence() {
        return nextParagraph(1);
    }

    public String nextParagraph() {
        return nextParagraph(Math.min(Math.max(5 + (int) (random.nextGaussian() * 2d), 2), 8));
    }

    public List<String> nextParagraphs() {
        return nextParagraphs(Math.min(Math.max(4 + (int) (random.nextGaussian() * 3d), 1), 8));
    }

    public List<String> nextParagraphs(int num) {
        List<String> paragraphs = new ArrayList<String>(num);
        for(int i = 0; i < num; i++) {
            paragraphs.add(nextParagraph());
        }
        return paragraphs;
    }

    public String nextParagraph(int totalSentences) {
        StringBuilder out = new StringBuilder();

        List<String> lastWords = new ArrayList<String>();
        lastWords.add(null);
        lastWords.add(null);
        int numSentences = 0;
        boolean inSentence = false;
        while(numSentences < totalSentences) {
            List<String> words = table.get(lastWords);
            if(words == null) {
                // hit end of paragraph pair, nothing directly following. start again
                words = table.get(Arrays.<String>asList(null, null));
            }
            String nextWord = words.get(random.nextInt(words.size()));

            if(nextWord.length() == 1 && sentenceEndChars.indexOf(nextWord.charAt(0)) != -1) {
                out.append(nextWord);
                numSentences++;
                inSentence = false;
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
            }
            lastWords.remove(0);
            lastWords.add(nextWord);

        }
        return out.toString();
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    private static class Parser {
        private Map<List<String>,List<String>> table;

        String w1, w2;

        Map<List<String>, List<String>> parse(InputStream stream) {
            table = new HashMap<List<String>, List<String>>();

            for(List<String> paragraph : readParagraphs(stream)) {
                w1 = null;
                w2 = null;
                for(String line : paragraph) {
                    for(String word : line.split(" ")) {
                        if(word.isEmpty()) {
                            continue;
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
//                        addWord(stopword);
                }
            }

            return table;
        }

        private void addWord(String word) {
            List<String> key = Arrays.asList(w1, w2);
            List<String> value = table.get(key);
            if(value == null) {
                value = new ArrayList<String>();
                table.put(key, value);
            }
            value.add(word);
        }
        private List<List<String>> readParagraphs(InputStream stream) {
            List<List<String>> paragraphs = new ArrayList<List<String>>();
            List<String> currentParagraph = null;
            boolean inParagraph = false;
    //        boolean lastLineBlank = true;
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

}
