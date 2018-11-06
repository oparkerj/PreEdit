package com.ssplugins.preedit.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ExpandableBounds {
    
    private DoubleProperty originalX, originalY, originalWidth, originalHeight;
    
    private DoubleProperty x, y, width, height;
    
    public ExpandableBounds(double x, double y, double width, double height) {
        originalX = new SimpleDoubleProperty(x);
        originalY = new SimpleDoubleProperty(y);
        originalWidth = new SimpleDoubleProperty(width);
        originalHeight = new SimpleDoubleProperty(height);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
    }
    
    public void reset() {
        x.set(getOriginalX());
        y.set(getOriginalY());
        width.set(getOriginalWidth());
        height.set(getOriginalHeight());
    }
    
    public void expand(double top, double right, double bottom, double left) {
        y.set(getY() - top);
        width.set(getWidth() + right + left);
        height.set(getHeight() + bottom + top);
        x.set(getX() - left);
    }
    
    public void crop(double top, double right, double bottom, double left) {
        expand(-top, -right, -bottom, -left);
    }
    
    public double getOriginalX() {
        return originalX.get();
    }
    
    public DoubleProperty originalXProperty() {
        return originalX;
    }
    
    public void setOriginalX(double originalX) {
        this.originalX.set(originalX);
    }
    
    public double getOriginalY() {
        return originalY.get();
    }
    
    public DoubleProperty originalYProperty() {
        return originalY;
    }
    
    public void setOriginalY(double originalY) {
        this.originalY.set(originalY);
    }
    
    public double getOriginalWidth() {
        return originalWidth.get();
    }
    
    public DoubleProperty originalWidthProperty() {
        return originalWidth;
    }
    
    public void setOriginalWidth(double originalWidth) {
        this.originalWidth.set(originalWidth);
    }
    
    public double getOriginalHeight() {
        return originalHeight.get();
    }
    
    public DoubleProperty originalHeightProperty() {
        return originalHeight;
    }
    
    public void setOriginalHeight(double originalHeight) {
        this.originalHeight.set(originalHeight);
    }
    
    public double getX() {
        return x.get();
    }
    
    public DoubleProperty xProperty() {
        return x;
    }
    
    public void setX(double x) {
        this.x.set(x);
    }
    
    public double getY() {
        return y.get();
    }
    
    public DoubleProperty yProperty() {
        return y;
    }
    
    public void setY(double y) {
        this.y.set(y);
    }
    
    public double getWidth() {
        return width.get();
    }
    
    public DoubleProperty widthProperty() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width.set(width);
    }
    
    public double getHeight() {
        return height.get();
    }
    
    public DoubleProperty heightProperty() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height.set(height);
    }
    
}
