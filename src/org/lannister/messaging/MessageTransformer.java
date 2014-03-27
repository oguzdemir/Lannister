package org.lannister.messaging;

import com.google.common.base.Function;

import eis.iilang.Identifier;
import eis.iilang.Parameter;

/**
author = 'Oguz Demir'
 */
public class MessageTransformer implements Function<String, Parameter>{

	@Override
	public Parameter apply(String message) {
		return new Identifier(message);
	}
	
}
