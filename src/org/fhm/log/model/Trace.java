package org.fhm.log.model;

import org.omg.CORBA.PUBLIC_MEMBER;

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
