package edu.unc.cem.correct;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unc.cem.command.CommandLineHandler;
import edu.unc.cem.util.*;

public class CemCorrect {
    private String configHome;
    private String configFile;
    private String inputHome;
    private String outputHome;
    private String rptPrefix;
    private String rptSuffix;
    private List<String> states;
    private Map<String, List<String>> inputFiles = new HashMap<String, List<String>>();
    private String events;
    private Map<String,CemEvent> eventMap;
    private PropertiesManager propertiesManager;
    private List<String> validStates = Arrays.asList(Constants.VALID_STATES);
    private Tokenizer tokenizer;

    public CemCorrect(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void loadProperties(CommandLineHandler command) throws Exception {
        if (command != null) {
            List<String> temp = command.getOption(Constants.CONFIG_HOME);
            if (temp != null && temp.get(0) != null) configHome = temp.get(0).trim();

            temp = command.getOption(Constants.CONFIG_FILE);
            if (temp != null && temp.get(0) != null) configFile = temp.get(0).trim();

            temp = command.getOption(Constants.STATE_LIST);
            if (temp != null && temp.get(0) != null) states = temp;

            temp = command.getOption(Constants.INPUT_DIR);
            if (temp != null && temp.get(0) != null) inputHome = temp.get(0).trim();

            temp = command.getOption(Constants.OUTPUT_DIR);
            if (temp != null && temp.get(0) != null) outputHome = temp.get(0).trim();

            temp = command.getOption(Constants.REPORT_PREF);
            if (temp != null && temp.get(0) != null) rptPrefix = temp.get(0).trim();

            temp = command.getOption(Constants.REPORT_SUFF);
            if (temp != null && temp.get(0) != null) rptSuffix = temp.get(0).trim();
        }

        if (configFile == null || configFile.isEmpty()) configFile = "config/config.properties";

        propertiesManager = new PropertiesManager((configHome == null || configHome.isEmpty()) ? configFile : configHome + File.separator + configFile);

        if (states == null || states.size() == 0) {
            String temp = propertiesManager.getProperty(Constants.STATE_LIST);
            if (temp == null || temp.trim().length() == 0)
                temp = "ALL";
            states = Arrays.asList(tokenizer.tokens(temp));
        }

        if (inputHome == null || inputHome.isEmpty()) {
            inputHome = propertiesManager.getProperty(Constants.INPUT_DIR);
        }

        if (outputHome == null || outputHome.isEmpty()) {
            outputHome = propertiesManager.getProperty(Constants.OUTPUT_DIR);
        }

        if (rptPrefix == null || rptPrefix.isEmpty()) {
            rptPrefix = propertiesManager.getProperty(Constants.REPORT_PREF);
        }

        if (rptSuffix == null || rptSuffix.isEmpty()) {
            rptSuffix = propertiesManager.getProperty(Constants.REPORT_SUFF);
        }

        if (rptPrefix == null) rptPrefix = "";
        if (rptSuffix == null) rptSuffix = "";
        if (!rptPrefix.endsWith("_")) rptPrefix += "_";
        if (!rptSuffix.startsWith("_")) rptSuffix = "_" + rptSuffix;

        registerInputFiles(states, command);
        registerInputFiles(states, propertiesManager);

        if (events == null) {
            events = propertiesManager.getProperty(Constants.EVENTS);
        }
        //System.out.println( "Events: " + events);
        eventMap = propertiesManager.parseEvent(events);
        propertiesManager.printEventMap(eventMap);

        propertiesManager = null; //No need to be kept in memory
        command = null; //No need to be kept in memory
    }

    private void processStateFiles(String state) throws Exception {
        List<String> files = inputFiles.get(state);
        CemReader reader = null;
        CemWriter writer = null;
        Calculator calculator = new Calculator(tokenizer);
        calculator.setEventMap(eventMap);

        for (int index = 0; index < files.size(); index++) {
            File inputfile = new File(inputHome + File.separator + files.get(index));
            reader = new CemReader(inputfile);
            reader.open();
            calculator.addRecords(reader.read(), inputfile);
            reader.close();

            reader.finalize();
            System.out.println(inputfile.getName());
        }


        calculator.calculateMeans();

        String fs = File.separator;
        File log = new File(outputHome + fs + Constants.LOG_DIR + fs + state + "_" + Constants.LOG_FILE);
        File report = new File(outputHome + fs + Constants.LOG_DIR + fs + rptPrefix + state + rptSuffix + ".csv");

        if (log.exists()) log.delete();
        if (report.exists()) report.delete();

        System.out.println("Writing:");

        for (int index = 0; index < files.size(); index++) {
            File input = new File(inputHome + fs + files.get(index));
            File output = new File(outputHome + fs + files.get(index));
            writer = new CemWriter(input, output, log, report);
            writer.write(calculator);
            writer.close();

            writer.finalize();
            System.out.println(output.getName());
        }

    }

    public void process() {
        if (inputHome == null || inputHome.trim().isEmpty()) {
            System.out.println("Input directory is not specified.");
            System.exit(1);
        }

        for (String st : states) {
            try {
                if (st != null && !st.isEmpty()) {
                    processStateFiles(st);
                }
            } catch (Exception e) {
                System.out.println("Error reading files for state: " + st + ".");
                e.printStackTrace();
            }
        }
    }

    public void generateReports() {
        System.out.println("Reports generated.");
    }

    private void registerInputFiles(List<String> sts, CommandLineHandler command) throws Exception {
        Class<Constants> cemConstants = Constants.class;
        Constants contnts = new Constants();

        for (String state : sts) {
            state = state.toUpperCase();

            if (validStates.contains(state)) {
                System.out.println(state);
                String stateFilesId = (String) cemConstants.getField(state).get(contnts);
                System.out.println(stateFilesId);
                inputFiles.put(state, command.getOption(stateFilesId));
            }
        }
    }

    private void registerInputFiles(List<String> sts, PropertiesManager manager) throws Exception {
        if (sts == null || sts.size() == 0 || manager == null) return;

        Class<Constants> cemConstants = Constants.class;
        Constants contnts = new Constants();

        for (String state : sts) {
            state = state.toUpperCase();

            if (validStates.contains(state)) {
                String stateFilesId = (String) cemConstants.getField(state).get(contnts);
                String files = manager.getProperty(stateFilesId);
                if (inputFiles.get(state) == null)
                    inputFiles.put(state, Arrays.asList(tokenizer.tokens(files)));
            }
        }
    }

    public void printProperties() {
        System.out.println("Loaded Properties =====>");
        System.out.println("config.home: " + this.configHome);
        System.out.println("config.file: " + this.configFile);
        System.out.println("input.dir: " + this.inputHome);
        System.out.println("output.dir: " + this.outputHome);
        System.out.println("states.list: " + this.states);

        Set<String> keys = inputFiles.keySet();

        for (String key : keys) {
            System.out.println(key + " files: " + inputFiles.get(key));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("-v") || args[0].equalsIgnoreCase("-version"))) {
            System.out.println("CemCorrect version: " + VersionInfo.getVersion() + ", built on: " + VersionInfo.getDate() + ".");
            System.exit(0);
        }

        System.out.println("Running CemCorrect Program...\n");
        CommandLineHandler command = new CommandLineHandler(args);
        CemCorrect exe = new CemCorrect(new CommaDelimitedTokenizer());
        try {
            System.out.println("Loading config properties...\n");
            exe.loadProperties(command);
        } catch (Exception e) {
            System.out.print("Error fetching properties.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Started processing the input files...\n");
        exe.process();
        exe.generateReports();
    }

}
