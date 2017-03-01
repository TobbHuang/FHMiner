package org.fhm.heuristics.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DGNode {

    public int index;

    public Set<DGNode> inputSet;

    public Set<DGNode> outputSet;

    public DGNode(int index) {
        this.index = index;
        inputSet = new HashSet<>();
        outputSet = new HashSet<>();
    }

    public void addInput(DGNode input) {
        inputSet.add(input);
        //        System.out.println(index + " input: " + input.index);
    }

    public void addOutput(DGNode output) {
        outputSet.add(output);
//                System.out.println(index + " output: " + output.index);
    }
}
