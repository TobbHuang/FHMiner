package org.fhm.heuristics.model.dg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DG {

    public List<DGNode> dependencyGraphNodes;

    public List<String> taskNames;

    public DG(int taskNum, List<String> taskNames) {
        dependencyGraphNodes = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            dependencyGraphNodes.add(new DGNode(i));
        }
        this.taskNames = taskNames;
    }

    public void print() {
        for (int i = 0; i < dependencyGraphNodes.size(); i++) {
            DGNode node = dependencyGraphNodes.get(i);

            System.out.print("[");
            Iterator<DGNode> inIterator = node.inputSet.iterator();
            while (inIterator.hasNext()) {
                DGNode inputNode = inIterator.next();
                String inputTaskName;
                if (inputNode.index == 0) {
                    inputTaskName = "Start";
                } else {
                    inputTaskName = taskNames.get(inputNode.index - 1);
                }
                System.out.print(inputTaskName);
                if (inIterator.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.print("]");

            System.out.print(" ");
            if (node.index == 0) {
                System.out.print("Start");
            } else if (node.index == dependencyGraphNodes.size() - 1) {
                System.out.print("End");
            } else {
                System.out.print(taskNames.get(node.index - 1));
            }
            System.out.print(" ");

            System.out.print("[");
            Iterator<DGNode> outIterator = node.outputSet.iterator();
            while (outIterator.hasNext()) {
                DGNode outputNode = outIterator.next();
                String outputTaskName;
                if (outputNode.index == dependencyGraphNodes.size() - 1) {
                    outputTaskName = "End";
                } else {
                    outputTaskName = taskNames.get(outputNode.index - 1);
                }
                System.out.print(outputTaskName);
                if (outIterator.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.print("]");

            System.out.println();
        }
    }

}