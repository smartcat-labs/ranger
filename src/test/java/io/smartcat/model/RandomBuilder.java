package io.smartcat.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;

import com.google.common.collect.Maps;

public class RandomBuilder {
	
	private final Map<String,Rule> fieldRules = Maps.newHashMap();
	
	private int numberOfUsers;
	
	public RandomBuilder randomUsernameFrom(String... usernames) {
		fieldRules.put("username", DiscreteRule.newSet("username", usernames));
		return this;
	}
	
	public RandomBuilder randomUsernameFromX(String... usernames) {
		fieldRules.put("username", DiscreteRule.newSetExclusive("username", usernames));
		return this;
	}
	
	public RandomBuilder randomBirthDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
		long lower = startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		long upper = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		
		fieldRules.put("birthDate", RangeRule.withRanges("birthDate", lower, upper));
		
		return this;
	}
	
	public RandomBuilder randomBirthDateBetweenX(LocalDateTime startDate, LocalDateTime endDate) {
		long lower = startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		long upper = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		
		fieldRules.put("birthDate", RangeRule.withRangesX("birthDate", lower, upper));
		
		return this;
	}
	
	public RandomBuilder toBeBuilt(int numbeerOfUsers) {
		this.numberOfUsers = numbeerOfUsers;
		return this;
	}
	
	public User build(User user) {
		return null;
	}
	
	public List<User> build(long numberOfUsersToBuild) {
		List<User> result = Lists.newArrayList();
		for (long i = 1; i <= numberOfUsersToBuild; i++) {
			User randomUser = buildRandom();
			result.add(randomUser);
		}
		
		return result;
	}
	
	private User buildRandom() {
		User user = new User();
		
//		List<String> allowedUsernames = fieldRules.get("username").getAllowedValues();
//		int randomIndex = ThreadLocalRandom.current().nextInt(0, allowedUsernames.size());
//		user.setUsername(allowedUsernames.get(randomIndex));
//		
//		
//		List<Long> birthdateRanges = birthDateRule.getAllowedRanges();
//		// A1, A2, ... , An; 
//		//=> 
//		// { [A1,A2), [A2,A3), ... , [An-1, An) }
//		//      0        1       		 n-1
//		// 	
//		int randomRangeIndex = ThreadLocalRandom.current().nextInt(0, birthdateRanges.size() - 1);
//		System.out.println("size is: " + birthdateRanges.size());
//		System.out.println("randomRangeIdex is: " + randomRangeIndex);
//		Long randomBirthDate = ThreadLocalRandom.current().nextLong(birthdateRanges.get(randomRangeIndex), birthdateRanges.get(randomRangeIndex+1));
//
//		Instant instant =
//			    Instant.ofEpochMilli(randomBirthDate).atZone(ZoneId.systemDefault()).toInstant();
//		user.setBirthDate(Date.from(instant));
		
		String randomUsername = (String) fieldRules.get("username").getRandomAllowedValue();
		Long randomBirthDate = (Long) fieldRules.get("birthDate").getRandomAllowedValue();
		
		Instant instant = Instant.ofEpochMilli(randomBirthDate).atZone(ZoneId.systemDefault()).toInstant();
		
		user.setUsername(randomUsername);
		user.setBirthDate(Date.from(instant));
		
		return user;
	}

	public List<User> buildAll() {
		return this.build(numberOfUsers);
	}

	public Map<String, Rule> getFieldRules() {
		return fieldRules;
	}
	
}
