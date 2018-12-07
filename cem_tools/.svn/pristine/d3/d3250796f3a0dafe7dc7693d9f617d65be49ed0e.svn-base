package edu.unc.cem.util;

import java.util.Date;

public class CemEvent {
	
    private Date start = null;
    private Date end = null;
    
    public CemEvent() {
    }
    
    public CemEvent(Date start) {
    	this.start = start;
    }
    
    public CemEvent(Date start, Date end) throws Exception {
    	this.start = start;
    	this.end = end;
    	this.validate();
    }
    
	public void setStart(Date start) throws Exception {
		this.start = start;
		this.validate();
	}
	public Date getStart() {
		return start;
	}
	
	public void setEnd(Date end) throws Exception {
		this.end = end;
		this.validate();
	}
	public Date getEnd() {
		return end;
	}
	
	public String toString()
	{
		return 
		    "Start: " + ((start==null) ? "null" : start.toLocaleString()) + "\n"  +
		    "End: " + ((end == null) ? "null" : end.toLocaleString());
	}
	
	private void validate() throws Exception{
		if (start != null && end != null) {
			if ( start.getYear() != end.getYear()) {
				throw new Exception("Start and end date should be in the same year: " + "\n" + this.toString());
			}
			if (start.equals(end) || start.after(end)) {
				throw new Exception("Start date should be before end date: " + "\n" + this.toString());
			}
		}
	}
	
	public static int getNumPeriodsSO2(CemEvent event) throws Exception{
		if (event == null) 
			return 1;
		int numPeriods = 1;
		Date start = event.getStart();
		Date end = event.getEnd();
		if (start == null) {
			throw new Exception("Event - " + event.toString() + " - start date is null!");
		}
		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
		if (start.equals(jan1)) {
			if ( end == null) {
				numPeriods = 1;
			}
			else {
				numPeriods = 2;
			}
		} else {
			if ( end == null) {
				numPeriods = 2;
			}
			else {
				numPeriods = 3;
			}
		}
		return numPeriods;
	}

