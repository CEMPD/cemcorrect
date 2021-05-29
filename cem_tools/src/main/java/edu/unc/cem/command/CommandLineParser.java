package edu.unc.cem.command;

import edu.unc.cem.util.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandLineParser {
    private static final Map<String, ArrayList<String>> options = new HashMap<String, ArrayList<String>>();

    public static Map<String, ArrayList<String>> parseCommands(String[] args, Tokenizer tokenizer) throws Exception {
        ArrayList<String> thisCommand = new ArrayList<String>();
        String currentOption = "";

        //Check to see if this is the last item in the command or or a '\'
        //Each option item will be the first in the thisArgItem list
        for (int i = 0; i < args.length; i++) {
            String thisArgItem = args[i];

            if (args[i].charAt(0) == '-') {
                currentOption = args[i].substring(1);
            }

            if (args[i].charAt(0) != '-') {
                String tempStr = System.getenv(args[i]);

                if (tempStr != null) thisArgItem = tempStr;

                // '\' is the command line continuation switch
                if (!args[i].equals("\\")) thisCommand.addAll(Arrays.asList(tokenizer.tokens(thisArgItem)));
            }

            //if the next item starts with a "-" or we have reached the end of the arguments
            if ((i < args.length - 1 && args[i + 1].charAt(0) == '-')
                    || i == args.length - 1) {
                options.put(currentOption, thisCommand);
                thisCommand = new ArrayList<String>();
            }
        }

        return options;
    }

}
