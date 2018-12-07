package edu.unc.cem.correct;

public class CemEvaluator {
	
	private String rule;
	
	public int compare(Float one, Float two) {
		return one.compareTo(new Float(two.floatValue() * 2));
	}
	
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getRule() {
		return rule;
	}

}
