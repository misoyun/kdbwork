package com.kx.sample;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fd.delta.dm.DeltaMessagingPublisher;
import com.fd.delta.dm.DeltaMessagingSubscription;
import com.kx.q.c;

public class Publisher implements DeltaMessagingPublisher {

	List<DeltaMessagingSubscription> subscriptions = new ArrayList< >();

	public void subscriptionRemoved(String table, DeltaMessagingSubscription subscription) {

		subscriptions.remove(subscription);
		System.out.println("Subscription removed to the list");
	}
	public void subscriptionAdded(String table, DeltaMessagingSubscription subscription) {

		subscriptions.add(subscription);
		System.out.println("Subscription added to the list");

	}
	
	public void publishData(String table,c.Flip data) throws InterruptedException {
		while(0==subscriptions.size()) {
			System.out.println("The number of subscriptions: " + subscriptions.size());
			System.out.println("Waiting for the new subscription");
			Thread.sleep(1000);
		}
		for(DeltaMessagingSubscription s : subscriptions ){
			if(true==s.publish(table, data))
					System.out.println("Data is published to KX Platform");
			else
					System.out.println("The data is not published");
			}
		}
}
