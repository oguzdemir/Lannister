package org.lannister.messaging;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import eis.iilang.Parameter;
import eis.iilang.Percept;

/**
author = 'Oguz Demir'
 */
public class Messages {
	
	public static Message create(String from, String action, String... params) {
		params = params == null ? new String[] {} : params;
		List<Parameter> parameters  = Lists.transform(Arrays.asList(params), new MessageTransformer());
		
		return new Message(from, new Percept(action, Lists.newLinkedList(parameters)));
	}
	
	public static Message create(String from, Percept percept) {
		return new Message(from, percept);
	}
}
