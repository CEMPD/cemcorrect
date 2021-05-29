package edu.unc.cem.correct;

public class CemEvaluator {

    private String rule;
    private final double peakFactor;

    public CemEvaluator() {
        String factorStr = System.getenv("PEAKFACTOR");
        this.peakFactor = Double.parseDouble(factorStr);
    }

    public int compare(Float one, Float two) {
        return one.compareTo(new Float(two.floatValue() * this.peakFactor));
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

}
