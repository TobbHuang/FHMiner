package org.fhm;

import org.fhm.heuristics.HeuristicsMetrics;
import org.fhm.heuristics.HeuristicsMinerConstants;
import org.fhm.heuristics.model.DG;
import org.fhm.heuristics.model.DGPair;
import org.fhm.heuristics.model.DGSubSet;
import org.fhm.log.LogParser;
import org.fhm.log.model.LogInfo;

import java.io.File;
import java.util.Iterator;

/**
 * Created by huangtao on 2017/2/27.
 * <p>
 * main function
 */
public class FlexibleHeuristicsMiner {

    HeuristicsMetrics heuristicsMetrics;

    public FlexibleHeuristicsMiner(String logPath) {
        try {
            LogInfo logInfo = LogParser.parseLog(new File(logPath));

            heuristicsMetrics = new HeuristicsMetrics(logInfo);
            heuristicsMetrics.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mine() {
        // calculate dependency graph

        // Step 1:
        // get the set of add tasks appearing in the log
        // finish, skip

        // step 2:
        // length-one loops
        DGSubSet l1lSubSet = new DGSubSet();
        for (int i = 0; i < heuristicsMetrics.countOfTasks; i++) {
            int l1lDirectSuccessorCounting = heuristicsMetrics.directSuccessorCounting[i][i];
            double l1lDependencyMeasure = (double) l1lDirectSuccessorCounting / (l1lDirectSuccessorCounting + 1);
            if (l1lDependencyMeasure >= HeuristicsMinerConstants.L1L_THRESHOLD) {
                l1lSubSet.addPair(new DGPair(i, i));
            }
        }

        // step 3:
        // length-two loops
        DGSubSet l2lSubSet = new DGSubSet();
        for (int i = 0; i < heuristicsMetrics.countOfTasks; i++) {
            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                if (i == j) {
                    continue;
                }
                if (!l1lSubSet.contains(new DGPair(i, i)) && !l1lSubSet.contains(new DGPair(j, j)) &&
                        heuristicsMetrics.lengthTwoLoopsDependencyMeasures[i][j] >= HeuristicsMinerConstants
                                .L2L_THRESHOLD) {
                    l2lSubSet.addPair(new DGPair(i, j));
                }
            }
        }

        // step 4、step 5:
        // for each task, the strongest cause
        // for each task, the strongest follower

        //TODO: 2017/2/27 i和j是否应该跳过start和end，这里先不跳过了
        DGSubSet outSubSet = new DGSubSet();
        DGSubSet inSubSet = new DGSubSet();
        for (int i = 0; i < heuristicsMetrics.countOfTasks; i++) {
            double maxOutDependencyMeasure = -1;
            DGPair tmpOutPair = null;

            double maxInDependencyMeasure = -1;
            DGPair tmpInPair = null;

            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                if (i == j) {
                    continue;
                }

                if (heuristicsMetrics.dependencyMeasures[i][j] > maxOutDependencyMeasure) {
                    tmpOutPair = new DGPair(i, j);
                    maxOutDependencyMeasure = heuristicsMetrics.dependencyMeasures[i][j];
                }

                if (heuristicsMetrics.dependencyMeasures[j][i] > maxInDependencyMeasure) {
                    tmpInPair = new DGPair(j, i);
                    maxInDependencyMeasure = heuristicsMetrics.dependencyMeasures[j][i];
                }
            }
            if (tmpOutPair != null) {
                outSubSet.addPair(tmpOutPair);
            }
            if (tmpInPair != null) {
                inSubSet.addPair(tmpInPair);
            }
        }

        // step 6:
        // only one following task is necessary for a length-two loop
        DGSubSet nonNecessaryOutSubSet = new DGSubSet();
        Iterator<DGPair> l2lIterator = l2lSubSet.pairSet.iterator();
        while (l2lIterator.hasNext()) {
            DGPair l2lPair = l2lIterator.next();
            DGSubSet l2lOutSubset = new DGSubSet();
            l2lOutSubset.merge(outSubSet.getFollowerSet(l2lPair.from));
            l2lOutSubset.merge(outSubSet.getFollowerSet(l2lPair.to));

            double maxDependencyMeasure = -1;
            DGPair maxDependencyPair = null;
            Iterator<DGPair> l2lFollowerIterator = l2lOutSubset.pairSet.iterator();
            while (l2lFollowerIterator.hasNext()) {
                DGPair l2lFollowerPair = l2lFollowerIterator.next();
                if (heuristicsMetrics.dependencyMeasures[l2lFollowerPair.from][l2lFollowerPair.to] >
                        maxDependencyMeasure) {
                    if (maxDependencyPair != null) {
                        nonNecessaryOutSubSet.addPair(maxDependencyPair);
                    }
                    maxDependencyMeasure = heuristicsMetrics.dependencyMeasures[l2lFollowerPair.from][l2lFollowerPair
                            .to];
                    maxDependencyPair = l2lFollowerPair;
                } else {
                    nonNecessaryOutSubSet.addPair(l2lFollowerPair);
                }
            }
        }

        // step 7:
        // remove contents of non necessary out subset
        Iterator<DGPair> nonNecessaryOutIterator = nonNecessaryOutSubSet.pairSet.iterator();
        while (nonNecessaryOutIterator.hasNext()) {
            outSubSet.remove(nonNecessaryOutIterator.next());
        }

        // step 8:
        // only one cause task is necessary for a length-two loop
        DGSubSet nonNecessaryInSubSet = new DGSubSet();
        l2lIterator = l2lSubSet.pairSet.iterator();
        while (l2lIterator.hasNext()) {
            DGPair l2lPair = l2lIterator.next();
            DGSubSet l2lInSubset = new DGSubSet();
            l2lInSubset.merge(outSubSet.getCauseSet(l2lPair.to));
            l2lInSubset.merge(outSubSet.getCauseSet(l2lPair.to));

            double maxDependencyMeasure = -1;
            DGPair maxDependencyPair = null;
            Iterator<DGPair> l2lCauseIterator = l2lInSubset.pairSet.iterator();
            while (l2lCauseIterator.hasNext()) {
                DGPair l2lCausePair = l2lCauseIterator.next();
                if (heuristicsMetrics.dependencyMeasures[l2lCausePair.from][l2lCausePair.to] > maxDependencyMeasure) {
                    if (maxDependencyPair != null) {
                        nonNecessaryInSubSet.addPair(maxDependencyPair);
                    }
                    maxDependencyMeasure = heuristicsMetrics.dependencyMeasures[l2lCausePair.from][l2lCausePair.to];
                    maxDependencyPair = l2lCausePair;
                } else {
                    nonNecessaryInSubSet.addPair(l2lCausePair);
                }
            }
        }

        // step 9:
        // remove contents of non necessary in subset
        Iterator<DGPair> nonNecessaryInIterator = nonNecessaryInSubSet.pairSet.iterator();
        while (nonNecessaryInIterator.hasNext()) {
            inSubSet.remove(nonNecessaryInIterator.next());
        }

        // step 10:
        // mining extra out connections which can be accepted
        DGSubSet outPlusSubSet = new DGSubSet();
        for (int i = 0; i < heuristicsMetrics.countOfTasks; i++) {
            DGSubSet rowSubSet = new DGSubSet();
            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                DGPair tmpPair = new DGPair(i, j);
                if (l1lSubSet.contains(tmpPair) || l2lSubSet.contains(tmpPair) || outSubSet.contains(tmpPair) ||
                        inSubSet.contains(tmpPair)) {
                    rowSubSet.addPair(tmpPair);
                }
            }
            if (rowSubSet.pairSet.size() == 0) {
                continue;
            }

            double bestDependencyMeasure = rowSubSet.getBestDependencyMeasure(heuristicsMetrics);
            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                double tmpDependencyMeasure = heuristicsMetrics.dependencyMeasures[i][j];
                if (!rowSubSet.contains(new DGPair(i, j)) && tmpDependencyMeasure >= HeuristicsMinerConstants
                        .DEPENDENCY_THRESHOLD && (bestDependencyMeasure - tmpDependencyMeasure <=
                        HeuristicsMinerConstants.RELATIVE_TO_BEST_THRESHOLD)) {
                    outPlusSubSet.addPair(new DGPair(i, j));
                }
            }
        }

        // step 11:
        // mining extra in connections which can be accepted
        DGSubSet inPlusSubSet = new DGSubSet();
        for (int i = 0; i < heuristicsMetrics.countOfTasks; i++) {
            DGSubSet columnSubSet = new DGSubSet();
            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                DGPair tmpPair = new DGPair(j, i);
                if (l1lSubSet.contains(tmpPair) || l2lSubSet.contains(tmpPair) || outSubSet.contains(tmpPair) ||
                        inSubSet.contains(tmpPair)) {
                    columnSubSet.addPair(tmpPair);
                }
            }
            if (columnSubSet.pairSet.size() == 0) {
                continue;
            }

            double bestDependencyMeasure = columnSubSet.getBestDependencyMeasure(heuristicsMetrics);
            for (int j = 0; j < heuristicsMetrics.countOfTasks; j++) {
                double tmpDependencyMeasure = heuristicsMetrics.dependencyMeasures[j][i];
                if (!columnSubSet.contains(new DGPair(j, i)) && tmpDependencyMeasure >= HeuristicsMinerConstants
                        .DEPENDENCY_THRESHOLD && (bestDependencyMeasure - tmpDependencyMeasure <=
                        HeuristicsMinerConstants.RELATIVE_TO_BEST_THRESHOLD)) {
                    inPlusSubSet.addPair(new DGPair(j, i));
                }
            }
        }

        // final and extra:
        // mining long-distance  dependencies
        // ??????????????


        // merge all results into a DG
        DG dependencyGraph = new DG(heuristicsMetrics.countOfTasks);
        l1lSubSet.mergeToDG(dependencyGraph);
        l2lSubSet.mergeToDG(dependencyGraph);
        outSubSet.mergeToDG(dependencyGraph);
        inSubSet.mergeToDG(dependencyGraph);
        outPlusSubSet.mergeToDG(dependencyGraph);
        inPlusSubSet.mergeToDG(dependencyGraph);

        dependencyGraph.print();

    }

    public static void main(String[] args) {
        FlexibleHeuristicsMiner miner = new FlexibleHeuristicsMiner
                ("/Users/huangtao/Documents/本科毕设/example-logs/exercise1.xes");
        miner.mine();
    }

}
