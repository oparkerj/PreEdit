package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.util.ShiftList;

import java.util.Collections;
import java.util.List;

public abstract class Module extends Layer {
	
	private ShiftList<Effect> effects = new ShiftList<>();
	
	public final List<Effect> getEffects() {
		return Collections.unmodifiableList(effects);
	}
	
	public final void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	public final void removeEffect(int i) {
		effects.remove(i);
	}
	
	public final void shiftEffect(int i, boolean up) {
		effects.shiftElement(i, !up);
	}
	
}
