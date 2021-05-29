package edu.unc.cem.util;

public interface Tokenizer {

    String SINGLE_QUOTED_TEXT = "('(.)*')";

    String DOUBLE_QUOTED_TEXT = "(\"(.)*\")";

    String INLINE_COMMENTS = "!(.)*";

    String[] tokens(String input) throws Exception;

    String delimiter();

}
