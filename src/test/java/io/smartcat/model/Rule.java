package io.smartcat.model;

public interface Rule<T extends Comparable<T>> {
	
	boolean isExclusive();
	
	Rule<T> recalculatePrecedance(Rule<T> exclusiveRule);
	
	T getRandomAllowedValue();

}
