package com.ssplugins.preedit.util.wrapper;

import com.sun.javafx.collections.ObservableListWrapper;

import java.util.ArrayList;

public class ShiftList<T> extends ObservableListWrapper<T> {
	
	public ShiftList() {
		super(new ArrayList<>());
	}
	
	public int shiftElement(int i, boolean forward) {
		if (forward && i == size() - 1) return -1; //throw new IllegalArgumentException("Cannot shift last element forward.");
		if (!forward && i == 0) return -1; //throw new IllegalArgumentException("Cannot shift last element backwards.");
		T t = this.remove(i);
		int n = i + (forward ? 1 : -1);
		this.add(n, t);
		return n;
	}
	
	public int shiftForward(int i) {
		return shiftElement(i, true);
	}
	
	public int shiftBackward(int i) {
		return shiftElement(i, false);
	}
	
}
