package com.kx.sample;

import java.util.Arrays;

import com.fd.delta.control.config.DeltaSchema;
import com.fd.delta.dm.DeltaMessagingServer;
import com.fd.delta.dm.DeltaMessagingSubscriber;
import com.fd.delta.stream.DeltaStream;
import com.fd.delta.stream.DeltaStreamException;
import com.fd.delta.stream.DeltaStreamFactory;
import com.kx.q.c.Flip;

public class SubscribeData {

	public static void main(String[] args) throws DeltaStreamException, InterruptedException {
		
		// creation of a single DeltaStream
		DeltaStream stream = DeltaStreamFactory.newDeltaStream();
		
		// Initialise the connection with DeltaControl
		String primaryHost = "HOST_NAME";
		int primaryPort = 10000; // Delta Control port
		String secondaryHost = "";
		int secondaryPort = 0;
		String instanceName = "DataIngestionDemo";
		int instanceId = 100;
		boolean exclusive = false;
		String loginString = "USERNAME:PASSWORD";
		int retryMaxTimeOutPeriod = 1000;
        	int retryTimeoutStep = 0;
        	boolean encrypted = false;

		stream.initialiseDeltaControl(primaryHost, primaryPort, secondaryHost, secondaryPort, instanceName, instanceId, 
				exclusive, loginString, retryMaxTimeOutPeriod, retryTimeoutStep, encrypted);
	
		// Get configuration info from DeltaControl
		DeltaSchema schema = stream.getConfigurationManager().getSchema("sampleTab");
		System.out.print(Arrays.toString(schema.getColumnNames())); // Get column names from the schema

		// Data Publish
		// Connect to Messaging Server
		DeltaMessagingServer dms = new DeltaMessagingServer(stream, "DS_MESSAGING_SERVER:DS","Administrator:password");
		dms.start();
		
		Thread.sleep(10000);
		
		// Implement DeltaMessagingSubscriber interface
		Subscriber dmsSubscriber = new Subscriber();
		
		dms.registerSubscriber(dmsSubscriber, "javaSub", "sampleTab" , null);
		
		System.out.println("Subscriber is registered");
		
		Thread.sleep(10000);

		// Get the table from DMS Subscriber
		QResultPrinter resultPrinter = new QResultPrinter(System.out);
		
		for(int i = 0; i < 10; i++)
			resultPrinter.printResult(dmsSubscriber.get());

		Thread.sleep(10000);
		
		// Close the connection
		stream.close();	
	


	}

}
