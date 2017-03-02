package org.fhm.log.model;

/**
 * Created by huangtao on 2017/3/1.
 */
public class Task {

    String name;

    int index;

    public Task(String taskName, int index) {
        this.name = taskName;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

}
