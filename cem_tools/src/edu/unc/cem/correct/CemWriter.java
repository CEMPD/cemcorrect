package edu.unc.cem.correct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.unc.cem.util.Constants;

public class CemWriter {
    private File input;
	private File output;
	private File summary;
	private File log;
	private BufferedWriter writer;
	private BufferedWriter reporter;
	private BufferedWriter logger;
	
	public CemWriter(File input, File output, File log, File sum) {
		this.input = input;
		this.output = output;
		this.summary = sum;
		this.log = log;
	}
	
	public void write(Calculator calc) throws IOException {
		File outdir = output.getParentFile();
		if (!outdir.exists()) outdir.mkdirs();
		
		File logdir = log.getParentFile();
		if (!logdir.exists()) logdir.mkdir();
		
		boolean reportExists = (summary.exists());
		
		BufferedReader reader = new BufferedReader(new FileReader(input));
		writer = new BufferedWriter(new FileWriter(output));
		reporter = new BufferedWriter(new FileWriter(summary, true));
		logger = new BufferedWriter(new FileWriter(log, true));
		
		List<String> processed = null;
		String line = null;
		long count = 1;
		
		try {
			calc.clearTotals();
			
			String wrn = calc.getWarnings();
			if (wrn != null && !wrn.isEmpty()) logger.append(wrn);
			
			while ((line = reader.readLine()) != null) {
				
				//test
				if (Constants.DEBUG && !line.startsWith("3948,\"1\","))
				{
					count++;
					continue;
				}
				
				processed = calc.processLine(line, input);
				String toOut = processed.get(0);
				String toLog = processed.get(1);
				
				writer.write(toOut);
				
				if (toLog != null && !toLog.isEmpty()) {
					logger.append(toLog);
				}
				
				count++;
			}
			
			List<String> totals = calc.getTotals();
			
			if (!reportExists) reporter.write("Source,Month," +
					"NOX Mass Total (pre-substitution) (lb/hr),NOX Mass Total (post-substitution) (lb/hr),NOX Mass Total Difference (lb/hr),Number of Hours Substituted," +
					"SO2 Mass Total (pre-substitution) (lb/hr),SO2 Mass Total (post-substitution) (lb/hr),SO2 Mass Total Difference (lb/hr),Number of Hours Substituted," +
					"Heat Input Total (pre-substitution) (mmBtu),Heat Input Total (post-substitution) (mmBtu),Heat Input Total Difference (mmBtu),Number of Hours Substituted\n");
			
			for (String total : totals) reporter.append(total);
		} catch (Exception e) {
			System.out.println("Error reading line " + count + "(" + input.getName() + "): " + line);
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	public void close() throws IOException {
		writer.close();
		reporter.close();
		logger.close();
	}
	
	public void finalize() {
		writer = null;
		
		try {
			super.finalize();
		} catch (Throwable e) {
			//no-op
		}
	}
}
