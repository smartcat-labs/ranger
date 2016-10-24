package io.smartcat.model;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DiscreteRule {
	
	private boolean exclusive;
	private String fieldName;
	
	private final List<String> usernames = Lists.newArrayList();
	
	private DiscreteRule() {};
	
	public static DiscreteRule newSet(String fieldName, String... allowedUsernames) {
		DiscreteRule result = new DiscreteRule();
		
		result.fieldName = fieldName;
		result.usernames.addAll(Lists.newArrayList(allowedUsernames));
		
		return result;
	}
	
	public static DiscreteRule newSetExclusive(String fieldName, String... allowedUsernames) {
		DiscreteRule result = new DiscreteRule();
		
		result.exclusive = true;
		result.fieldName = fieldName;
		result.usernames.addAll(Lists.newArrayList(allowedUsernames));
		
		return result;
	}

	public boolean isExclusive() {
		return exclusive;
	}
	
	public List<String> getAllowedUsernames() {
		return this.usernames;
	}

}
