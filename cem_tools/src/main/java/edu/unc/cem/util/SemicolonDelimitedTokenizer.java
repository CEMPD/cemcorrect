package edu.unc.cem.util;

public class SemicolonDelimitedTokenizer implements Tokenizer {
    private final DelimitedTokenizer delegate;

    private final String pattern;

    private int numOfDelimiter;

    private boolean initialized = false;

    public SemicolonDelimitedTokenizer() {
        pattern = "[^;]+";
        delegate = new DelimitedTokenizer(pattern);

    }

    public String[] tokens(String input) throws Exception {
        input = padding(input);
        String[] tokens = null;

        try {
            tokens = delegate.doTokenize(input);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        if (!initialized) {
            numOfDelimiter = tokens.length;
            initialized = true;
            return tokens;
        }

        if (initialized && tokens.length != numOfDelimiter && tokens.length < 2) {
            throw new Exception("Could not find " + --numOfDelimiter + " of ';' delimiters on the line.");
        }

        return tokens;
    }

    private String padding(String input) {
        input = input.trim();
        if (input.startsWith(";"))
            input = ";" + input;
        if (input.endsWith(";"))
            input = input + ";";
        input = input.replaceAll(";;", "; ;").replaceAll(";;", "; ;");

        return input;
    }

    public String delimiter() {
        return ";";
    }
}
