package org.fhm.heuristics.model;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DGPair {

    public int from;

    public int to;

    public DGPair(int fromIndex, int toIndex) {
        from = fromIndex;
        to = toIndex;
    }

    public boolean equals(DGPair pair) {
        if (from == pair.from && to == pair.to) {
            return true;
        }
        return false;
    }

    public DGPair clone() {
        return new DGPair(from, to);
    }

    public void print() {
        System.out.println(from + " -> " + to);
    }

}
