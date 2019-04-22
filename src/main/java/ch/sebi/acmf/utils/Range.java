package ch.sebi.acmf.utils;

/**
 * Created by Sebastian on 01.07.2017.
 */
public class Range {
    private int min, max;

    private Range() {
        this.min = 0;
        this.max = 0;
    }


    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean overlap(Range r) {
        return overlap(r, false);
    }

    private boolean overlap(Range r, boolean alreadyCalled) {
        if(contains(r.min)) return true;
        if(contains(r.max)) return true;
        if(!alreadyCalled)
            return r.overlap(this, true);
        return false;
    }

    public boolean contains(int v) {
        return (min < v) && (max > v);
    }

    public static Range range(int min, int max) {
        return new Range(min, max);
    }


}
