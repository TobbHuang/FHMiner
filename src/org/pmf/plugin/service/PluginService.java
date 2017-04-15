package org.pmf.plugin.service;

import net.sf.json.JSONObject;
import org.deckfour.xes.model.XLog;

import java.util.Map;

public interface PluginService {
	public JSONObject doPluginService(XLog log, Map<String, String> params);
}
