package com.ssplugins.preedit.util.calc;

import com.ssplugins.preedit.util.data.Range;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;

public class SizeHandler {
    
    private DoubleProperty outX, outY, outWidth, outHeight, outAngle;
    
    private double startX, startY, ix, iy, iw, ih, ia, ir;
    private Pos anchor;
    
    public SizeHandler() {
        outX = new SimpleDoubleProperty();
        outY = new SimpleDoubleProperty();
        outWidth = new SimpleDoubleProperty();
        outHeight = new SimpleDoubleProperty();
        outAngle = new SimpleDoubleProperty();
    }
    
    public void beginRotate(Point2D start) {
        begin(start.getX(), start.getY(), Pos.BASELINE_CENTER);
    }
    
    public void begin(Point2D start, Pos anchor) {
        begin(start.getX(), start.getY(), anchor);
    }
    
    public void begin(double startX, double startY, Pos anchor) {
        this.startX = startX;
        this.startY = startY;
        this.anchor = anchor;
        ix = outX.get();
        iy = outY.get();
        iw = outWidth.get();
        ih = outHeight.get();
        ir = outAngle.get();
        ia = angle(startX, startY);
    }
    
    public void update(Point2D mouse, MouseEvent event) {
        update(mouse.getX(), mouse.getY(), event);
    }
    
    public void update(double mx, double my, MouseEvent event) {
        double dx = mx - startX;
        double dy = my - startY;
        if (anchor == Pos.CENTER) {
            move(dx, dy);
            return;
        }
        else if (anchor == Pos.BASELINE_CENTER) {
            double a = angle(mx, my);
            outAngle.set((ir + (a - ia) + 360) % 360);
            return;
        }
        Point2D r = rot(dx, dy, -outAngle.get());
        double cx = r.getX();
        double cy = r.getY();
        if (anchor == Pos.TOP_LEFT) {
            size(-cx, -cy);
            cx = Range.clampMax(cx, iw);
            cy = Range.clampMax(cy, ih);
        }
        else if (anchor == Pos.BOTTOM_RIGHT) {
            size(cx, cy);
            cx = Range.clampMin(cx, -iw);
            cy = Range.clampMin(cy, -ih);
        }
        else if (anchor == Pos.TOP_RIGHT) {
            size(cx, -cy);
            cx = Range.clampMin(cx, -iw);
            cy = Range.clampMax(cy, ih);
        }
        else if (anchor == Pos.BOTTOM_LEFT) {
            size(-cx, cy);
            cx = Range.clampMax(cx, iw);
            cy = Range.clampMin(cy, -ih);
        }
        else if (anchor == Pos.TOP_CENTER) {
            sizeH(-cy);
            cx = 0;
            cy = Range.clampMax(cy, ih);
        }
        else if (anchor == Pos.CENTER_LEFT) {
            sizeW(-cx);
            cx = Range.clampMax(cx, iw);
            cy = 0;
        }
        else if (anchor == Pos.CENTER_RIGHT) {
            sizeW(cx);
            cx = Range.clampMin(cx, -iw);
            cy = 0;
        }
        else if (anchor == Pos.BOTTOM_CENTER) {
            sizeH(cy);
            cx = 0;
            cy = Range.clampMin(cy, -ih);
        }
        Point2D f = rot(cx, cy, outAngle.get());
        dx = f.getX();
        dy = f.getY();
        centerTo(ix + iw / 2 + dx / 2, iy + ih / 2 + dy / 2);
    }
    
    private double getRatioAngle(Pos pos) {
        switch (pos) {
            case TOP_LEFT:
            case BOTTOM_RIGHT:
                return Math.toDegrees(Math.atan2(ih, iw));
            case TOP_RIGHT:
            case BOTTOM_LEFT:
                return Math.toDegrees(Math.atan2(-iw, ih));
            case TOP_CENTER:
            case BOTTOM_CENTER:
                return 0;
            case CENTER_LEFT:
            case CENTER_RIGHT:
                return 90;
            default:
                return 0;
        }
    }
    
    private void move(double dx, double dy) {
        moveX(dx);
        moveY(dy);
    }
    
    private void centerTo(double x, double y) {
        moveTo(x - outWidth.get() / 2, y - outHeight.get() / 2);
    }
    
    private void moveTo(double x, double y) {
        outX.set(x);
        outY.set(y);
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
        double out = clamp(iw + dw);
        outWidth.set(out);
    }
    
    private void sizeH(double dh) {
        double out = clamp(ih + dh);
        outHeight.set(out);
    }
    
    private double clamp(double v) {
        if (v < 1) return 1;
        return v;
    }
    
    private double centerX() {
        return outX.get() + outWidth.get() / 2;
    }
    
    private double centerY() {
        return outY.get() + outHeight.get() / 2;
    }
    
    private double angle(double x, double y) {
        return angle(centerX(), centerY(), x, y);
    }
    
    private double angle(double fx, double fy, double tx, double ty) {
        double dx = tx - fx;
        double dy = ty - fy;
        double v = Math.toDegrees(Math.atan2(dy, dx));
        return (v + 360) % 360;
    }
    
    private Point2D rot(double x, double y, double deg) {
        return rot(x, y, 0, 0, deg);
    }
    
    private Point2D rot(double x, double y, double cx, double cy, double deg) {
        if (deg == 0) return new Point2D(x, y);
        deg = Math.toRadians(deg);
        x -= cx;
        y -= cy;
        double rx = x * Math.cos(deg) - y * Math.sin(deg);
        double ry = y * Math.cos(deg) + x * Math.sin(deg);
        return new Point2D(rx + cx, ry + cy);
    }
    
    private Point2D limitDir(double mx, double my, double offset) {
        double a = Math.toRadians(outAngle.get() + offset);
        return segmentPoint(0, 0, Math.cos(a), Math.sin(a), mx, my);
    }
    
    private Point2D segmentPoint(double sx1, double sy1, double sx2, double sy2, double px, double py) {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;
        if ((xDelta == 0) && (yDelta == 0)) {
            throw new IllegalArgumentException("Segment start equals segment end");
        }
        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
        return new Point2D(sx1 + u * xDelta, sy1 + u * yDelta);
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
    
    public double getOutAngle() {
        return outAngle.get();
    }
    
    public DoubleProperty outAngleProperty() {
        return outAngle;
    }
    
}
