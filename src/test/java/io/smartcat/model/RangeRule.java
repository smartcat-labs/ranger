package io.smartcat.model;

import java.util.List;

import org.assertj.core.util.Lists;

import com.google.common.collect.Range;

public final class RangeRule {
	
	private boolean exclusive;
	
	private long birthDateStartRange;
	private long birthDateEndRange;
	
	private String fieldName;
	
	private final List<Range<Long>> birthdateRanges = Lists.newArrayList();
	
	// definition of the range: e.g [a,b,c,d] : a <b<c<d is a range: {[a,b),[c,d)}
	// or [a,b,c] : a<b<c is a range: {[a,b), [c, infinity)} 
	private List<Long> ranges = Lists.newArrayList();
	
	private RangeRule() {};
	
	public static RangeRule withRanges(String fieldName, Long... rangeMarkers) {
		
		RangeRule result = new RangeRule();
		
		result.fieldName = fieldName;
		result.ranges.addAll(Lists.newArrayList(rangeMarkers));
		
		return result;
	}

	public boolean isExclusive() {
		// TODO Auto-generated method stub
		return this.exclusive;
	}

	public List<Long> getRanges() {
		return ranges;
	}
	
}
