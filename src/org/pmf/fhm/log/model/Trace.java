package org.pmf.fhm.log.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangtao on 2017/3/1.
 */
public class Trace {

    List<Task> taskList;

    public Trace() {
        taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public Task getTask(int index) {
        return taskList.get(index);
    }

    public int getLength() {
        return taskList.size();
    }

    public int backwardIndexOf(int start, int index) {
        for (int i = start; i < taskList.size(); i++) {
            if (taskList.get(i).getIndex() == index) {
                return i;
            }
        }
        return -1;
    }

    public int forwardIndexOf(int start, int index) {
        for (int i = start; i >=0; i--) {
            if (taskList.get(i).getIndex() == index) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < getLength(); i++) {
            str += getTask(i).getName();
            if (i != getLength() - 1) {
                str += ", ";
            }
        }
        return str;
    }
}
