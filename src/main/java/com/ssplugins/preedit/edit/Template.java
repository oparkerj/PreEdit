package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.util.ShiftList;

import java.util.Collections;
import java.util.List;

public class Template {
	
	private String name;
	
	private ShiftList<Module> modules = new ShiftList<>();
	
	public Template(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Module> getModules() {
		return Collections.unmodifiableList(modules);
	}
	
	public void addModule(Module module) {
		modules.add(module);
	}
	
	public void removeModule(int i) {
		modules.remove(i);
	}
	
}
