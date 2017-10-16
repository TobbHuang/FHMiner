package org.pmf.fhm.heuristics.model.dg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.pmf.fhm.heuristics.HeuristicsMetrics;

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

    public JSONObject buildJsn(HeuristicsMetrics heuristicsMetrics){
        JSONObject jsn = new JSONObject();

        JSONArray nodeJsns = new JSONArray();

        JSONObject startNodeJsn = new JSONObject();
        startNodeJsn.put("label", "Start");
        startNodeJsn.put("detail", "Start");
        startNodeJsn.put("count", heuristicsMetrics.logInfo.traces.size());
        startNodeJsn.put("type", "DG_NODE");
        nodeJsns.add(startNodeJsn);

        for (int i = 0; i < taskNames.size(); i++) {
            String taskName = taskNames.get(i);
            JSONObject nodeJsn = new JSONObject();
            nodeJsn.put("label", taskName);
            nodeJsn.put("detail", taskName);
            int totalCount = 0;
            for(int followedCount : heuristicsMetrics.directSuccessorCounting[i+1]){
                totalCount += followedCount;
            }
            totalCount -= heuristicsMetrics.directSuccessorCounting[i+1][i+1];
            nodeJsn.put("count", totalCount);
            nodeJsn.put("type", "DG_NODE");
            nodeJsns.add(nodeJsn);
        }

        JSONObject endNodeJsn = new JSONObject();
        endNodeJsn.put("label", "End");
        endNodeJsn.put("detail", "End");
        endNodeJsn.put("count", heuristicsMetrics.logInfo.traces.size());
        endNodeJsn.put("type", "DG_NODE");
        nodeJsns.add(endNodeJsn);

        jsn.put("nodes", nodeJsns);

        JSONArray links = new JSONArray();
        for(DGNode source : dependencyGraphNodes){
            for(DGNode target : source.outputSet){
                JSONObject link = new JSONObject();
                link.put("source", source.index);
                link.put("target", target.index);
                link.put("type", 1);
                link.put("frequency", heuristicsMetrics.directSuccessorCounting[source.index][target.index]);
                links.add(link);
            }
        }
        jsn.put("links", links);

        jsn.put("tracesCount", heuristicsMetrics.logInfo.traces.size());

        return jsn;
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
