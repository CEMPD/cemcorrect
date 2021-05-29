package edu.unc.cem.command;

import edu.unc.cem.util.CommaDelimitedTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandLineHandler {

    private Map<String, ArrayList<String>> commands = null;

    private final String HELPTEXT = "\t-config.home <configuration file home directory> \n"
            + "\t-config.file <filename> \n"
            + "\t-input.dir <input data file directory> \n"
            + "\t-output.dir <output data file directory> \n"
            + "\t-states.list <comma separated list of states, such as AL,AK,AZ> \n"
            + "\t-copyright \n";


    public CommandLineHandler(String[] args) {
        try {
            commands = CommandLineParser.parseCommands(args, new CommaDelimitedTokenizer());
        } catch (Exception e) {
            System.out.println("Command line argument format error.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public List<String> getOption(String opt) {
        return commands.get(opt);
    }

    public String getUsage() {
        return "java -classpath ./;./lib edu.unc.cem.correct [options] \n" + "\tOptions:\n" + HELPTEXT;
    }

}
