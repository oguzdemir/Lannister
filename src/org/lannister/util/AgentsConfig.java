package org.lannister.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
author = 'Oguz Demir'
 */
public class AgentsConfig {
	
	private Document doc;
	private List<AgentConfig> configs = new ArrayList<AgentConfig>();
	
	public AgentsConfig(String configFile) {
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(configFile));
			doc.getDocumentElement().normalize();
			createAgentConfigs();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void createAgentConfigs() {
		NodeList nl = doc.getElementsByTagName("agent");
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				AgentConfig config = new AgentConfig();
				config.setName(e.getAttribute("name"));
				config.setEntity(e.getAttribute("entity"));
				config.setTeam(e.getAttribute("team"));
				config.setClazz(e.getAttribute("class"));
				configs.add(config);
			}
		}
	}
	
	public List<AgentConfig> getAgentConfigs() {
		return configs;
	}
	
	public int getTeamSize() {
		return configs.size();
	}
}
