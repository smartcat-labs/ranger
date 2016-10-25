package io.smartcat.model;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BuildRunner {
	
	// set builder-a. prodje kroz sve builder-e i proveri koji ima exclusive rule.
	// recalculates vrednosti za druge builder-e na osnovu exclusive rule-a.
	
	// npr: ako jedan builder ima exclusive rule da kreira korisnika sa username iz skupa: ["wulverine", "prof x"], prodje kroz
	// ostale buider-e i izbaci usernames 'wulverine' i "prof x" iz skupa mogucih vrednosti za taj builder.4
	
	// npr2: ako jedan builder ima exclusive rule da kreira korisnika iz birth date range [2001,2003), buildRunner
	// prodje kroz ostale builder-e i izracuna nove range-ove ako je potrebno: [1980,2010), bice: [1980, 2001), [2003,2010).
	
	private final Set<RandomBuilder> builderSet = Sets.newHashSet();
	
	public List<User> build() {
		recalculateRules();
		List<User> resultList = Lists.newArrayList();
		for (RandomBuilder builder : builderSet) {
			List<User> entityList = builder.buildAll();
			resultList.addAll(entityList);
		}
		return resultList;
	}
	
	public void addBuilder(RandomBuilder builder) {
		builderSet.add(builder);
	}
	
	// recalculates rules by taking into account exclusive rules
	private void recalculateRules() {
		// TODO
		
//		for (RandomBuilder randomBuilder : builderSet) {
//			
//			for (Entry<String, Rule> entry : randomBuilder.getFieldRules().entrySet()) {
//				String fieldName = entry.getKey();
//				Rule rule = entry.getValue();
//				if (rule.isExclusive()) {
//				}
//			}
//		}
	}

}