	// for testing
	public void testGetNumPeriodsSO2() {
		
		Date [] dates = {
				new Date(107,0,1),
				new Date(107,0,2),
				new Date(107,1,4),
				new Date(107,3,30),
				new Date(107,4,1),
				new Date(107,4,2),
				new Date(107,7,1),
				new Date(107,8,30),
				new Date(107,9,1),
				new Date(107,9,2),
				new Date(107,10,1),
				new Date(107,11,31)
		};

		CemEvent event = null;
		try {
			if ( this.getNumPeriodsSO2(event) != 1) 
				System.out.println("Wrong num SO2 periods for null event");
			
			event = new CemEvent(dates[0]);
			if ( this.getNumPeriodsSO2(event) != 1) 
				System.out.println("Wrong num SO2 periods for event: " + event.toString());
			
			int numDates = dates.length;
			int num = 0;
			
			for ( int i = 1; i < numDates; i++) { // start
				event = new CemEvent(dates[i]);
				if ( this.getNumPeriodsSO2(event) != 2) 
					System.out.println("Wrong num SO2 periods for event: " + event.toString());
			}
			
			for (int j = 1; j < numDates; j++) {
				event = new CemEvent(dates[0],dates[j]);
				if ( this.getNumPeriodsSO2(event) != 2) 
					System.out.println("Wrong num SO2 periods for event: " + event.toString());
			}
			
			for ( int i = 1; i < numDates; i++) { // start
				for (int j = i+1; j < numDates; j++) {
					event = new CemEvent(dates[i],dates[j]);
					if ( this.getNumPeriodsSO2(event) != 3) 
						System.out.println("Wrong num SO2 periods for event: " + event.toString());
				}
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		System.out.println("===End of testing getNumPeriodsSO2!");
	}
	
	public static int getPeriodIndexSO2(CemEvent event, int month, int day) throws Exception{
		if ( event == null) { // only one period
			return 0;
		}
		
		Date start = event.getStart();		
		Date end = event.getEnd();
		if (start == null) {
			throw new Exception("Event - " + event.toString() + " - start date is null!");
		}
		
		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
		if (start.equals(jan1)) {
			if ( end == null) { // 1 period
				return 0;
			}
			else { // 2 periods
				if ( month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
					return 0;
				} 
				return 1;
			}
		} else {
			if ( end == null) { // 2 periods
				if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
					return 0;
				}
				return 1;
			}
			else { // 3 periods
				if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
					return 0;
				} else if (month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())){
					return 2;
				}
				return 1;
			}
		}
	}
	
	// for testing
	public void testGetPeriodIndexSO2() {
		
		Date [] dates = {
				new Date(107,0,1),
				new Date(107,0,2),
				new Date(107,1,4),
				new Date(107,3,30),
				new Date(107,4,1),
				new Date(107,4,2),
				new Date(107,7,1),
				new Date(107,8,30),
				new Date(107,9,1),
				new Date(107,9,2),
				new Date(107,10,1),
				new Date(107,11,31)
		}; //12

		CemEvent event = null;
		try {
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for null event");
			
			event = new CemEvent(dates[0]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,11,31) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
			event = new CemEvent(dates[1]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,0,2) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=2");
			if ( this.getPeriodIndexSO2(event,11,31) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
			event = new CemEvent(dates[11]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,11,30) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=30");
			if ( this.getPeriodIndexSO2(event,11,31) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
			event = new CemEvent(dates[0],dates[1]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,0,2) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=2");
			if ( this.getPeriodIndexSO2(event,11,31) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
			event = new CemEvent(dates[0],dates[11]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,11,30) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=30");
			if ( this.getPeriodIndexSO2(event,11,31) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
			event = new CemEvent(dates[1],dates[2]);
			if ( this.getPeriodIndexSO2(event,0,1) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=1, day=1");
			if ( this.getPeriodIndexSO2(event,1,3) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=2, day=3");
			if ( this.getPeriodIndexSO2(event,1,4) != 2) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=2, day=4");
			
			event = new CemEvent(dates[2],dates[11]);
			if ( this.getPeriodIndexSO2(event,1,3) != 0) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=2, day=3");
			if ( this.getPeriodIndexSO2(event,1,4) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=2, day=4");
			if ( this.getPeriodIndexSO2(event,11,30) != 1) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=30");
			if ( this.getPeriodIndexSO2(event,11,31) != 2) 
				System.out.println("Wrong getPeriodIndexSO2 for event: " + event.toString() + " and mon=12, day=31");
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		System.out.println("===End of testing testGetPeriodIndexSO2!");
	}
	
//	public static int getNumPeriodsNOx(CemEvent event) throws Exception {
//		if (event == null) 
//			return 2;
//		
//		int numPeriods = 2;
//		Date start = event.getStart();
//		Date end = event.getEnd();
//		if (start == null) {
//			throw new Exception("Event - " + event.toString() + " - start date is null!");
//		}
//		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
//		Date may1 = new Date(start.getYear(),Constants.MAY,1);
//		Date oct1 = new Date(start.getYear(),Constants.OCT,1);
//		if (start.equals(jan1)) {
//			if ( end == null) {
//				numPeriods = 2;
//			}
//			else if (end.equals(may1) || end.equals(oct1)){
//				numPeriods = 3;
//			}
//			else {
//				numPeriods = 4;
//			}
//		} else if (start.after(jan1) && start.before(may1)){
//			if ( end == null || end.equals(may1) || end.equals(oct1)) {
//				numPeriods = 4;
//			}
//			else {
//				numPeriods = 5;
//			}
//		} else if (start.equals(may1)) {
//			if ( end == null || end.equals(oct1)) {
//				numPeriods = 3;
//			}
//			else {
//				numPeriods = 4;
//			}
//		}else if (start.after(may1) && start.before(oct1)){
//			if ( end == null || end.equals(oct1)) {
//				numPeriods = 4;
//			}
//			else {
//				numPeriods = 5;
//			}
//		}else if (start.equals(oct1)) {
//			if ( end == null) {
//				numPeriods = 3;
//			}
//			else {
//				numPeriods = 4;
//			}
//		}
//		else if (start.after(oct1)){
//			if ( end == null) {
//				numPeriods = 4;
//			}
//			else {
//				numPeriods = 5;
//			}
//		}
//		return numPeriods;
//	}
	
	public static int getNumPeriodsNOx(CemEvent event) throws Exception {
		if (event == null) 
			return 2;
		
		int numPeriods = 2;
		Date start = event.getStart();
		Date end = event.getEnd();
		if (start == null) {
			throw new Exception("Event - " + event.toString() + " - start date is null!");
		}
		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
		Date may1 = new Date(start.getYear(),Constants.MAY,1);
		Date oct1 = new Date(start.getYear(),Constants.OCT,1);
		if (start.equals(jan1)) {
			if ( end == null) {
				numPeriods = 2;
			}
			else if (end.after(may1) && end.before(oct1)){
				numPeriods = 4;
			}
			else {
				numPeriods = 3;
			}
		} else if (start.after(jan1) && start.before(may1)){
			if ( end != null && end.after(may1) && end.before(oct1)) {
				numPeriods = 4;
			}
			else {
				numPeriods = 3;
			}
		} else if (start.equals(may1)) {
			if (end != null && end.equals(oct1)) {
				numPeriods = 2;
			}
			else {
				numPeriods = 3;
			}
		}else if (start.after(may1) && start.before(oct1)){
			if ( end != null && (end.before(oct1) || end.equals(oct1))) {
				numPeriods = 3;
			}
			else {
				numPeriods = 4;
			}
		}else { // (start.equals(oct1) || start.after(oct1))
			numPeriods = 3;
		}
		return numPeriods;
	}
	
	// for testing
	public void testGetNumPeriodsNOx() {
		
		Date [] dates = {
				new Date(107,0,1),  //0
				new Date(107,0,2),  //1
				new Date(107,1,4),  //2
				new Date(107,3,30), //3
				new Date(107,4,1),  //4
				new Date(107,4,2),  //5
				new Date(107,7,1),  //6
				new Date(107,8,30), //7
				new Date(107,9,1),  //8
				new Date(107,9,2),  //9
				new Date(107,10,1), //10
				new Date(107,11,31) //11
		}; //12

		CemEvent event = null;
		try {
			if ( this.getNumPeriodsNOx(event) != 2) 
				System.out.println("Wrong num NOx periods for null event");
			event = new CemEvent(dates[0]);
			if ( this.getNumPeriodsNOx(event) != 2) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			event = new CemEvent(dates[3]);
			if ( this.getNumPeriodsNOx(event) != 3) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			event = new CemEvent(dates[4]);
			if ( this.getNumPeriodsNOx(event) != 3) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			event = new CemEvent(dates[7]);
			if ( this.getNumPeriodsNOx(event) != 4) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			event = new CemEvent(dates[8]);
			if ( this.getNumPeriodsNOx(event) != 3) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			event = new CemEvent(dates[11]);
			if ( this.getNumPeriodsNOx(event) != 3) 
				System.out.println("Wrong num NOx periods for event: " + event.toString());
			
			if ( this.getNumPeriodsNOx( new CemEvent(dates[0], dates[1]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[2]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[3]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[4]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[5]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[6]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[7]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[8]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[9]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[10]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[0], dates[11]) ) != 3 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[2]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[3]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[4]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[5]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[7]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[8]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[9]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[1], dates[11]) ) != 3 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[4]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[5]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[7]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[8]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[9]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[3], dates[11]) ) != 3 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[4], dates[5]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[4], dates[7]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[4], dates[8]) ) != 2 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[4], dates[9]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[4], dates[11]) ) != 3 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[5], dates[7]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[5], dates[8]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[5], dates[9]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[5], dates[11]) ) != 4 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[7], dates[8]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[7], dates[9]) ) != 4 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[7], dates[11]) ) != 4 ||

				 this.getNumPeriodsNOx( new CemEvent(dates[8], dates[9]) ) != 3 ||
				 this.getNumPeriodsNOx( new CemEvent(dates[8], dates[11]) ) != 3 ||
				 
				 this.getNumPeriodsNOx( new CemEvent(dates[9], dates[11]) ) != 3)
				System.out.println("Wrong num NOx periods for event: ");
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		System.out.println("===End of testing getNumPeriodsNOx!");
	}
	
