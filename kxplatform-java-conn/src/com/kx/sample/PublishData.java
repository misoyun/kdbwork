package com.kx.sample;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import com.fd.delta.apiservice.i.IDeltaAPI;
import com.fd.delta.apiservice.i.IDeltaQuery;
import com.fd.delta.control.config.DeltaSchema;
import com.fd.delta.dm.DeltaMessagingPublisher;
import com.fd.delta.dm.DeltaMessagingServer;
import com.fd.delta.dm.DeltaMessagingSubscription;
import com.fd.delta.q.impl.HostAndPort;
import com.fd.delta.stream.DeltaStream;
import com.fd.delta.stream.DeltaStreamException;
import com.fd.delta.stream.DeltaStreamFactory;
import com.fd.delta.stream.DeltaStreamTickerPlantDetails;
import com.kx.q.c.Dict;
import com.kx.q.c.Flip;

public class PublishData {

	public static void main(String[] args) throws DeltaStreamException, TimeoutException, IOException, InterruptedException {

		// creation of a single DeltaStream
		DeltaStream stream = DeltaStreamFactory.newDeltaStream();
		
		// Initialise the connection with DeltaControl

		String primaryHost = "HOSTNAME";
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

		// Implement DeltaMessagingPublisher interface
		Publisher dmsPublisher = new Publisher();
		
//		HashMap<String, String> filterCols = new HashMap<String, String>();
		dms.registerPublisher(dmsPublisher, "javaPub","sampleTab", null);

		Timestamp currentTS = new Timestamp(System.currentTimeMillis());
		String[] cols = schema.getColumnNames();
	
		Object[] records = {
				new Timestamp[] { currentTS, currentTS, currentTS },
				new String[] { "EUR/USD", "EUR/USD", "USD/KRW" },
				new Double [] { new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble() },
				new Double [] { new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble() },
				new String [] { "S", "B", "B"}
				};
		Flip fData = new Flip(new Dict(cols, records));
		
		dmsPublisher.publishData("sampleTab", fData);
		
		Thread.sleep(10000);
		
		// Close the connection
		stream.close();	
		
	}

}
