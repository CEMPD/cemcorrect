package edu.unc.cem.correct;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import edu.unc.cem.util.CemEvent;
import edu.unc.cem.util.Constants;
import edu.unc.cem.util.Tokenizer;

/*
 * Calculate averages and correct bad values at the state level.
 */
public class Calculator {

    private int [] numDaysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};

    private int INX_HI = 0;
    private int INX_SO2 = 1;
    private int INX_NOX = 2;

    private double [] thresholds = {0.2, 0.2, 0.2};
    private Tokenizer tokenizer;
    private ConcurrentHashMap<String, String> heatInputs;
    private List<ConcurrentHashMap<String, String>> lstHeatInputMonthly;
    private ConcurrentHashMap<String, String>[] heatInputsMonthly;
    private float [] HIPercnt = new float[12];

    private ConcurrentHashMap<String, Float> preSubNoxMTotal;
    private ConcurrentHashMap<String, Float> postSubNoxMTotal;
    private ConcurrentHashMap<String, Float> preSubSO2MTotal;
    private ConcurrentHashMap<String, Float> postSubSO2MTotal;
    private ConcurrentHashMap<String, Float> preSubHeatInputTotal;
    private ConcurrentHashMap<String, Float> postSubHeatInputTotal;
    private ConcurrentHashMap<String, Integer> numOfHoursSO2;
    private ConcurrentHashMap<String, Integer> numOfHoursNOX;
    private ConcurrentHashMap<String, Integer> numOfHoursHI;
    private List<String> allSrcs;
    private StringBuilder warnings = new StringBuilder();
    private int currentMonth = -1;
    private CemEvaluator eval;
    public static String NaN = "-9";
    public static String MOD_FLAG = "5";

    private Map<String,CemEvent> eventMap;
    private int numPeriodsSO2 = 1;
    private int numPeriodsNOx = 2;

    private List<ConcurrentHashMap<String, String>> periodSO2TotalEmission;
    private List<ConcurrentHashMap<String, String>> periodNOXTotalEmission;
    private List<HashMap<String, String>> periodBadHI4NOX;
    private List<HashMap<String, String>> periodBadHI4SO2;
    private List<ConcurrentHashMap<String, String>> lstSO2Monthly;
    private ConcurrentHashMap<String, String>[] SO2Monthly;
    private float [] SO2Percnt = new float[12];
    private List<ConcurrentHashMap<String, String>> lstNOXMonthly;
    private ConcurrentHashMap<String, String>[] NOXMonthly;
    private float [] NOXPercnt = new float[12];

    @SuppressWarnings("unchecked")
    public Calculator(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        eval = new CemEvaluator();
        heatInputs = new ConcurrentHashMap<String, String>();
        lstSO2Monthly = new ArrayList<ConcurrentHashMap<String, String>>();
        for ( int i=0; i<12; i++) {
            lstSO2Monthly.add(new ConcurrentHashMap<String, String>());
        }
        SO2Monthly = (ConcurrentHashMap<String, String>[]) lstSO2Monthly.toArray(new ConcurrentHashMap[0]);
        lstNOXMonthly = new ArrayList<ConcurrentHashMap<String, String>>();
        for ( int i=0; i<12; i++) {
            lstNOXMonthly.add(new ConcurrentHashMap<String, String>());
        }
        NOXMonthly = (ConcurrentHashMap<String, String>[]) lstNOXMonthly.toArray(new ConcurrentHashMap[0]);
        lstHeatInputMonthly = new ArrayList<ConcurrentHashMap<String, String>>();
        for ( int i=0; i<12; i++) {
            lstHeatInputMonthly.add(new ConcurrentHashMap<String, String>());
        }
        heatInputsMonthly = (ConcurrentHashMap<String, String>[]) lstHeatInputMonthly.toArray(new ConcurrentHashMap[0]);

        preSubNoxMTotal = new ConcurrentHashMap<String, Float>();
        postSubNoxMTotal = new ConcurrentHashMap<String, Float>();
        preSubSO2MTotal = new ConcurrentHashMap<String, Float>();
        postSubSO2MTotal = new ConcurrentHashMap<String, Float>();
        preSubHeatInputTotal = new ConcurrentHashMap<String, Float>();
        postSubHeatInputTotal = new ConcurrentHashMap<String, Float>();
        numOfHoursSO2 = new ConcurrentHashMap<String, Integer>();
        numOfHoursNOX = new ConcurrentHashMap<String, Integer>();
        numOfHoursHI = new ConcurrentHashMap<String, Integer>();

        allSrcs = new ArrayList<String>();

        getThresholds();
    }

    private void getThresholds() {
        String thresholdStr = System.getenv("THRESHOLDHI");
        thresholds[0] = Double.parseDouble(thresholdStr);
        thresholdStr = System.getenv("THRESHOLDSO2");
        thresholds[1] = Double.parseDouble(thresholdStr);
        thresholdStr = System.getenv("THRESHOLDNOX");
        thresholds[2] = Double.parseDouble(thresholdStr);
    }

    public void addRecords(List<String> records, File file) {
        int count = 1;

        for (String record : records) {
            try {
                String[] t = tokenizer.tokens(record);

                //System.out.println(t[0] + " " + t[1]);

                // for testing
                if ( Constants.DEBUG && !(t[0].equals("3948") && t[1].equals("1"))) {
                    count++;
                    continue;
                }

                String badId = t[0]+t[1]+Constants.COMMA+t[2]+Constants.COMMA+t[3]; //t[0]-ORISID; t[1]-BLRID; t[2]-YYMMDD; t[3]-HOUR
                String srcHrId = t[0]+t[1]+t[3];
                String srcId = t[0]+t[1];
                int month = 0;
                int day = 1;

                try {
                    month = Integer.parseInt(t[2].substring(2, 4))-1;
                    day   = Integer.parseInt(t[2].substring(4));
                } catch (Exception e) {
                    System.out.println("Bad date in file " + file.getName() + ": " + record);
                    System.exit(1);
                }

                //for debug
                if (Constants.DEBUG && month==3 && (day == 27 || day == 28)  && (t[3].equals("22") || t[3].equals("23"))) {
                    System.out.println("April " + day);
                }

                // update num of days in Feb if this is a leap year
                if ( month == 2 && day == 29) {
                    this.numDaysInMonth[1] = 29;
                }

                // get the event period related total emission
                CemEvent event = null;
                if ( this.eventMap != null)
                    event = this.eventMap.get( srcId);
                ConcurrentHashMap<String, String> periodNOXTotalEmission = this.getPeriodNOXTotalEmission(event, month, day);
                ConcurrentHashMap<String, String> periodSO2TotalEmission = this.getPeriodSO2TotalEmission(event, month, day);
                HashMap<String,String> periodBadHINox = this.getPeriodBadHI4NOX(event, month, day);
                HashMap<String,String> periodBadHISo2 = this.getPeriodBadHI4SO2(event, month, day);

                if ( periodNOXTotalEmission == null || periodSO2TotalEmission == null ||
                     periodBadHINox == null         || periodBadHISo2 == null ) {
                    System.out.println("Calculator was not initialized correctly - some hash maps are null!");
                    System.exit(1);
                }

                // read in and sum up good Heat Input
                String exist = null;
                float goodHI = 0.0f;
                if (isGoodFloatValue(t[10]) && !isFlagged4Mean(t[11])) {  //Reads in good HI values, add them up, store with a count
                    goodHI = Float.parseFloat(t[10]);
                    // update yearly total
                    exist = heatInputs.get(srcHrId);
                    if (exist == null || exist.isEmpty()) {
                        heatInputs.put(srcHrId, t[10] + ",1");
                    }
                    else {
                        updateValue(goodHI, srcHrId, exist, heatInputs);
                    }
                    //update monthly total
                    exist = heatInputsMonthly[month].get(srcHrId);
                    if (exist == null || exist.isEmpty()) {
                        heatInputsMonthly[month].put(srcHrId, t[10] + ",1");
                    }
                    else {
                        updateValue(goodHI, srcHrId, exist, heatInputsMonthly[month]);
                    }
                }

                //Reads in good NOX values, get emission rate if HI is good;
                //if not, store them with the 'bad' HI value
                if (isGoodFloatValue(t[4]) && !isFlagged4Mean(t[13])) {
                    exist = periodNOXTotalEmission.get(srcHrId);
                    if (goodHI == 0.0f) {
                        periodBadHINox.put(badId, t[4]+","+t[10]); //simply remember the bad HI value for replacement later on
                    } else if (exist == null || exist.isEmpty()) {
                        periodNOXTotalEmission.put(srcHrId, Float.parseFloat(t[4])/goodHI + ",1");
                    } else {
                        updateValue(Float.parseFloat(t[4])/goodHI, srcHrId, exist, periodNOXTotalEmission);
                    }

                    exist = NOXMonthly[month].get(srcHrId);
                    if (goodHI == 0.0f) {
                        //periodBadHINox.put(badId, t[4]+","+t[10]); //simply remember the bad HI value for replacement later on
                    } else if (exist == null || exist.isEmpty()) {
                        NOXMonthly[month].put(srcHrId, Float.parseFloat(t[4])/goodHI + ",1");
                    } else {
                        updateValue(Float.parseFloat(t[4])/goodHI, srcHrId, exist, NOXMonthly[month]);
                    }
                }

                //Reads in good SO2 values, get emission rate if HI is good;
                //if not, store them with the 'bad' HI value
                if (isGoodFloatValue(t[5]) && !isFlagged4Mean(t[12])) {
                    exist = periodSO2TotalEmission.get(srcHrId);
                    if (goodHI == 0.0f) {
                        periodBadHISo2.put(badId, t[5]+","+t[10]); //simply remember the bad HI value for replacement later on
                    } else if (exist == null || exist.isEmpty()) {
                        periodSO2TotalEmission.put(srcHrId, Float.parseFloat(t[5])/goodHI + ",1");
                    } else {
                        updateValue(Float.parseFloat(t[5])/goodHI, srcHrId, exist, periodSO2TotalEmission);
                    }

                    exist = SO2Monthly[month].get(srcHrId);
                    if (goodHI == 0.0f) {
                        //periodBadHISo2.put(badId, t[5]+","+t[10]); //simply remember the bad HI value for replacement later on
                    } else if (exist == null || exist.isEmpty()) {
                        SO2Monthly[month].put(srcHrId, Float.parseFloat(t[5])/goodHI + ",1");
                    } else {
                        updateValue(Float.parseFloat(t[5])/goodHI, srcHrId, exist, SO2Monthly[month]);
                    }
                }

                count++;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Bad line in file " + file.getName() + " (" + count + "): " + record);
                System.exit(1);
            }
        }
    }

    private void updateValue(float t, String srcHrId, String exist, ConcurrentHashMap<String, String> map) {
        String[] temp = exist.split(",");
        try {
            float total = Float.parseFloat(temp[0]) + t;
            int inc = Integer.parseInt(temp[1]) + 1;
            map.replace(srcHrId, total+","+inc);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isGoodFloatValue(String t) {
        boolean good = (t != null && !t.isEmpty() && !t.equals(NaN));
        try {
            Float.parseFloat(t);
        } catch (Exception e) {
            good = false;
        }

        return good;
    }

    private boolean isFlagged4Mean(String t) {
        if (t == null || t.isEmpty()) return true;

        int flag = 3;

        try {
            flag = Integer.parseInt(t);
        } catch (Exception e) {
            //no-op
        }

        return flag > 2;
    }

    private boolean isFlagged4Anomalous(String t) {
        if (t == null || t.isEmpty()) return true;

        int flag = 3;

        try {
            flag = Integer.parseInt(t);
        } catch (Exception e) {
            //no-op
        }

        return flag > 1;
    }

    public void calculateDayPercnt() {
        for (int i=0; i<12; i++) {
            ConcurrentHashMap<String,String> map = heatInputsMonthly[i];
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String temp =  map.get(key);
                String[] values = temp.split(",");
                float numDays = Float.parseFloat(values[1]);
                HIPercnt[i] = numDays/numDaysInMonth[i];
                if (Constants.DEBUG)
                    System.out.println( "Month " + i+1 + " HI percent: " + HIPercnt[i]);
            }
            map = SO2Monthly[i];
            keys = map.keySet();
            for (String key : keys) {
                String temp =  map.get(key);
                String[] values = temp.split(",");
                float numDays = Float.parseFloat(values[1]);
                SO2Percnt[i] = numDays/numDaysInMonth[i];
                if (Constants.DEBUG)
                    System.out.println( "Month " + i+1 + " SO2 percent: " + SO2Percnt[i]);
            }
            map = NOXMonthly[i];
            keys = map.keySet();
            for (String key : keys) {
                String temp =  map.get(key);
                String[] values = temp.split(",");
                float numDays = Float.parseFloat(values[1]);
                NOXPercnt[i] = numDays/numDaysInMonth[i];
                if (Constants.DEBUG)
                    System.out.println( "Month " + i+1 + " NOX percent: " + NOXPercnt[i]);
            }
        }
    }

    public void calculateMeans() {

        calculateDayPercnt();

        calculateHeatInputMean(heatInputs);

        for (int i=0; i<12; i++) {
            calculateHeatInputMean(heatInputsMonthly[i]);
            calculateHeatInputMean(SO2Monthly[i]);
            calculateHeatInputMean(NOXMonthly[i]);
        }

        for ( int i=0; i<this.numPeriodsNOx; i++) {
            ConcurrentHashMap<String,String> map = this.periodNOXTotalEmission.get(i);
            HashMap<String,String> badMap = this.periodBadHI4NOX.get(i);

            if (Constants.DEBUG) {
                System.out.println("===NOx period " + i);
                System.out.println("Map size: " + map.size());
            }
            calculateEmissionRate(map, heatInputs, badMap);
            if (Constants.DEBUG) {
                System.out.println("Map size: " + map.size());
                System.out.println("***");
            }
        }

        for ( int i=0; i<this.numPeriodsSO2; i++) {
            ConcurrentHashMap<String,String> map = this.periodSO2TotalEmission.get(i);
            HashMap<String,String> badMap = this.periodBadHI4SO2.get(i);
            if (Constants.DEBUG)
                System.out.println("===SO2 period " + i);
            calculateEmissionRate(map, heatInputs, badMap);
        }

        this.periodBadHI4NOX = null;
        this.periodBadHI4SO2 = null;
    }

    private void calculateHeatInputMean(ConcurrentHashMap<String, String> map) {
        Set<String> keys = map.keySet();

        for (String key : keys) {
            String temp =  map.get(key);
            String[] values = temp.split(",");
            float mean = Float.parseFloat(values[0]) / Float.parseFloat(values[1]);
            map.replace(key, mean+"");
            if (Constants.DEBUG)
                System.out.println(key + " mean: " + mean);
        }
    }

    private void calculateEmissionRate(ConcurrentHashMap<String, String> map,
            ConcurrentHashMap<String, String> hi, HashMap<String, String> toBeAdded) {
        Set<String> badKeys = toBeAdded.keySet();

        for (String badkey : badKeys) { // Calculate emission rate with corrected HI values and add back to the bigger emission rates list
            String[] subkeys = badkey.split(Constants.COMMA);
            String srcHrKey = subkeys[0]+subkeys[2]; //We know these components, see addRecords().badId (line 53)
            String[] stored = toBeAdded.get(badkey).split(","); //We stored this way: mass emission,flagged heat input
            String meanHI = hi.get(srcHrKey);
            float meanHIf = 0.0f;

            if (meanHI == null) {
                String msg = "Warning: average heat input cannot be calculated for source (id=" + subkeys[0] + "(ORISID+BLRID)) for hour " + subkeys[2];
                System.out.println(msg);

                int index = Integer.parseInt(subkeys[2]); //It is the hour
                int prev = 0;
                int nxt = 0;

                if (index == 0) {prev = 23; nxt = 1;}
                else if (index == 23) {prev = 22; nxt = 0;}
                else {prev = index - 1; nxt = index + 1;}

                String pMeanHI = hi.get(subkeys[0]+prev);
                String nMeanHI = hi.get(subkeys[0]+nxt);

                if (pMeanHI == null && nMeanHI == null)
                    meanHIf = Float.NaN;
                else if (pMeanHI == null)
                    meanHIf = Float.parseFloat(nMeanHI);
                else if (nMeanHI == null)
                    meanHIf = Float.parseFloat(pMeanHI);
                else if (pMeanHI != null && nMeanHI != null)
                    meanHIf = (Float.parseFloat(pMeanHI) + Float.parseFloat(pMeanHI)) / 2.0f;

            } else
                meanHIf = Float.parseFloat(meanHI);

            float rate = 0.0f;
            float massEmis = Float.parseFloat(stored[0]);
            float badHI = Float.parseFloat(stored[1]);

            if (meanHIf == Float.NaN) {} //NOTE: no-op
            else if (badHI > meanHIf * 2.0 || stored[1].equals(NaN) || stored[1].equals("0") || stored[1].isEmpty()) {rate = massEmis/meanHIf;}
            else {rate = massEmis/badHI;}

            String exist = map.get(srcHrKey);
            if (exist == null && rate != 0.0f) map.put(srcHrKey, rate + ",1");
            if (exist != null && rate != 0.0f) updateValue(rate, srcHrKey, exist, map);

            if (badHI == 0.0f && massEmis > 0.0f) warnings.append("WARNING: CANNOT HAVE MASS EMISSION VALUES WHILE HEAT INPUT VALUE IS 0!!! " +
                    "Source: " + srcHrKey + "\n");
        }

        Set<String> keys = map.keySet();

        for (String key : keys) { //Now we can calculate the annual/seasonal means
            String temp =  map.get(key);
            String[] values = temp.split(",");
            float mean = Float.parseFloat(values[0]) / Float.parseFloat(values[1]);
            map.replace(key, mean+"");
            if (Constants.DEBUG)
                System.out.println( key + ": num " + values[1] + ", mean: " + mean);
        }
    }

    private void printMap(Map<String, String> map) {
        for ( String key : map.keySet()) {
            System.out.println("\"" + key + "\": " + map.get(key));
        }
    }

    public List<String> processLine(String line, File infile) throws Exception {
        List<String> toReturn = new ArrayList<String>();
        StringBuilder log = new StringBuilder();
        String[] t = tokenizer.tokens(line);
        boolean hiflag = isFlagged4Anomalous(t[11]);
        boolean so2flag = isFlagged4Anomalous(t[12]);
        boolean noxmassflag = isFlagged4Anomalous(t[13]);
        boolean noxrateflag = isFlagged4Anomalous(t[14]);

        // for testing
        if (Constants.DEBUG) {
            System.out.println(line);
        }

        if (!hiflag && !so2flag && !noxmassflag && !noxrateflag) { // no need to replace values
            toReturn.add(line + "\n");
            toReturn.add(null);

            return toReturn;
        } //Everything is normal

        String srcHrId = t[0]+t[1]+t[3];
        String srcHrInfo = t[0] + "_" + t[1] + "_" + t[3];
        String srcId = t[0] + "_" + t[1];
        String srcIdForPeriod = t[0] + t[1];

        int month = -1;
        int day = -1;
        try {
            if (currentMonth == -1)
                currentMonth = Integer.parseInt(t[2].substring(2, 4));
            day = Integer.parseInt(t[2].substring(4));
        } catch (Exception e) {
            throw new Exception("Bad date: " + t[2] + "in file " + infile.getName() + " : " + line);
        }
        month = currentMonth - 1;

        if (!allSrcs.contains(srcId)) allSrcs.add(srcId);

        addToTotals(t[4], srcId, preSubNoxMTotal);
        addToTotals(t[5], srcId, preSubSO2MTotal);
        addToTotals(t[10], srcId, preSubHeatInputTotal);

        // get HI mean: if num of good values >= the HI threshold, use monthly average, else use annual average
        String hiMean = "";
        if ( HIPercnt[month] >= thresholds[INX_HI]) {
            hiMean = heatInputsMonthly[month].get(srcHrId);
        } else {
            heatInputs.get(srcHrId);
        }

        if (hiMean == null || hiMean.equals("0") || Float.parseFloat(hiMean) == 0.0f)
            log.append("Warning: " + infile.getName() + ": source: " + srcHrInfo + " heat input mean value: " + hiMean + "\n");
        String head = "Replaced: " + infile.getName() + ": source: " + srcId + " date: " + t[2] + " hour: " + t[3] + " ";
        String templog = head;
        String hiLocal = t[10];
        if (!isAnomalous(hiLocal, hiMean)) hiMean = hiLocal;

        // get the event
        CemEvent event = null;
        if (this.eventMap != null)
            event = this.eventMap.get(srcIdForPeriod);

        // check init
        if ( periodNOXTotalEmission == null || periodSO2TotalEmission == null //||
                //periodBadHINox == null         || periodBadHISo2 == null )
        ) {
            System.out.println("Calculator was not initialized correctly - some hash maps are null!");
            System.exit(1);
        }

        // get NOX rate
        ConcurrentHashMap<String, String> NOXEmission = NOXMonthly[month];
        if ( NOXPercnt[month] < thresholds[INX_NOX])
        {
            int periodNOx = CemEvent.getPeriodIndexNOx(event, month, day);
            ConcurrentHashMap<String, String> periodNOXTotalEmission = this.periodNOXTotalEmission.get(periodNOx);
            NOXEmission = periodNOXTotalEmission;
        }
        String noxRate = NOXEmission.get(srcHrId);
        if (noxRate == null || Float.isInfinite(Float.parseFloat(noxRate)))
            log.append("Warning: " + infile.getName() + ": source: " + srcHrInfo + " nox emission mean value: " + noxRate + "\n");

        if ( Constants.DEBUG && noxRate == null) {
            System.out.println("SrcHrId = \"" + srcHrId + "\"\nMap size: " + NOXEmission.size() );

            printMap( NOXEmission);
        }

        // get SO2 rate
        ConcurrentHashMap<String, String>SO2Emission = SO2Monthly[month];
        if ( SO2Percnt[month] < thresholds[INX_SO2])
        {
            int periodSO2 = CemEvent.getPeriodIndexSO2(event, month, day);
            ConcurrentHashMap<String, String> periodSO2TotalEmission = this.periodSO2TotalEmission.get(periodSO2);
            SO2Emission = periodSO2TotalEmission;
        }
        String so2Rate = SO2Emission.get(srcHrId);
        if (so2Rate == null || Float.isInfinite(Float.parseFloat(so2Rate)))
            log.append("Warning: " + infile.getName() + ": source: " + srcHrInfo + " so2 emission mean value: " + so2Rate + "\n");

        // Heat Input
        String t10orig = t[10];
        if (hiflag && !t[10].equals(NaN)) {
            t[10] = hiMean;

            if (t10orig != null && !t10orig.isEmpty() && !t10orig.equals(t[10])) {
                t[11] = MOD_FLAG;
                templog += "HTINPUT: (original: " + t10orig + " substituted: " + t[10] + ") ";
                incrementCount(srcId, numOfHoursHI);
            }
            addToTotals(t[10], srcId, postSubHeatInputTotal);
        } else if (!t[10].equals(NaN)) {
            addToTotals(t[10], srcId, postSubHeatInputTotal);
        }

        // SO2
        String t5orig = t[5];
        if (so2flag && hiflag && !t[5].equals(NaN) && isAnomalous(t[5], so2Rate, hiMean)) {
            t[5] = typicalValue(so2Rate, hiMean).toString();

            if (t5orig != null && !t5orig.isEmpty() && !t5orig.equals(t[5])) {
                t[12] = MOD_FLAG;
                templog += "SO2MASS: (original: " + t5orig + " substituted: " + t[5] + ") ";
                incrementCount(srcId, numOfHoursSO2);
            }
            addToTotals(t[5], srcId, postSubSO2MTotal);
        } else if (!t[5].equals(NaN)) {
            addToTotals(t[5], srcId, postSubSO2MTotal);
        }

        // NOx mass
        String t4orig = t[4];
        // change to: if NEITHER of noxmass or noxrate are measured (1) AND boxmass is anomalous - do replacement
        if ((noxmassflag && noxrateflag) && !t[4].equals(NaN) && isAnomalous(t[4], noxRate, hiMean)) {
            t[4] = typicalValue(noxRate, hiMean).toString();

            if (t4orig != null && !t4orig.isEmpty() && !t4orig.equals(t[4])) {
                t[13] = MOD_FLAG;
                templog += "NOXMASS: (original: " + t4orig + " substituted: " + t[4] + ") ";
                incrementCount(srcId, numOfHoursNOX);
            }
            addToTotals(t[4], srcId, postSubNoxMTotal);
        } else if (!t[4].equals(NaN))
            addToTotals(t[4], srcId, postSubNoxMTotal);

        // NOx rate
        String t6orig = t[6];
        // change to: if NEITHER of noxmass or noxrate are measured (1) AND boxmass is anomalous - do replacement
        if ((noxmassflag && noxrateflag) && !t[6].equals(NaN) && isAnomalous(t[6], noxRate, hiMean)) {
            t[6] = typicalValue(noxRate, hiMean).toString();

            if (t6orig != null && !t6orig.isEmpty() && !t6orig.equals(t[6])) {
                t[14] = MOD_FLAG;
                templog += "NOXRATE: (original: " + t6orig + " substituted: " + t[6] + ") ";
            }
        }

        if (templog.length() > head.length()) log.append(templog + "\n");

        toReturn.add(t[0]+",\""+t[1]+"\",\""+t[2]+"\","+t[3]+","+t[4]+","+t[5]+","+t[6]+","+t[7]+","+t[8]+","+t[9]+","+t[10]+","+t[11]+","+t[12]+","+t[13]+","+t[14]+","+t[15] + "\n"); //[0]: output line
        toReturn.add(log.toString());

        return toReturn;
    }

    private void addToTotals(String t, String srcId, ConcurrentHashMap<String, Float> totals) {
        if (t == null || t.isEmpty()) return;

        Float value = totals.get(srcId);
        if (value == null && !t.equals(NaN)) totals.put(srcId, Float.valueOf(t));
        else if (value != null && !t.equals(NaN)) totals.replace(srcId, Float.parseFloat(t) + value.floatValue());
    }

    private void incrementCount(String srcId, ConcurrentHashMap<String, Integer> totals) {
        Integer value = totals.get(srcId);
        if (value == null) totals.put(srcId, new Integer(1));
        else if (value != null) totals.replace(srcId, value.intValue() + 1);
    }

    private boolean isAnomalous(String field, String normal) {
        Float value = null;

        try {
            value = Float.valueOf(field);
        } catch (Exception e) {
            return true;
        }

        if (field.equals(NaN)) return true;

        if (normal == null || normal.trim().isEmpty()) return false;

        Float normVal = Float.valueOf(normal);

        if (eval.compare(value, normVal) > 0) return true;

        return false;
    }

    private boolean isAnomalous(String field, String rate, String hi) {
        Float value = null;

        try {
            value = Float.valueOf(field);
        } catch (Exception e) {
            return true;
        }

        if (field.equals(NaN)) return true;

        Float normVal = typicalValue(rate, hi);

        if (normVal == null) return false;

        if (eval.compare(value, normVal) > 0) return true;

        return false;
    }

    private Float typicalValue(String rate, String hi) {
        if (rate == null || rate.isEmpty() || hi == null || hi.isEmpty())
             return null;

        try {
            float frate = Float.parseFloat(rate);
            float fhi = Float.parseFloat(hi);

            return new Float(frate * fhi);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearTotals() {
        preSubNoxMTotal.clear();
        postSubNoxMTotal.clear();
        preSubSO2MTotal.clear();
        postSubSO2MTotal.clear();
        preSubHeatInputTotal.clear();
        postSubHeatInputTotal.clear();
        currentMonth = -1;
        numOfHoursSO2.clear();
        numOfHoursNOX.clear();
        numOfHoursHI.clear();
    }

    public List<String> getTotals() {
        List<String> totals = new ArrayList<String>();

        for (String id : allSrcs) {
            Float preSubNoxTotal = preSubNoxMTotal.get(id) ;
            Float postSubNoxTotal = postSubNoxMTotal.get(id);

            Float preSubSO2Total = preSubSO2MTotal.get(id);
            Float postSubSO2Total = postSubSO2MTotal.get(id);

            Float preSubHITotal = preSubHeatInputTotal.get(id);
            Float postSubHITotal = postSubHeatInputTotal.get(id);

            Integer totalNOXHours = numOfHoursNOX.get(id);
            Integer totalSO2Hours = numOfHoursSO2.get(id);
            Integer totalHIHours = numOfHoursHI.get(id);

            totals.add(id + "," + currentMonth + "," +
                    convert2String(preSubNoxTotal) + "," + convert2String(postSubNoxTotal) + "," + convertDiff2String(preSubNoxTotal, postSubNoxTotal) + "," + convert2String(totalNOXHours) + "," +
                    convert2String(preSubSO2Total) + "," + convert2String(postSubSO2Total) + "," + convertDiff2String(preSubSO2Total, postSubSO2Total) + "," + convert2String(totalSO2Hours) + "," +
                    convert2String(preSubHITotal) + ","  + convert2String(postSubHITotal) + "," + convertDiff2String(preSubHITotal, postSubHITotal) + "," + convert2String(totalHIHours) + "\n");
        }

        return totals;
    }

    private String convert2String(Object num) {
        if (num == null) return "";

        return num.toString();
    }

    private String convertDiff2String(Float num1, Float num2) {
        if (num1 == null && num2 == null) return "";
        if (num1 == null && num2 != null) return -num2.floatValue() + "";
        if (num1 != null && num2 == null) return num1.toString();

        return (num1.floatValue() - num2.floatValue()) + "";
    }

    public String getWarnings() {
        return this.warnings.toString();
    }

    public void clearMemory() {
        tokenizer = null;
//		annSO2TotalEmission = null;
//		ozSeasonNOXTotalEmission = null;
//		nonOzSeasonNOXTotalEmission = null;
        this.periodNOXTotalEmission = null;
        this.periodSO2TotalEmission = null;
        heatInputs = null;
//		badHI4SO2 = null;
//		badHI4NOXNonOz = null;
//		badHI4NOXOz = null;
        this.periodBadHI4NOX = null;
        this.periodBadHI4SO2 = null;
        eval = null;
        preSubNoxMTotal = null;
        postSubNoxMTotal = null;
        preSubSO2MTotal = null;
        postSubSO2MTotal = null;
        preSubHeatInputTotal = null;
        postSubHeatInputTotal = null;
    }

    public void setEventMap(Map<String,CemEvent> eventMap) throws Exception{
        this.eventMap = eventMap;
        this.calculatePeriods();
        this.init();
    }

    public Map<String,CemEvent> getEventMap() {
        return eventMap;
    }

    private void calculatePeriods() throws Exception{
        if ( this.eventMap == null) {
            return;
        }
        Collection<CemEvent> eventSet = eventMap.values();
        int numSO2 = 1;
        int numNOx = 2;
        for (CemEvent event : eventSet) {
            numSO2 = CemEvent.getNumPeriodsSO2(event);
            numNOx = CemEvent.getNumPeriodsNOx(event);
            if ( numSO2 > this.numPeriodsSO2) {
                this.numPeriodsSO2 = numSO2;
            }
            if ( numNOx > this.numPeriodsNOx) {
                this.numPeriodsNOx = numNOx;
            }
        }

        //System.out.println( "So2 periods: " + this.numPeriodsSO2 + "; NOx peridods: " + this.numPeriodsNOx);
    }

    private void init() {
        this.periodNOXTotalEmission = new ArrayList<ConcurrentHashMap<String, String>>();
        this.periodSO2TotalEmission = new ArrayList<ConcurrentHashMap<String, String>>();
        this.periodBadHI4NOX = new ArrayList<HashMap<String,String>>();
        this.periodBadHI4SO2 = new ArrayList<HashMap<String,String>>();
        for ( int i = 0; i < this.numPeriodsNOx; i++) {
            this.periodNOXTotalEmission.add( new ConcurrentHashMap<String,String>());
            this.periodBadHI4NOX.add(new HashMap<String,String>());
        }
        for ( int i = 0; i < this.numPeriodsSO2; i++) {
            this.periodSO2TotalEmission.add( new ConcurrentHashMap<String,String>());
            this.periodBadHI4SO2.add(new HashMap<String,String>());
        }
    }

    private ConcurrentHashMap<String,String> getPeriodNOXTotalEmission(CemEvent event, int month, int day) throws Exception {
        if ( this.periodNOXTotalEmission == null) {
            throw new Exception("Please initialize the Calculator first!");
        }
        int index = CemEvent.getPeriodIndexNOx(event, month, day);
        return periodNOXTotalEmission.get(index);
    }

    private ConcurrentHashMap<String,String> getPeriodSO2TotalEmission(CemEvent event, int month, int day) throws Exception {
        if (this.periodSO2TotalEmission == null) {
            throw new Exception("Please initialize the Calculator first!");
        }
        int index = CemEvent.getPeriodIndexSO2(event, month, day);
        return periodSO2TotalEmission.get(index);
    }

    private HashMap<String,String> getPeriodBadHI4NOX(CemEvent event, int month, int day) throws Exception {
        if ( this.periodBadHI4NOX == null) {
            throw new Exception("Please initialize the Calculator first!");
        }
        int index = CemEvent.getPeriodIndexNOx(event, month, day);
        return periodBadHI4NOX.get(index);
    }

    private HashMap<String,String> getPeriodBadHI4SO2(CemEvent event, int month, int day) throws Exception {
        if ( this.periodBadHI4SO2 == null) {
            throw new Exception("Please initialize the Calculator first!");
        }
        int index = CemEvent.getPeriodIndexSO2(event, month, day);
        return periodBadHI4SO2.get(index);
    }

    public static void main(String[] args) {
//		Calculator cal = new Calculator(null);
//		cal.testIndexing();
    }
}
