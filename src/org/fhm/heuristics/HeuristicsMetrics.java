package org.fhm.heuristics;

import org.fhm.log.model.LogInfo;
import org.fhm.log.model.Trace;

/**
 * Created by huangtao on 2017/2/28.
 */
public class HeuristicsMetrics {

    public LogInfo logInfo;

    public int countOfTasks;

    // index=0 means start, index=taskName.size()+1 means end
    public int[][] directSuccessorCounting;

    public int[][] lengthTwoLoopsCounting;

    public double[][] dependencyMeasures;

    public double[][] lengthTwoLoopsDependencyMeasures;

    public HeuristicsMetrics(LogInfo log) {
        this.logInfo = log;
        // start and end
        countOfTasks = log.taskNames.size() + 2;

        calRelation();
        calDependencyMeasures();
    }

    /**
     * 计算task的关系
     */
    private void calRelation() {
        // init array
        directSuccessorCounting = new int[countOfTasks][countOfTasks];
        lengthTwoLoopsCounting = new int[countOfTasks][countOfTasks];

        for (Trace trace : logInfo.traces) {
            directSuccessorCounting[0][logInfo.taskNames.indexOf(trace.getTask(0).getName()) + 1]++;
            directSuccessorCounting[logInfo.taskNames.indexOf(trace.getTask(trace.getLength() - 1).getName()) +
                    1][countOfTasks - 1]++;

            for (int i = 0; i < trace.getLength() - 1; i++) {
                int fromIndex = logInfo.taskNames.indexOf(trace.getTask(i).getName());
                int toIndex = logInfo.taskNames.indexOf(trace.getTask(i + 1).getName());
                directSuccessorCounting[fromIndex + 1][toIndex + 1]++;

                // length two loop
                if (i < trace.getLength() - 2 && fromIndex != toIndex && fromIndex == logInfo.taskNames.indexOf(trace
                        .getTask(i + 2).getName())) {
                    lengthTwoLoopsCounting[fromIndex + 1][toIndex + 1]++;
                }
            }
        }
    }

    /**
     * 计算dependency measures
     */
    private void calDependencyMeasures() {
        // init array
        dependencyMeasures = new double[countOfTasks][countOfTasks];
        lengthTwoLoopsDependencyMeasures = new double[countOfTasks][countOfTasks];

        for (int i = 0; i < countOfTasks; i++) {
            for (int j = 0; j < countOfTasks; j++) {
                if (i != j) {
                    dependencyMeasures[i][j] = calDirectSuccessorDependency(directSuccessorCounting[i][j],
                            directSuccessorCounting[j][i]);
                } else {
                    dependencyMeasures[i][j] = calEqualDirectSuccessorDependency(directSuccessorCounting[i][j]);
                }

                lengthTwoLoopsDependencyMeasures[i][j] = calLengthTwoLoopsDependency(lengthTwoLoopsCounting[i][j],
                        lengthTwoLoopsCounting[j][i]);
            }
        }
    }

    /**
     * 计算直接跟随的dependency measures && a!=b
     *
     * @param a a>wb
     * @param b b>wa
     * @return
     */
    private double calDirectSuccessorDependency(int a, int b) {
        return ((double) (a - b)) / (a + b + 1);
    }

    /**
     * 计算直接跟随的dependency measures && a==b
     *
     * @param a a>wa
     * @return
     */
    private double calEqualDirectSuccessorDependency(int a) {
        return ((double) a) / (a + 1);
    }

    /**
     * 计算length two loop的dependency measures
     *
     * @param a a>>wb
     * @param b b>>wa
     * @return
     */
    private double calLengthTwoLoopsDependency(int a, int b) {
        return ((double) (a + b)) / (a + b + 1);
    }

    public void print() {
        System.out.println("task name:");
        for (String name : logInfo.taskNames) {
            System.out.print(name + ", ");
        }
        System.out.println();

        // 太长了，就不打印了
        //        System.out.println("traces:");
        //        for (Trace trace : logInfo.traces) {
        //            System.out.println(trace);
        //        }
        //        System.out.println();

        System.out.println("direct successor counting: ");
        for (int i = 0; i < directSuccessorCounting.length; i++) {
            for (int j = 0; j < directSuccessorCounting.length; j++) {
                System.out.print(directSuccessorCounting[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("length-two loops counting: ");
        for (int i = 0; i < lengthTwoLoopsCounting.length; i++) {
            for (int j = 0; j < lengthTwoLoopsCounting.length; j++) {
                System.out.print(lengthTwoLoopsCounting[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("dependency measures: ");
        for (int i = 0; i < dependencyMeasures.length; i++) {
            for (int j = 0; j < dependencyMeasures.length; j++) {
                System.out.print(dependencyMeasures[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("length-two loops dependency measures: ");
        for (int i = 0; i < lengthTwoLoopsDependencyMeasures.length; i++) {
            for (int j = 0; j < lengthTwoLoopsDependencyMeasures.length; j++) {
                System.out.print(lengthTwoLoopsDependencyMeasures[i][j] + " ");
            }
            System.out.println();
        }

    }

}
