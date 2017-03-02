package org.fhm.heuristics.model.cnet;

import org.fhm.heuristics.model.dg.DGNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2017/3/1.
 * <p>
 * 存储split或join的集合，包括点集和频率
 */
public class CNetDependencySet extends HashSet<CNetNode> {

    int frequency;

    public CNetDependencySet() {
        super();
        frequency = 0;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increase() {
        frequency++;
    }

    public boolean equals(HashSet<DGNode> subset) {
        List<CNetNode> list1 = new ArrayList<>();
        list1.addAll(this);

        List<DGNode> list2 = new ArrayList<>();
        list2.addAll(subset);

        for (int i = 0; i < list2.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < list1.size(); j++) {
                if (list1.get(j).index == list2.get(i).index) {
                    list1.remove(j);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }

        if (list1.size() == 0) {
            return true;
        }

        return false;
    }

    public String toString(List<String> taskNames) {
        String str = "";

        str += "(";

        Iterator<CNetNode> iterator = iterator();
        while (iterator.hasNext()) {
            CNetNode node = iterator.next();
            String taskName;
            if (node.index == 0) {
                taskName = "Start";
            } else if (node.index == taskNames.size() + 1) {
                taskName = "End";
            } else {
                taskName = taskNames.get(node.index - 1);
            }
            str += taskName + ", ";
        }

        str += getFrequency();
        str += ")";

        return str;
    }

}
