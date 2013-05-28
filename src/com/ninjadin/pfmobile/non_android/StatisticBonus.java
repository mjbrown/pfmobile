package com.ninjadin.pfmobile.non_android;

import java.util.List;


public class StatisticBonus {
	private String value;
	private List<StatisticInstance> dependentUpon;
	public String stackType;
	public String source;
	private boolean isActive;
	
	public StatisticBonus(String stack, String src, String val) {
		this.stackType = stack;
		this.source = src;
		this.isActive = true;
		setValue(val);
	}
	public StatisticBonus(String stack, String src, String val, List<StatisticInstance> depends) {
		this.stackType = stack;
		this.source = src;
		this.isActive = true;
		setValue(val);
		this.dependentUpon = depends;
	}
	private void setValue(String val) {
		this.value = val;
	}
	public void disable() {
		this.isActive = false;
	}
	public void enable() {
		this.isActive = true;
	}
	public int getValue() {
		if (isActive == false)
			return 0;
		String expression = new String(this.value);
		if (dependentUpon != null) {
			for (StatisticInstance dep: dependentUpon) {
				int dependencyValue = dep.getFinalValue();
				String dependencyName = dep.getName();
				expression = expression.replace(dependencyName, Integer.toString(dependencyValue));
			}
		} else {
			expression = value;
		}
		return evaluate(expression);
	}
	// Basic support for min, max, /, *, +, - ONLY
	final static public int evaluate(String expression) {
		String[] tokens = expression.split(" ");
		int retValue = 0;
		String lastOperator = "+";
		if (tokens[0].equals("min")) {
			int minimum = Integer.MAX_VALUE;
			for (int i=1; i < tokens.length; i++) {
				int newMin = Integer.parseInt(tokens[i]);
				if (newMin < minimum)
					minimum = newMin;
			}
			return minimum;
		} else if (tokens[0].equals("max")) {
			int maximum = Integer.MIN_VALUE;
			for (int i=1; i < tokens.length; i++) {
				int newMax = Integer.parseInt(tokens[i]);
				if (newMax > maximum)
					maximum = newMax;
			}
			return maximum;
		}
		for (int i = 0; i < tokens.length; i++) {
			try {
				int nextValue = Integer.parseInt(tokens[i]);
				if (lastOperator.equals("+")) {
					retValue += nextValue;
				} else if (lastOperator.equals("-")) {
					retValue -= nextValue;
				} else if (lastOperator.equals("/")) {
					retValue /= nextValue;
				} else if (lastOperator.equals("*")) {
					retValue *= nextValue;
				}
			} catch (NumberFormatException e) {
				lastOperator = tokens[i];
			}
		}
		return retValue;
	}
}
