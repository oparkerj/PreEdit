package com.ssplugins.preedit.util;

public class Range {
	
	private double lower;
	private double upper;
	private Type type;
	
	private Range(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		type = Type.DEFAULT;
	}
	
	private Range type(Type type) {
		this.type = type;
		return this;
	}
	
	public static Range from(double lower, double upper) {
		if (lower > upper) throw new IllegalArgumentException("Lower bound is greater than upper bound.");
		return new Range(lower, upper);
	}
	
	public static Range single(double number) {
		return new Range(number, 0).type(Type.SINGLE);
	}
	
	public static Range outside(double lower, double upper) {
		return from(lower, upper).type(Type.OUTSIDE);
	}
	
	public static Range lowerBound(double bound) {
		return new Range(bound, 0).type(Type.LOWER_BOUND);
	}
	
	public static Range upperBound(double bound) {
		return new Range(0, bound).type(Type.UPPER_BOUND);
	}
	
	public static Range any() {
		return new Range(0, 0).type(Type.ANY);
	}
	
	public static double clamp(double n, double lower, double upper) {
		return Range.from(lower, upper).clamp(n);
	}
	
	public static double clampMin(double n, double min) {
		return Range.lowerBound(min).clamp(n);
	}
	
	public static double clampMax(double n, double max) {
		return Range.upperBound(max).clamp(n);
	}
	
	public double getLowerBound() {
		return lower;
	}
	
	public double getUpperBound() {
		return upper;
	}
	
	public boolean inRange(double value) {
		if (type == Type.ANY) return true;
		if (type == Type.SINGLE) return value == lower;
		if (type == Type.OUTSIDE) {
			return value < lower || value > upper;
		}
		if (type == Type.LOWER_BOUND) return value >= lower;
		if (type == Type.UPPER_BOUND) return value <= upper;
		else return lower <= value && value <= upper;
	}
	
	public boolean outsideRange(double value) {
		return !inRange(value);
	}
	
	public double clamp(double value) {
		if (inRange(value)) return value;
		if (type == Type.SINGLE) return lower;
		if (type == Type.OUTSIDE) {
			if (Math.abs(value - lower) < Math.abs(value - upper)) {
				return lower;
			}
			return upper;
		}
		if (type == Type.LOWER_BOUND) {
			return lower;
		}
		if (type == Type.UPPER_BOUND) {
			return upper;
		}
		if (value < lower) return lower;
		if (value > upper) return upper;
		return value;
	}
	
	private enum Type {
		DEFAULT,
		SINGLE,
		OUTSIDE,
		LOWER_BOUND,
		UPPER_BOUND,
		ANY
	}
	
	@Override
	public String toString() {
		if (type == Type.ANY) return "any";
		if (type == Type.SINGLE) return String.valueOf(lower);
		if (type == Type.OUTSIDE) return lower + ">n>" + upper;
		if (type == Type.LOWER_BOUND) return ">=" + lower;
		if (type == Type.UPPER_BOUND) return "<=" + upper;
		else return lower + "-" + upper;
	}
}
