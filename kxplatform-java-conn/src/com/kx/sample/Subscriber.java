package com.kx.sample;

import java.sql.Timestamp;

import com.fd.delta.dm.DeltaMessagingSubscriber;
import com.kx.q.c;
import com.kx.q.c.Flip;

public class Subscriber implements DeltaMessagingSubscriber {
	
	public c.Flip table;

	@Override
	public void upd(String baseTopic, Object data) {

		System.out.println(baseTopic + " is subscribed");
		table = (c.Flip) data;
	
	}
	
	public c.Flip get() {
		return table;
	}

}
