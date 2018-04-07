package com.ssplugins.preedit.util;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class SafePixelWriter implements PixelWriter {
	
	private int w, h;
	private PixelWriter writer;
	
	public SafePixelWriter(WritableImage img) {
		w = (int) img.getWidth();
		h = (int) img.getHeight();
		writer = img.getPixelWriter();
	}
	
	private boolean inside(int x, int y) {
		return x >= 0 && x < w && y >= 0 && y < h;
	}
	
	private Box fixBounds(int x, int y, int w, int h) {
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (!inside(x, y)) return null;
		if (x + w > this.w) w = this.w - x;
		if (y + h > this.h) h = this.h - y;
		return new Box(x, y, w, h);
	}
	
	@Override
	public PixelFormat getPixelFormat() {
		return writer.getPixelFormat();
	}
	
	@Override
	public void setArgb(int x, int y, int argb) {
		if (!inside(x, y)) return;
		writer.setArgb(x, y, argb);
	}
	
	@Override
	public void setColor(int x, int y, Color c) {
		if (!inside(x, y)) return;
		writer.setColor(x, y, c);
	}
	
	@Override
	public <T extends Buffer> void setPixels(int x, int y, int w, int h, PixelFormat<T> pixelformat, T buffer, int scanlineStride) {
		writer.setPixels(x, y, w, h, pixelformat, buffer, scanlineStride);
	}
	
	@Override
	public void setPixels(int x, int y, int w, int h, PixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
		writer.setPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
	}
	
	@Override
	public void setPixels(int x, int y, int w, int h, PixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
		writer.setPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
	}
	
	@Override
	public void setPixels(int dstx, int dsty, int w, int h, PixelReader reader, int srcx, int srcy) {
		writer.setPixels(dstx, dsty, w, h, reader, srcx, srcy);
	}
	
	private class Box {
		private int x, y, w, h;
		
		public Box(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
	
}
