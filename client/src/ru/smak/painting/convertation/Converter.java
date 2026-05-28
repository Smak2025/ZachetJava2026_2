package ru.smak.painting.convertation;

public class Converter {
    private double xMin, xMax, yMin, yMax;
    private int width = 1, height = 1;

    public Converter(double xMin, double xMax, double yMin, double yMax) {
        setXRange(xMin, xMax);
        setYRange(yMin, yMax);
    }

    public double xCrt2Scr(double x) {
        return (x - xMin) * width / (xMax - xMin);
    }
    public double yCrt2Scr(double y) {
        return height - (y - yMin) * height / (yMax - yMin);
    }
    public double xScr2Crt(double xScr) {
        return xMin + xScr * (xMax - xMin) / width;
    }
    public double yScr2Crt(double yScr) {
        return yMin + (height - yScr) * (yMax - yMin) / height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Некорректная ширина");
        }
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Некорректная высота");
        }
        this.height = height;
    }

    public void setXRange(double xMin, double xMax){
        if (Math.abs(xMin - xMax) < 1e-15) {
            throw new IllegalArgumentException("Границы не могут совпадать");
        }
        this.xMin = Math.min(xMin, xMax);
        this.xMax = Math.max(xMin, xMax);
    }

    public void setYRange(double yMin, double yMax){
        if (Math.abs(yMin - yMax) < 1e-15) {
            throw new IllegalArgumentException("Границы не могут совпадать");
        }
        this.yMin = Math.min(yMin, yMax);
        this.yMax = Math.max(yMin, yMax);
    }
}