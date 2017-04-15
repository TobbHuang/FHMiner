package org.pmf.fhm.log;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.pmf.fhm.log.model.LogInfo;
import org.pmf.fhm.log.model.Task;
import org.pmf.fhm.log.model.Trace;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2017/2/27.
 * <p>
 * logic of parsing log
 */
public class LogParser {

    private static LogParser logParser;

    public static LogInfo parseLog(File xesFile) throws Exception {
        if (logParser == null) {
            logParser = new LogParser();
        }

        LogInfo logInfo = new LogInfo();

        logParser.convertXesFileToBasicData(xesFile, logInfo);
        if (logInfo.traces.size() == 0 || logInfo.taskNames.size() == 0) {
            throw new Exception("log is empty");
        }

        return logInfo;
    }

    public static LogInfo parseLog(XLog log) throws Exception {
        if (logParser == null) {
            logParser = new LogParser();
        }

        LogInfo logInfo = new LogInfo();

        logParser.convertXlogToBasicData(log, logInfo);
        if (logInfo.traces.size() == 0 || logInfo.taskNames.size() == 0) {
            throw new Exception("log is empty");
        }

        return logInfo;
    }

    /**
     * 将xes文件的内容解析为log的基础数据，包括task name和trace
     *
     * @param xesFile
     * @param logInfo
     */
    private void convertXesFileToBasicData(File xesFile, LogInfo logInfo) {
        XesXmlParser xesXmlParser = new XesXmlParser();
        if (!xesXmlParser.canParse(xesFile)) {
            return;
        }

        try {
            List<XLog> logs = xesXmlParser.parse(xesFile);
            // 若存在多个日志，只处理第一个
            XLog log = logs.get(0);

            convertXlogToBasicData(log, logInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将xes文件的内容解析为log的基础数据，包括task name和trace
     *
     * @param log
     * @param logInfo
     */
    private void convertXlogToBasicData(XLog log, LogInfo logInfo) {

        try {
            List<XEventClassifier> eventClassifiers = log.getClassifiers();
            // TODO: 2016/12/29 classifier选择器
            XEventClassifier classifier = eventClassifiers.get(1);
            String attribute = classifier.getDefiningAttributeKeys()[0];
            // TODO: 2016/12/29 不确定这个值取的是否绝对正确，有空验证一下
            Iterator<XEventClass> iterator = XLogInfoFactory.createLogInfo(log).getNameClasses().getClasses()
                    .iterator();

            while (iterator.hasNext()) {
                logInfo.taskNames.add(iterator.next().getId());
            }

            for (int i = 0; i < log.size(); i++) {
                XTrace xTrace = log.get(i);
                Trace tmpTrace = new Trace();
                for (int j = 0; j < xTrace.size(); j++) {
                    String taskName = xTrace.get(j).getAttributes().get(attribute).toString();
                    tmpTrace.addTask(new Task(taskName, logInfo.taskNames.indexOf(taskName)));
                }
                logInfo.traces.add(tmpTrace);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}