package org.fhm.heuristics.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2017/2/28.
 */
public class DG {

    public List<DGNode> dependencyGraphNodes;

    public DG(int taskNum) {
        dependencyGraphNodes = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            dependencyGraphNodes.add(new DGNode(i));
        }
    }

    public void print(){
        // 还没写完。。。比想象中复杂，明天再说了
        for(int i=0;i<dependencyGraphNodes.size();i++){
            DGNode node=dependencyGraphNodes.get(i);

            System.out.print("[");
            Iterator<DGNode> inIterator=node.inputSet.iterator();
            while(inIterator.hasNext()){
            }
            System.out.print("]");
        }
    }

}
