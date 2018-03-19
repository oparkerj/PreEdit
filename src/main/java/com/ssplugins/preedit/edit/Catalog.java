package com.ssplugins.preedit.edit;

import java.util.*;

public class Catalog {
	
	private List<Template> templates = new ArrayList<>();
	private Map<String, Class<? extends Module>> modules = new HashMap<>();
	private Map<String, Class<? extends Effect>> effects = new HashMap<>();
	
	public Template newTemplate(String name) {
		Template t = new Template(name);
		templates.add(t);
		return t;
	}
	
	public boolean templateExists(String name) {
		return templates.stream().anyMatch(template -> template.getName().equalsIgnoreCase(name));
	}
	
	public Optional<Template> findTemplate(String name) {
		return templates.stream().filter(template -> template.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	public List<Template> getTemplates() {
		return Collections.unmodifiableList(templates);
	}
	
	public void registerModule(String id, Class<? extends Module> type) {
		modules.put(id, type);
	}
	
	public void registerEffect(String id, Class<? extends Effect> type) {
		effects.put(id, type);
	}
	
	public Optional<Class<? extends Module>> findModule(String name) {
		return Optional.ofNullable(modules.get(name));
	}
	
	public Optional<Class<? extends Effect>> findEffect(String name) {
		return Optional.ofNullable(effects.get(name));
	}
	
	public Optional<Module> createModule(String name) {
		return findModule(name).flatMap(aClass -> {
			try {
				return Optional.of(aClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				return Optional.empty();
			}
		});
	}
	
	public Optional<Effect> createEffect(String name) {
		return findEffect(name).flatMap(aClass -> {
			try {
				return Optional.of(aClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				return Optional.empty();
			}
		});
	}
	
}
