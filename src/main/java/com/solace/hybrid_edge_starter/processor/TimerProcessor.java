package com.solace.hybrid_edge_starter.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This processes messages from the Camel Timer component.
 * 
 * It reads the TIMER_FIRED_TIME property, and creates a string message from it
 * to populate the body.
 * 
 * If the message already has a body, it does nothing.
 * 
 * If the message has no body and no TIMER_FIRED_TIME property, it
 * sets the body to the empty string.
 *
 */
@Component
public class TimerProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(TimerProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Object body = message.getBody();
		
		if (body == null) {
			Object timer = exchange.getProperty(Exchange.TIMER_FIRED_TIME);
			
			if (timer != null) {
				message.setBody("The timer says " + timer);
				logger.debug("TimerProcessor: processed a Timer message.");
			} else {
				message.setBody("");
				logger.debug("TimerProcessor: processed a null message body.");
			}
		}		
	}
}
