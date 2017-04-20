package org.pmf.fhm.log;

import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.out.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by huangtao on 2017/3/14.
 */
public class XLogManager {

    public static XLog readLog(InputStream logFile, String name) {
        XLog lg = null;

        try {
            lg = openLog(logFile, name);
            return lg;
        } catch (Exception var4) {
            return null;
        }
    }

    public static XLog openLog(InputStream inputLogFile, String name) throws Exception {
        XLog log = null;
        if (name.toLowerCase().endsWith("mxml.gz")) {
            XMxmlGZIPParser parser = new XMxmlGZIPParser();
            log = (XLog) parser.parse(inputLogFile).get(0);
        } else if (name.toLowerCase().endsWith("mxml") || name.toLowerCase().endsWith("xml")) {
            XMxmlParser parser1 = new XMxmlParser();
            log = (XLog) parser1.parse(inputLogFile).get(0);
        }

        if (name.toLowerCase().endsWith("xes.gz")) {
            XesXmlGZIPParser parser2 = new XesXmlGZIPParser();
            log = (XLog) parser2.parse(inputLogFile).get(0);
        } else if (name.toLowerCase().endsWith("xes")) {
            XesXmlParser parser3 = new XesXmlParser();
            log = (XLog) parser3.parse(inputLogFile).get(0);
        }

        if (log == null) {
            throw new Exception("Oops could not open the log file!");
        } else {
            return log;
        }
    }

    public static XSerializer getSerializer(String logName) {
        Object xs = null;
        if (logName.toLowerCase().endsWith("mxml.gz")) {
            xs = new XMxmlGZIPSerializer();
        } else if (logName.toLowerCase().endsWith("mxml") || logName.toLowerCase().endsWith("xml")) {
            xs = new XMxmlSerializer();
        }

        if (logName.toLowerCase().endsWith("xes.gz")) {
            xs = new XesXmlGZIPSerializer();
        } else if (logName.toLowerCase().endsWith("xes")) {
            xs = new XesXmlSerializer();
        }

        return (XSerializer) xs;
    }

    public static void saveLogInDisk(XLog log, String logFilePath) {
        try {
            XSerializer e = getSerializer(logFilePath);
            FileOutputStream os = new FileOutputStream(new File(logFilePath));
            e.serialize(log, os);
            os.close();
        } catch (Exception var4) {
            System.out.println("Exception when writing file " + var4.toString());
        }

    }

    public static String getEventType(XEvent e) {
        return e.getAttributes().get("lifecycle:transition").toString().trim();
    }

    public static boolean isCompleteEvent(XEvent e) {
        return getEventType(e).compareToIgnoreCase("complete") == 0;
    }

    public static String getEventName(XEvent e) {
        return e.getAttributes().get("concept:name").toString().trim();
    }

    public static XAttribute getEventAttr(XEvent e, String attrKey) {
        return e.getAttributes().get(attrKey);
    }

    public static XAttributeTimestamp getEventTime(XEvent e) {
        return (XAttributeTimestamp) e.getAttributes().get("time:timestamp");
    }

    public static XLog logStreamer(XLog log) {
        XLogImpl eventStream = new XLogImpl(log.getAttributes());

        for (int i = 0; i < log.size(); ++i) {
            XTrace t = log.get(i);

            for (int j = 0; j < t.size(); ++j) {
                XEvent e = t.get(j);
                if (XLogManager.isCompleteEvent(e) && XLogManager.getEventName(e).compareTo("START") != 0 &&
                        XLogManager.getEventName(e).compareTo("END") != 0 && XLogManager.getEventAttr(e,
                        "time:timestamp") != null) {
                    XAttributeMap attmap = t.getAttributes();
                    XTraceImpl t1 = new XTraceImpl(attmap);
                    t1.add(e);
                    eventStream.add(t1);
                }
            }
        }

        Collections.sort(eventStream, (o1, o2) -> {
            XAttributeTimestampImpl date1 = (XAttributeTimestampImpl) XLogManager.getEventTime(o1.get(0));
            XAttributeTimestampImpl date2 = (XAttributeTimestampImpl) XLogManager.getEventTime(o2.get(0));
            return date1.compareTo(date2);
        });

        return eventStream;
    }

    public static XLog parseXesToEventStream(String path) {
        try {
            XesXmlParser xesXmlParser = new XesXmlParser();
            List<XLog> logs = xesXmlParser.parse(new File(path));
            XLog log = logs.get(0);

            return logStreamer(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XLog parseMxmlToEventStream(String path) {
        try {
            Path mxmlPath = Paths.get(path);
            XLog xl = XLogManager.readLog(new FileInputStream(mxmlPath.toString()), mxmlPath.getFileName().toString());
            return logStreamer(xl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTraceID(XTrace t) {
        return ((XAttribute)t.getAttributes().get("concept:name")).toString().trim();
    }

    public static XLog serialEventStream(XLog eventStream) {
        XLog resultEventStream = new XLogImpl(eventStream.getAttributes());

        Map<String, List<XTrace>> waitingMap = new HashMap<>();

        for (XTrace trace : eventStream) {
            XEvent event = trace.get(0);
            ((XAttributeLiteralImpl) event.getAttributes().get("org:resource")).setValue(XLogManager.getTraceID(trace));
            if (XLogManager.getEventName(event).equals("startAppointProcess")) {
                List<XTrace> list = new ArrayList<>();
                list.add(trace);
                waitingMap.put(XLogManager.getTraceID(trace), list);
            } else if (XLogManager.getEventName(event).equals("completeAppointProcess")) {
                List<XTrace> list = waitingMap.remove(XLogManager.getTraceID(trace));
                list.add(trace);
                resultEventStream.addAll(list);
            } else {
                List<XTrace> list = waitingMap.get(XLogManager.getTraceID(trace));
                list.add(trace);
            }
        }
        return resultEventStream;
    }

}
