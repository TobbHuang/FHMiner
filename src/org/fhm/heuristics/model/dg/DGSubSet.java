package org.fhm.heuristics.model.dg;

import org.fhm.heuristics.HeuristicsMetrics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DGSubSet {

    public Set<DGPair> pairSet;

    public DGSubSet() {
        pairSet = new HashSet<>();
    }

    public void addPair(DGPair pair) {
        pairSet.add(new DGPair(pair.from, pair.to));
    }

    /**
     * 将子集合并到DG
     *
     * @param dependencyGraph
     */
    public void mergeToDG(DG dependencyGraph) {
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair pair = iterator.next();
            dependencyGraph.dependencyGraphNodes.get(pair.from).addOutput(dependencyGraph.dependencyGraphNodes.get
                    (pair.to));
            dependencyGraph.dependencyGraphNodes.get(pair.to).addInput(dependencyGraph.dependencyGraphNodes.get(pair
                    .from));
        }
    }

    public boolean contains(DGPair comparePair) {
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair pair = iterator.next();
            if (pair.equals(comparePair)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取from的follower集合
     *
     * @param from
     * @return
     */
    public DGSubSet getFollowerSet(int from) {
        DGSubSet followerSet = new DGSubSet();
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair pair = iterator.next();
            if (pair.from == from) {
                followerSet.addPair(pair.clone());
            }
        }
        return followerSet;
    }

    /**
     * 取to的cause集合
     *
     * @param to
     * @return
     */
    public DGSubSet getCauseSet(int to) {
        DGSubSet causeSet = new DGSubSet();
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair pair = iterator.next();
            if (pair.to == to) {
                causeSet.addPair(pair.clone());
            }
        }
        return causeSet;
    }

    public void merge(DGSubSet subSet) {
        pairSet.addAll(subSet.pairSet);
    }

    public void remove(DGPair pair) {
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair tmpPair = iterator.next();
            if (tmpPair.from == pair.from && tmpPair.to == pair.to) {
                pairSet.remove(tmpPair);
                return;
            }
        }
    }

    public double getBestDependencyMeasure(HeuristicsMetrics metrics) {
        double bestDependencyMeasure = -1;
        Iterator<DGPair> iterator = pairSet.iterator();
        while (iterator.hasNext()) {
            DGPair pair = iterator.next();
            if (metrics.dependencyMeasures[pair.from][pair.to] > bestDependencyMeasure) {
                bestDependencyMeasure = metrics.dependencyMeasures[pair.from][pair.to];
            }
        }
        return bestDependencyMeasure;
    }

}
