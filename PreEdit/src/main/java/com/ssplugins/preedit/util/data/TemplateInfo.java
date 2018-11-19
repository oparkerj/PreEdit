package com.ssplugins.preedit.util.data;

public class TemplateInfo {
    
    private String name;
    private int width, height;
    
    public TemplateInfo(String name, int width, int height) {
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
    
}
