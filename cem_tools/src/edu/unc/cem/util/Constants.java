package edu.unc.cem.util;

import java.io.File;

public class Constants {
	public static final String[] VALID_STATES = { "AL", "AK", "AS", "AZ", "AR",
			"CA", "CO", "CT", "DE", "DC", "FM", "FL", "GA", "GU", "HI", "ID",
			"IL", "IN", "IA", "KS", "KY", "LA", "ME", "MH", "MD", "MA", "MI",
			"MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC",
			"ND", "MP", "OH", "OK", "OR", "PW", "PA", "PR", "RI", "SC", "SD",
			"TN", "TX", "UT", "VT", "VI", "VA", "WA", "WV", "WI", "WY" };
	
    public static final String AL="alabama.files"; //ALABAMA                         
    public static final String AK="alaska.files"; //ALASKA                          
    public static final String AS="american.samoa.files"; //AMERICAN SAMOA                  
    public static final String AZ="arizona.files"; //ARIZONA                         
    public static final String AR="arkansas.files"; //ARKANSAS                        
    public static final String CA="california.files"; //CALIFORNIA                      
    public static final String CO="colorado.files"; //COLORADO                        
    public static final String CT="connecticut.files"; //CONNECTICUT                     
    public static final String DE="delaware.files"; //DELAWARE                        
    public static final String DC="district.of.columbia.files"; //DISTRICT OF COLUMBIA            
    public static final String FM="federated.states.of.micronesia.files"; //FEDERATED STATES OF MICRONESIA  
    public static final String FL="florida.files"; //FLORIDA                         
    public static final String GA="georgia.files"; //GEORGIA                         
    public static final String GU="guam.files"; //GUAM                            
    public static final String HI="hawaii.files"; //HAWAII                          
    public static final String ID="idaho.files"; //IDAHO                           
    public static final String IL="illinois.files"; //ILLINOIS                        
    public static final String IN="indiana.files"; //INDIANA                         
    public static final String IA="iowa.files"; //IOWA                            
    public static final String KS="kansas.files"; //KANSAS                          
    public static final String KY="kentucky.files"; //KENTUCKY                        
    public static final String LA="louisiana.files"; //LOUISIANA                       
    public static final String ME="maine.files"; //MAINE                           
    public static final String MH="marshall.islands.files"; //MARSHALL ISLANDS                
    public static final String MD="maryland.files"; //MARYLAND                        
    public static final String MA="massachusetts.files"; //MASSACHUSETTS                   
    public static final String MI="michigan.files"; //MICHIGAN                        
    public static final String MN="minnesota.files"; //MINNESOTA                       
    public static final String MS="mississippi.files"; //MISSISSIPPI                     
    public static final String MO="missouri.files"; //MISSOURI                        
    public static final String MT="montana.files"; //MONTANA                         
    public static final String NE="nebraska.files"; //NEBRASKA                        
    public static final String NV="nevada.files"; //NEVADA                          
    public static final String NH="new.hampshire.files"; //NEW HAMPSHIRE                   
    public static final String NJ="new.jersey.files"; //NEW JERSEY                      
    public static final String NM="new.mexico.files"; //NEW MEXICO                      
    public static final String NY="new.york.files"; //NEW YORK                        
    public static final String NC="north.carolina.files"; //NORTH CAROLINA                  
    public static final String ND="north.dakota.files"; //NORTH DAKOTA                    
    public static final String MP="northern.mariana.islands.files"; //NORTHERN MARIANA ISLANDS        
    public static final String OH="ohio.files"; //OHIO                            
    public static final String OK="oklahoma.files"; //OKLAHOMA                        
    public static final String OR="oregon.files"; //OREGON                          
    public static final String PW="palau.files"; //PALAU                           
    public static final String PA="pennsylvania.files"; //PENNSYLVANIA                    
    public static final String PR="puerto.rico.files"; //PUERTO RICO                     
    public static final String RI="rhode.island.files"; //RHODE ISLAND                    
    public static final String SC="south.carolina.files"; //SOUTH CAROLINA                  
    public static final String SD="south.dakota.files"; //SOUTH DAKOTA                    
    public static final String TN="tennessee.files"; //TENNESSEE                       
    public static final String TX="texas.files"; //TEXAS                           
    public static final String UT="utah.files"; //UTAH                            
    public static final String VT="vermont.files"; //VERMONT                         
    public static final String VI="virgin.islands.files"; //VIRGIN ISLANDS                  
    public static final String VA="virginia.files"; //VIRGINIA                        
    public static final String WA="washington.files"; //WASHINGTON                      
    public static final String WV="west.virginia.files"; //WEST VIRGINIA                   
    public static final String WI="wisconsin.files"; //WISCONSIN                       
    public static final String WY="wyoming.files"; //WYOMING
    
    public static final String DEFAULT_CONFIG_HOME=System.getProperty("user.home") + File.separator + ".cem";
    public static final String CONFIG_HOME="config.home";
    public static final String CONFIG_FILE="config.file";
    public static final String INPUT_DIR="input.dir";
    public static final String OUTPUT_DIR="output.dir";
    public static final String LOG_DIR="logs";
    public static final String LOG_FILE="log.txt";
    public static final String REPORT_PREF="report.prefix";
    public static final String REPORT_SUFF="report.suffix";
    public static final String STATE_LIST="states.list";
    public static final String EVENTS="events";
    
    public static final String COMMA="%%CEM_COMMA%%";
    
    public static int YEARBASE = 1900;
    public static int JAN = 0;
    public static int MAY = 4;
    public static int OCT = 9;
    public static int DEC = 11;
    
    public static boolean DEBUG = false; 

}
