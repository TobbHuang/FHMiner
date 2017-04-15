package org.pmf.fhm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.pmf.fhm.heuristics.HeuristicsMinerConstants;
import org.pmf.fhm.heuristics.model.cnet.CNetDependencySet;
import org.pmf.fhm.heuristics.model.cnet.CNetNode;
import org.pmf.plugin.service.PluginService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by huangtao on 2017/4/14.
 */
public class FHMPluginService implements PluginService {
    @Override
    public JSONObject doPluginService(XLog log, Map<String, String> params) {
        if (params.get("relative-best-threshold") != null) {
            HeuristicsMinerConstants.RELATIVE_TO_BEST_THRESHOLD = Double.parseDouble(params.get
                    ("relative-best-threshold"));
        }
        if (params.get("dependency-threshold") != null) {
            HeuristicsMinerConstants.DEPENDENCY_THRESHOLD = Double.parseDouble(params.get("dependency-threshold"));
        }
        if (params.get("l1l-threshold") != null) {
            HeuristicsMinerConstants.L1L_THRESHOLD = Double.parseDouble(params.get("l1l-threshold"));
        }
        if (params.get("l2l-threshold") != null) {
            HeuristicsMinerConstants.L2L_THRESHOLD = Double.parseDouble(params.get("l2l-threshold"));
        }

        FlexibleHeuristicsMiner miner = new FlexibleHeuristicsMiner(log);
        miner.mine();

        JSONObject jsn = new JSONObject();
        jsn.element("status", "OK");
        jsn.element("result", miner.heuristicsMetrics.dependencyGraph.buildJsn());

        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
        XEventClasses classes = logInfo.getEventClasses();
        List<XEventClass> eventClasses = new ArrayList(classes.size());
        eventClasses.addAll(classes.getClasses());
        JSONArray logarray = new JSONArray();
        for (XEventClass ec : eventClasses) {
            JSONObject logitem = new JSONObject();
            logitem.element("EventClass", ec.toString());
            logitem.element("Frequency", ec.size());
            logarray.element(logitem);
        }
        jsn.element("log", logarray);

        JSONArray cnetArray = new JSONArray();
        for(CNetNode node : miner.cNet.cNetNodes){
            JSONObject nodeJsn = new JSONObject();
            if (node.index == 0) {
                nodeJsn.put("name", "Start");
            } else if (node.index == miner.cNet.taskNames.size()+1){
                nodeJsn.put("name", "End");
            } else {
                nodeJsn.put("name", miner.cNet.taskNames.get(node.index-1));
            }

            JSONArray inputArray = new JSONArray();
            Iterator<CNetDependencySet> inIterator = node.inputSet.iterator();
            while(inIterator.hasNext()){
                CNetDependencySet set = inIterator.next();
                JSONObject setJsn = new JSONObject();
                setJsn.put("names", set.getSetNames(miner.cNet.taskNames));
                setJsn.put("frequency", set.getFrequency());
                inputArray.add(setJsn);
            }
            nodeJsn.put("input", inputArray);

            JSONArray outputArray = new JSONArray();
            Iterator<CNetDependencySet> outIterator = node.outputSet.iterator();
            while(outIterator.hasNext()){
                CNetDependencySet set = outIterator.next();
                JSONObject setJsn = new JSONObject();
                setJsn.put("names", set.getSetNames(miner.cNet.taskNames));
                setJsn.put("frequency", set.getFrequency());
                outputArray.add(setJsn);
            }
            nodeJsn.put("output", outputArray);

            cnetArray.add(nodeJsn);
        }
        jsn.element("cnet", cnetArray);

        return jsn;
    }
}
