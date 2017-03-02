package org.fhm.heuristics.model.dg;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DGNode {

    public static final int SEARCH_INPUT_SET = 0;
    public static final int SEARCH_OUTPUT_SET = 1;

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
    }

    public void addOutput(DGNode output) {
        outputSet.add(output);
    }

    public DGNode contains(int searchIndex, int searchSetType) {
        Iterator<DGNode> iterator;
        if (searchSetType == SEARCH_INPUT_SET) {
            iterator = inputSet.iterator();
        } else {
            iterator = outputSet.iterator();
        }
        while (iterator.hasNext()) {
            DGNode node = iterator.next();
            if (searchIndex == node.index - 1) {
                return node;
            }
        }
        return null;
    }

}
