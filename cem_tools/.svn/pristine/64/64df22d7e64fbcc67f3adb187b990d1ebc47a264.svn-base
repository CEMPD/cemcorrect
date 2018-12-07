package edu.unc.cem.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.lang.Exception;

import edu.unc.cem.correct.Calculator;

/**
 * @author Qun He
 * @version $Revision$ $Date$
 */
public class PropertiesManager {
	
	private String configFile;
	private boolean loaded = false;
	
	public PropertiesManager(String file) {
		this.configFile = file;
	}
	
	private void loadProperties() {
		if (loaded) return;
		
		try {
			if (configFile == null || configFile.trim().isEmpty()) System.out.println("Configuration file is not specified.");
			
			File file = new File(configFile);
			
			if (file.exists()) System.getProperties().load(new FileInputStream(file));
			else System.out.println("Configuration file \"" + configFile + "\" doesn't exist!");
			
			loaded = true;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
		}
	}

	public String getProperty (String key) {
		if (!loaded) loadProperties();
		
		return System.getProperty(key);
	}
	
	public Map<String,CemEvent> parseEvent( String events) throws Exception {
		Map<String,CemEvent> eventMap = null;
		
		if ( events != null && events.trim().length() != 0) {
			int year, month, day;
			Date start, end;
			Tokenizer tokenizer = new SemicolonDelimitedTokenizer();
			Tokenizer tokenizer2 = new CommaDelimitedTokenizer();
			try {
				String [] ts = tokenizer.tokens( events);
				for (String t : ts) {
					//System.out.println( t);
					String [] ts2 = tokenizer2.tokens( t);
					//for (String t2 : ts2) {
					//	System.out.println( "\t" + t2 + "_end");
					//}
					String key = ts2[0].trim() + ts2[1].trim(); // IRIS ID + BOILER ID
					if ( ts2[2] == null) {
						throw new Exception("Invalid start date in event: " + t);
					}
					String startDate = ts2[2].trim(); //YYMMDD
					if (startDate == null || startDate.length() != 6) {
						throw new Exception("Invalid start date in event: " + t);
					}
					try {
						year = Integer.parseInt(startDate.substring(0,2));
					} catch ( Exception e) {
						throw new Exception ("Invalid start year in event: " + t);
					}
					if ( !DateUtil.checkYear(year)) {
						throw new Exception ("Invalid start year in event: " + t);
					}
					if ( year < 30 ) {
						year += 100; // think these are in 21st century
					} else {
						year += 0;
					}
					try {
						month = Integer.parseInt(startDate.substring(2,4));
					} catch ( Exception e) {
						throw new Exception ("Invalid start month in event: " + t);
					}
					if ( !DateUtil.checkMonth(month)) {
						throw new Exception ("Invalid start month in event: " + t);
					}
					try {
						day = Integer.parseInt(startDate.substring(4));
					} catch ( Exception e) {
						throw new Exception ("Invalid start day in event: " + t);
					}
					if ( !DateUtil.checkDay( month, day)) {
						throw new Exception ("Invalid start day in event: " + t);
					}
					start = new Date(year,month-1,day);
					end = null;
					if ( ts2[3] != null && !ts2[3].trim().equals("")) {
						String endDate = ts2[3].trim(); // null or YYMMDD
						if (endDate.length() != 6) {
							throw new Exception("Invalid end date in event: " + t);
						}
						try {
							year = Integer.parseInt(endDate.substring(0,2));
						} catch ( Exception e) {
							throw new Exception ("Invalid end year in event: " + t);
						}
						if ( !DateUtil.checkYear(year)) {
							throw new Exception ("Invalid end year in event: " + t);
						}
						if ( year < 30 ) {
							year += 100; // think these are in 21st century
						} else {
							year += 0;
						}
						try {
							month = Integer.parseInt(endDate.substring(2,4));
						} catch ( Exception e) {
							throw new Exception ("Invalid end month in event: " + t);
						}
						if ( !DateUtil.checkMonth(month)) {
							throw new Exception ("Invalid end month in event: " + t);
						}
						try {
							day = Integer.parseInt(endDate.substring(4));
						} catch ( Exception e) {
							throw new Exception ("Invalid end day in event: " + t);
						}
						if ( !DateUtil.checkDay( month, day)) {
							throw new Exception ("Invalid end day in event: " + t);
						}
						end = new Date(year,month-1,day);
					}
				    
					if ( eventMap == null) {
						eventMap = new HashMap<String, CemEvent>();
					}
					
					eventMap.put(key, new CemEvent(start,end));					
				}
			} 
		    catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}		
		return eventMap;
	}
	
	public void printEventMap(Map<String,CemEvent> eventMap) {
		if (eventMap == null ) {
			return;
		}
		Set<Entry<String,CemEvent>> eventSet = eventMap.entrySet();
		for (Entry<String,CemEvent> en : eventSet) {
			System.out.println(en.getKey() + ": " 
					+ en.getValue().getStart().toLocaleString() + " "
					+ ((en.getValue().getEnd() == null) ? "" : en.getValue().getEnd().toLocaleString()));
		}
	}
	
	public static void main(String[] args) {
		PropertiesManager pMan = new PropertiesManager("/Users/jizhen/Documents/workspace/cem_tools/cem_tools/config/config.test");
		String events = pMan.getProperty("events");
		//System.out.println(events);	
		Map<String,CemEvent> eventMap = null;
		try {
			eventMap = pMan.parseEvent( events);
			pMan.printEventMap( eventMap);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		Calculator cal = new Calculator(null);
		try {
			cal.setEventMap(eventMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Tokenizer tokenizer = new SemicolonDelimitedTokenizer();
		Tokenizer tokenizer2 = new CommaDelimitedTokenizer();
		try {
			String [] ts = tokenizer.tokens( events);
			for (String t : ts) {
				System.out.println( t);
				String [] ts2 = tokenizer2.tokens( t);
				for (String t2 : ts2) {
					System.out.println( "\t" + t2 + "_end");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}
