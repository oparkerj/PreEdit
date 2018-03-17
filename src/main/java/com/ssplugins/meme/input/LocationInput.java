package com.ssplugins.meme.input;

import com.ssplugins.meme.exceptions.InvalidInputException;
import com.ssplugins.meme.util.GridMap;
import com.ssplugins.meme.util.Util;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class LocationInput extends Input<GridMap, LocationInput.Point> {
	
	public LocationInput() {
		this.ready();
	}
	
	@Override
	protected GridMap createInputNode() {
		GridMap map = new GridMap();
		map.add(null, 0, 0, new Label("x:"));
		map.add("x", 0, 1, new TextField("0"));
		map.add(null, 0, 2, new Label("y:"));
		map.add("y", 0, 3, new TextField("0"));
		return map;
	}
	
	@Override
	protected Point getNodeValue(GridMap node) throws InvalidInputException {
		int x = node.get("x", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
		int y = node.get("y", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
		return new Point(x, y);
	}
	
	@Override
	protected boolean isValid(Point value) {
		return true;
	}
	
	public class Point {
		private int x;
		private int y;
		
		private Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
}
