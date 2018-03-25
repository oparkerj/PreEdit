package com.ssplugins.preedit.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;

public class SizeHandler {
	
	private DoubleProperty outX, outY, outWidth, outHeight;
	
	private double startX, startY, ix, iy, iw, ih;
	private Pos anchor;
	
	public SizeHandler() {
		outX = new SimpleDoubleProperty();
		outY = new SimpleDoubleProperty();
		outWidth = new SimpleDoubleProperty();
		outHeight = new SimpleDoubleProperty();
	}
	
	public void begin(double startX, double startY, Pos anchor) {
		this.startX = startX;
		this.startY = startY;
		this.anchor = anchor;
		ix = outX.get();
		iy = outY.get();
		iw = outWidth.get();
		ih = outHeight.get();
	}
	
	public void update(double mx, double my) {
		double dx = mx - startX;
		double dy = my - startY;
		if (anchor == Pos.CENTER) {
			move(dx, dy);
		}
		else if (anchor == Pos.TOP_LEFT) {
			move(dx, dy);
			size(-dx, -dy);
		}
		else if (anchor == Pos.BOTTOM_RIGHT) {
			size(dx, dy);
		}
		else if (anchor == Pos.TOP_RIGHT) {
			moveY(dy);
			size(dx, -dy);
		}
		else if (anchor == Pos.BOTTOM_LEFT) {
			moveX(dx);
			size(-dx, dy);
		}
		else if (anchor == Pos.TOP_CENTER) {
			moveY(dy);
			sizeH(-dy);
		}
		else if (anchor == Pos.CENTER_LEFT) {
			moveX(dx);
			sizeW(-dx);
		}
		else if (anchor == Pos.CENTER_RIGHT) {
			sizeW(dx);
		}
		else if (anchor == Pos.BOTTOM_CENTER) {
			sizeH(dy);
		}
	}
	
	private void move(double dx, double dy) {
		moveX(dx);
		moveY(dy);
	}
	
	private void moveX(double dx) {
		outX.set(ix + dx);
	}
	
	private void moveY(double dy) {
		outY.set(iy + dy);
	}
	
	private void size(double dw, double dh) {
		sizeW(dw);
		sizeH(dh);
	}
	
	private void sizeW(double dw) {
		outWidth.set(clamp(iw + dw));
	}
	
	private void sizeH(double dh) {
		outHeight.set(clamp(ih + dh));
	}
	
	private double clamp(double v) {
		if (v < 1) return 1;
		return v;
	}
	
	public double getOutX() {
		return outX.get();
	}
	
	public DoubleProperty outXProperty() {
		return outX;
	}
	
	public double getOutY() {
		return outY.get();
	}
	
	public DoubleProperty outYProperty() {
		return outY;
	}
	
	public double getOutWidth() {
		return outWidth.get();
	}
	
	public DoubleProperty outWidthProperty() {
		return outWidth;
	}
	
	public double getOutHeight() {
		return outHeight.get();
	}
	
	public DoubleProperty outHeightProperty() {
		return outHeight;
	}
	
}
