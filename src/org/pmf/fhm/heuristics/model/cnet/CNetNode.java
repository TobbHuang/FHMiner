package org.pmf.fhm.heuristics.model.cnet;

import org.pmf.fhm.heuristics.model.dg.DGNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2017/3/1.
 */
public class CNetNode {

    public static final int ADD_INPUT_SET = 0;
    public static final int ADD_OUTPUT_SET = 1;

    public int index;

    public HashSet<CNetDependencySet> inputSet;

    public HashSet<CNetDependencySet> outputSet;

    public CNetNode(int index) {
        this.index = index;
        inputSet = new HashSet<>();
        outputSet = new HashSet<>();
    }

    public void addSubSet(HashSet<DGNode> subset, int addType) {
        if (subset.size() == 0) {
            return;
        }

        HashSet<CNetDependencySet> addSet;
        if (addType == ADD_INPUT_SET) {
            addSet = inputSet;
        } else {
            addSet = outputSet;
        }

        for (CNetDependencySet set : addSet) {
            if (set.equals(subset)) {
                set.increase();
                return;
            }
        }

        CNetDependencySet newSet = new CNetDependencySet();
        Iterator<DGNode> iterator = subset.iterator();
        while (iterator.hasNext()) {
            newSet.add(new CNetNode(iterator.next().index));
        }
        newSet.increase();
        addSet.add(newSet);
    }

    public String toString(List<String> taskNames) {
        String str = "";
        str += "[";

        Iterator<CNetDependencySet> iterator = inputSet.iterator();
        while (iterator.hasNext()) {
            str += iterator.next().toString(taskNames);
        }

        String taskName;
        if (index == 0) {
            taskName = "Start";
        } else if (index == taskNames.size() + 1) {
            taskName = "End";
        } else {
            taskName = taskNames.get(index - 1);
        }
        str += "] " + taskName + " [";

        iterator = outputSet.iterator();
        while (iterator.hasNext()) {
            str += iterator.next().toString(taskNames);
        }

        str += "]";
        return str;
    }
}
