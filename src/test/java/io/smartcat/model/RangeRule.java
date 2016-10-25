package io.smartcat.model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.assertj.core.util.Lists;

public final class RangeRule implements Rule<Long> {
	// TODO Replace Long argument with generic parameter <T extends Comparable> on order to allow other numeric types
	
	private boolean exclusive;
	private String fieldName;
	
	// definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
	// or [a,b,c] : a<b<c is a set of ranges: {[a,b), [c, infinity)} ? should this be currently allowed?
	private List<Long> ranges = Lists.newArrayList();
	
	private RangeRule() {};
	
	public static RangeRule withRanges(String fieldName, Long... rangeMarkers) {
		RangeRule result = new RangeRule();
		
		result.fieldName = fieldName;
		result.ranges.addAll(Lists.newArrayList(rangeMarkers));
		
		return result;
	}
	
	public static RangeRule withRangesX(String fieldName, Long... rangeMarkers) {
		RangeRule result = new RangeRule();
		
		result.fieldName = fieldName;
		result.exclusive = true;
		result.ranges.addAll(Lists.newArrayList(rangeMarkers));
		
		return result;
	}
	
	public static RangeRule withRanges(String fieldName, List<Long> rangeMarkers) {
		RangeRule result = new RangeRule();
		
		result.fieldName = fieldName;
		result.ranges.addAll(Lists.newArrayList(rangeMarkers));
		
		return result;
	}

	@Override
	public boolean isExclusive() {
		return this.exclusive;
	}

	private List<Long> getAllowedRanges() {
		return ranges;
	}
	
	@Override
	public Rule recalculatePrecedance(Rule exclusiveRule) {
		if (!exclusiveRule.isExclusive()) {
			throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
		}
		if (! (exclusiveRule instanceof RangeRule)) {
			throw new IllegalArgumentException("cannot compare discrete and range rules");
		}
		RangeRule otherRule = (RangeRule) exclusiveRule;
		
		if (!rangesIntersects(this.ranges, otherRule.getAllowedRanges())) {
			return this;
		}
		List<Long> newRanges = recalculateRanges(otherRule.getAllowedRanges());
		
		return RangeRule.withRanges(fieldName, newRanges);
	}
	
	private boolean rangesIntersects(List<Long> range1, List<Long> range2) {
		return range1.get(0) <= range2.get(1) && range2.get(0) <= range1.get(1);
	}

	private List<Long> recalculateRanges(List<Long> exclusiveRanges) {
		
		Long x1 = this.ranges.get(0);
		Long x2 = this.ranges.get(1);
		Long y1 = exclusiveRanges.get(0);
		Long y2 = exclusiveRanges.get(1);
		
		if (y1 <= x1 && x2 <= y2) { // 1.
			// ----x1----------x2----
			// -y1---------------y2--
			// -y1-------------y1----
			// ----y1------------y2--
			// ----y1----------y2----
			return Lists.newArrayList();
		}
		
		if (x1 < y1 && y2 < x2) { // 2.
			// ----x1----------x2----
			// --------y1--y2--------
			return Lists.newArrayList(x1,y1,y2,x2);
		}
		
		if (y1 <= x1 ) { // x2 > y2, otherwise 1.
			// ----x1----------x2----
			// -y1--------y2---------  <
			// ----y1-----y2---------  =
			return Lists.newArrayList(y2,x2);
		}
		
		if (x2 <= y2) { // x1 < y1 otherwise 1.
			// ----x1----------x2----
			// --------y1--------y2-- 
			return Lists.newArrayList(x1,y1);
		}
		
		throw new IllegalStateException("Unexpected error: x1=" + x1 + ", x2=" +x2+ ", y1="+y1+", y2="+y2 );
	}

	@Override
	public Long getRandomAllowedValue() {
		int randomRangeIndex = ThreadLocalRandom.current().nextInt(0, ranges.size() - 1);
		System.out.println("size is: " + ranges.size());
		System.out.println("randomRangeIdex is: " + randomRangeIndex);
		Long randomBirthDate = ThreadLocalRandom.current().nextLong(ranges.get(randomRangeIndex), ranges.get(randomRangeIndex+1));
		return randomBirthDate;
	}
	
}
