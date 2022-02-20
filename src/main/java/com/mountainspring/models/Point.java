package com.mountainspring.models;

import java.util.ArrayList;

public class Point extends ArrayList<Double> {

    public Point() {
        super(0);
    }

    public Point(Double[] initialElements) {
        super(initialElements.length);
        if (initialElements.length > 2) {
            throw new UnsupportedOperationException("Must have no more than two items");
        }
        for (Double loopElement : initialElements) {
            super.add(loopElement);
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Elements may not be cleared from a fixed size List.");
    }

    @Override
    public boolean add(Double o) {
        if (this.size() > 1) {
            throw new UnsupportedOperationException("Must have no more than two items");
        } else {
            return super.add(o);
        }
    }

    @Override
    public void add(int index, Double element) {
        if (this.size() > 1 || index > 1) {
            throw new UnsupportedOperationException("Must have no more than two items");
        } else {
            super.set(index, element);
        }
    }

    @Override
    public Double remove(int index) {
        throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
    }
}