package com.ssplugins.preedit.util;

import java.util.ArrayList;

public class ShiftList<T> extends ArrayList<T> {
	
	public void shiftElement(int i, boolean forward) {
		if (forward && i == size() - 1) throw new IllegalArgumentException("Cannot shift last element forward.");
		if (!forward && i == 0) throw new IllegalArgumentException("Cannot shift last element backwards.");
		T t = this.remove(i);
		this.add(i + (forward ? 1 : -1), t);
	}
	
	public void shiftForward(int i) {
		shiftElement(i, true);
	}
	
	public void shiftBackward(int i) {
		shiftElement(i, false);
	}
	
}