//	public static int getPeriodIndexNOx(CemEvent event, int month, int day) throws Exception {
//		if ( event == null ) { // 2 periods
//			if (month < Constants.MAY || month >= Constants.OCT) {
//				return 0; // non-oz season
//			}
//			return 1; // oz season
//		}
//		
//		Date start = event.getStart();
//		Date end = event.getEnd();
//		if (start == null) {
//			throw new Exception("Event - " + event.toString() + " - start date is null!");
//		}
//		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
//		Date may1 = new Date(start.getYear(),Constants.MAY,1);
//		Date oct1 = new Date(start.getYear(),Constants.OCT,1);
//		
//		if (start.equals(jan1)) {
//			if ( end == null) { //numPeriods = 2;
//				if (month < Constants.MAY || month >= Constants.OCT) return 0; // non-oz season
//				return 1; // oz season
//			}
//			
//			if (end.equals(may1) || end.equals(oct1)) { //numPeriods = 3;
//				if (month < Constants.MAY) return 0; //non-oz
//				if (month >= Constants.OCT) return 2; // non-oz
//				return 1; // oz season
//			}
//			//numPeriods = 4;
//			if (end.before(may1)) {
//				if ( month < Constants.MAY) { // non-oz
//					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 0; //non-oz
//					return 1; 
//				} 
//				if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
//				return 3; // non-oz season
//			}
//			if (end.after(oct1)) {
//				if (month < Constants.MAY) return 0; //non-oz
//				if (month >= Constants.MAY && month < Constants.OCT) return 1; // oz
//				if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
//					return 2; //non-oz
//				}
//				return 3; //non-oz
//			}
//			// ends in Apr~Sep
//			if (month < Constants.MAY) return 0; //non-oz
//			if (month >= Constants.MAY && month < Constants.OCT) {
//				if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 1; //non-oz
//				return 2; // oz
//			} 
//			return 3; //non-oz
//		} 
//		
//		if (start.after(jan1) && start.before(may1)){
//			if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) return 0; // non-oz
//			if ( end == null || end.equals(may1) || end.equals(oct1)) { //numPeriods = 4;
//				if ( month < Constants.MAY) return 1; // non-oz
//				if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
//				return 3; // non-oz season
//			}
//			//numPeriods = 5;
//			if ( end.before(may1)) {
//				if ( month < Constants.MAY) {//non-oz
//					if (month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) return 2;
//					return 1;
//				}
//				if ( month >= Constants.MAY && month < Constants.OCT) return 3; // oz-season
//				// non-oz season
//				return 4; 
//			} else if ( end.after(may1) && end.before(oct1)) {
//				if ( month < Constants.MAY) {//non-oz
//					return 1;
//				} 
//				if ( month >= Constants.MAY && month < Constants.OCT) { // oz-season
//					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 2; 
//					return 3;
//				} 
//				// non-oz season
//				return 4; 
//			} else {
//				if ( month < Constants.MAY) {//non-oz
//					return 1;
//				} else if ( month >= Constants.MAY && month < Constants.OCT) { // oz-season
//					return 2;
//				} else { // non-oz season
//					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
//						return 3; 
//					} 
//					return 4; 
//				}
//			}
//		}
//		
//		if (start.equals(may1)) {
//			if ( month < Constants.MAY) return 0;
//			if ( end == null || end.equals(oct1)) { //numPeriods = 3;
//				if ( month >= Constants.MAY && month < Constants.OCT) {
//					return 1;
//				}
//				return 2;
//			}
//			else { //numPeriods = 4;
//				if ( end.before(oct1)) {
//					if ( month >= Constants.MAY && month < Constants.OCT) {
//						if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
//							return 1; 
//						} 
//						return 2; 
//					}
//					return 3;
//				} else {
//					if ( month >= Constants.MAY && month < Constants.OCT) {
//						return 1;
//					} 
//					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
//						return 2; 
//					} 
//					return 3; 
//				}
//			}
//		}
//		
//		if (start.after(may1) && start.before(oct1)){
//			if ( month < Constants.MAY) return 0;
//			if ( end == null || end.equals(oct1)) { //numPeriods = 4;
//				if ( month >= Constants.MAY && month < Constants.OCT) {
//					if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
//						return 1; 
//					} 
//					return 2; 
//				}
//				return 3;
//			}
//			else { //numPeriods = 5;
//				if ( end.before(oct1)) {
//
//					if ( month >= Constants.MAY && month < Constants.OCT) {
//						if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
//							return 1; 
//						} 
//						if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) {
//							return 3;
//						}
//						return 2; 
//					}
//					
//					return 4;
//				} 
//				// end after 10/1
//				if ( month >= Constants.MAY && month < Constants.OCT) {
//					if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
//						return 1; 
//					} 
//
//					return 2; 
//				}
//				if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) {
//					return 4;
//				}
//				return 3;
//			}
//		}
//		
//		if (start.equals(oct1)) {
//			if ( month < Constants.MAY) return 0;
//			if ( month >= Constants.MAY && month < Constants.OCT) return 1;
//			
//			if ( end == null) { //numPeriods = 3;
//				return 2;
//			}
//
//			//numPeriods = 4;
//			if ( month < end.getMonth() || (month == end.getMonth() && day < end.getDate()) ) return 2;
//			return 3;
//		}
//		
//		//else if (start.after(oct1)){
//		if ( end == null) { //numPeriods = 4;
//			if ( month < Constants.MAY) return 0;
//			if ( month >= Constants.MAY && month < Constants.OCT) return 1;
//			if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate()) ) return 2;
//			return 3;
//		}
//		
//		//start.after(oct1) && end != null: numPeriods = 5;
//		if ( month < Constants.MAY) return 0;
//		if ( month >= Constants.MAY && month < Constants.OCT) return 1;
//		if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate()) ) return 2;
//		if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate()) ) return 4;
//		return 3;
//	}
	
	public static int getPeriodIndexNOx(CemEvent event, int month, int day) throws Exception {
		if ( event == null ) { // 2 periods
			if (month < Constants.MAY || month >= Constants.OCT) {
				return 0; // non-oz season
			}
			return 1; // oz season
		}
		
		Date start = event.getStart();
		Date end = event.getEnd();
		if (start == null) {
			throw new Exception("Event - " + event.toString() + " - start date is null!");
		}

		Date jan1 = new Date(start.getYear(),Constants.JAN,1);
		Date may1 = new Date(start.getYear(),Constants.MAY,1);
		Date oct1 = new Date(start.getYear(),Constants.OCT,1);
		
		if (start.equals(jan1)) {
			
			if (end == null) {
				if (month < Constants.MAY || month >= Constants.OCT) return 0; // non-oz season
				return 1; // oz season
			}
			
			if (end.equals(may1) || end.equals(oct1)) {
				if (month < Constants.MAY) return 0; //non-oz
				if (month >= Constants.OCT) return 2; // non-oz
				return 1; // oz season
			}

			if (end.before(may1)) {
				if ( month < Constants.MAY) { // non-oz
					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 0; //non-oz
					else return 1; 
				} 
				else if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
				else return 1; // non-oz season
			}

			if (end.after(oct1)) {
				if (month < Constants.MAY) return 0; //non-oz
				else if (month >= Constants.MAY && month < Constants.OCT) return 1; // oz
				else if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 0; //non-oz
				else return 2; //non-oz
			}
			
			// ends in Apr~Sep
			if (month < Constants.MAY) return 0; //non-oz
			else if (month >= Constants.MAY && month < Constants.OCT) {
				if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 1; //non-oz
				return 2; // oz
			} 
			else return 3; //non-oz
		} 
		
		if (start.after(jan1) && start.before(may1)){
			if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) return 0; // non-oz
			if ( end == null) { //numPeriods = 3;
				if ( month < Constants.MAY) return 1; // non-oz
				if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
				return 1; // non-oz season
			}
			if ( end.equals(may1) || end.equals(oct1)) { //numPeriods = 3;
				if ( month < Constants.MAY) return 1; // non-oz
				if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
				return 0; // non-oz season
			}
			if ( end.before(may1)) {
				if ( month < Constants.MAY) {//non-oz
					if (month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) return 0;
					return 1;
				}
				if ( month >= Constants.MAY && month < Constants.OCT) return 2; // oz-season
				// non-oz season
				return 0; 
			} else if ( end.after(may1) && end.before(oct1)) {
				if ( month < Constants.MAY) {//non-oz
					return 1;
				} 
				if ( month >= Constants.MAY && month < Constants.OCT) { // oz-season
					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 2; 
					return 3;
				} 
				// non-oz season
				return 0; 
			} else {
				if ( month < Constants.MAY) {//non-oz
					return 1;
				} else if ( month >= Constants.MAY && month < Constants.OCT) { // oz-season
					return 2;
				} else { // non-oz season
					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
						return 1; 
					} 
					return 0; 
				}
			}
		}
		
		if (start.equals(may1)) {
			if ( month < Constants.MAY) return 0;
			if ( end == null) { //numPeriods = 3;
				if ( month >= Constants.MAY && month < Constants.OCT) {
					return 1;
				}
				return 2;
			}
			else if ( end.equals(oct1)) { //numPeriods = 2;
				if ( month >= Constants.MAY && month < Constants.OCT) {
					return 1;
				}
				return 0;
			}
			else if ( end.before(oct1)) { // 3
				if ( month >= Constants.MAY && month < Constants.OCT) {
					if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
						return 1; 
					} 
					return 2; 
				}
				return 0;
			} else {
				if ( month >= Constants.MAY && month < Constants.OCT) {
					return 1;
				} 
				if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) {
					return 2; 
				} 
				return 0; 
			}
		}
		
		if (start.after(may1) && start.before(oct1)){
			
			if ( Constants.DEBUG && start.getMonth() == 7 ) {
				System.out.print("");
			}
			
			if ( month < Constants.MAY) return 0;
			
			if ( end == null) { //numPeriods = 4;
				if ( month >= Constants.MAY && month < Constants.OCT) {
					if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
						return 1; 
					} 
					return 2; 
				}
				return 3;
			}
			
			else if ( end.equals(oct1)) { //numPeriods = 3;
				if ( month >= Constants.MAY && month < Constants.OCT) {
					if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
						return 1; 
					} 
					return 2; 
				}
				return 0;
			}
			
			else if ( end.before(oct1)) { // 3

				if ( month >= Constants.MAY && month < Constants.OCT) {
					if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
						return 1; 
					} 
					if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) {
						return 1;
					}
					return 2; 
				}

				return 0;
			} 
			
			// end after 10/1
			if ( month >= Constants.MAY && month < Constants.OCT) {
				if (month < start.getMonth() || (month == start.getMonth() && day < start.getDate())) {
					return 1; 
				} 

				return 2; 
			}
			else if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate())) {
				return 0;
			}
			else return 3;
		} else if ( start.equals(oct1)) {
			if (month < Constants.MAY) return 0; //non-oz
			if (month >= Constants.MAY && month < Constants.OCT) return 1; //oz
			// after oct1
			if ( end == null) {
				return 2;
			} else {
				if (month < end.getMonth() || (month == end.getMonth() && day < end.getDate())) return 2; 
				return 0; // oz season
			}
		}
		
		if ( month < Constants.MAY) return 0;
		if ( month >= Constants.MAY && month < Constants.OCT) return 1;
		if (start.equals(oct1)) {
			if ( end == null) { //numPeriods = 3;
				return 2;
			}
			//numPeriods = 3;
			if ( month < end.getMonth() || (month == end.getMonth() && day < end.getDate()) ) return 2;
			return 0;
		}
		//else if (start.after(oct1)){
		else {
			if ( end == null) { //numPeriods = 3;
				if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate()) ) return 0;
				return 2;
			} else {
				if ( month < start.getMonth() || (month == start.getMonth() && day < start.getDate()) ) return 0;
				if ( month > end.getMonth() || (month == end.getMonth() && day >= end.getDate()) ) return 0;
				return 2;
			}
		}
	}
    
	private void testIndexingHelper(CemEvent event) throws Exception {
		int numMonDayPairs = 11;
		int [] mons = {0,0,2,3, 4,5,8, 9,9,10,11};
		int [] days = {1,2,1,30,1,1,30,1,2,1, 31};
		
		for ( int i = 0; i < numMonDayPairs; i++) {
			int index = CemEvent.getPeriodIndexNOx(event, mons[i], days[i]);
			printIndex(event, mons[i], days[i],index);
		}
	}
	
	public void testNOXIndexing() {
		
		Date [] dates = {
				new Date(107,0,1),
				new Date(107,0,2),
				new Date(107,1,4),
				new Date(107,3,30),
				new Date(107,4,1),
				new Date(107,4,2),
				new Date(107,7,1),
				new Date(107,8,30),
				new Date(107,9,1),
				new Date(107,9,2),
				new Date(107,10,1),
				new Date(107,11,31)
		};

		CemEvent event;
		try {
			
			int numDates = dates.length;
			for ( int i = 0; i < numDates; i++) { // start
				System.out.println("========");
				System.out.println("start: " + dates[i].getMonth() + "-" + dates[i].getDate());
				System.out.println("========");
				this.testIndexingHelper(new CemEvent(dates[i]));
				for (int j = i+1; j < numDates; j++) {
					System.out.println("========");
					this.testIndexingHelper(new CemEvent(dates[i], dates[j]));
				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void printIndex(CemEvent event, int mon, int day, int index) {
		//System.out.println( "\n>>> \n" + event.toString() + "\n" + "mon: " + (mon +1) + ", day: " + day + ", Index: " + index);
		System.out.print( index + ",");
	}
	
	public static void main(String[] args) {
		CemEvent cal = new CemEvent(null);

		//cal.testGetNumPeriodsSO2();
		//cal.testGetPeriodIndexSO2();
		cal.testGetNumPeriodsNOx();
		cal.testNOXIndexing();
	}
}
