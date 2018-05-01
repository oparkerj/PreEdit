package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.util.wrapper.ShiftList;

public class Template {
	
	private String name;
	private int width;
	private int height;
	
	private ShiftList<Module> modules = new ShiftList<>();
	
	public Template(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}
	
	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public ShiftList<Module> getModules() {
		return modules;
	}
	
	public void addModule(Module module) {
		modules.add(module);
	}
	
	public void removeModule(Module module) {
		modules.remove(module);
	}
	
	public void removeModule(int i) {
		modules.remove(i);
	}
    
    public void setEditor(boolean editor) {
	    modules.forEach(module -> module.setEditor(editor));
    }
	
	@Override
	public String toString() {
		return name;
	}
}
