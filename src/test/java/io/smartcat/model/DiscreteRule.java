package io.smartcat.model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;

public class DiscreteRule implements Rule<String> {
	// TODO replace String argument in generic parameter with something more generic, e.g. <T extends Comparable>
	
	private boolean exclusive;
	private String fieldName;
	
	private final List<String> allowedValues = Lists.newArrayList();
	
	private DiscreteRule() {};
	
	public static DiscreteRule newSet(String fieldName, String... allowedUsernames) {
		DiscreteRule result = new DiscreteRule();
		
		result.fieldName = fieldName;
		result.allowedValues.addAll(Lists.newArrayList(allowedUsernames));
		
		return result;
	}
	
	public static DiscreteRule newSet(String fieldName, List<String> allowedUsernames) {
		DiscreteRule result = new DiscreteRule();
		
		result.fieldName = fieldName;
		result.allowedValues.addAll(Lists.newArrayList(allowedUsernames));
		
		return result;
	}
	
	public static DiscreteRule newSetExclusive(String fieldName, String... allowedUsernames) {
		DiscreteRule result = new DiscreteRule();
		
		result.exclusive = true;
		result.fieldName = fieldName;
		result.allowedValues.addAll(Lists.newArrayList(allowedUsernames));
		
		return result;
	}

	@Override
	public boolean isExclusive() {
		return exclusive;
	}
	
	public List<String> getAllowedValues() {
		return this.allowedValues;
	}

	@Override
	public Rule recalculatePrecedance(Rule exclusiveRule) {
		if (!exclusiveRule.isExclusive()) {
			throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
		}
		if (! (exclusiveRule instanceof DiscreteRule)) {
			throw new IllegalArgumentException("cannot compare discrete and range rules");
		}
		DiscreteRule otherRule = (DiscreteRule) exclusiveRule;
		
		allowedValues.removeAll(otherRule.getAllowedValues());
		
		return DiscreteRule.newSet(fieldName, allowedValues);
	}

	@Override
	public String getRandomAllowedValue() {
		int randomIndex = ThreadLocalRandom.current().nextInt(0, allowedValues.size());
		String value = allowedValues.get(randomIndex);
		return value;
	}

}
