package org.pmf.fhm.heuristics.model.cnet;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangtao on 2017/3/1.
 */
public class CNet {

    public List<CNetNode> cNetNodes;

    public List<String> taskNames;

    public CNet(int taskNum, List<String> taskNames) {
        cNetNodes = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            cNetNodes.add(new CNetNode(i));
        }
        this.taskNames = taskNames;
    }

    public void print() {
        for (CNetNode node : cNetNodes) {
            System.out.println(node.toString(taskNames));
        }
    }

}
