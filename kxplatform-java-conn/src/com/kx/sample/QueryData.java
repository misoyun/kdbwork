package com.kx.sample;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.fd.delta.stream.queryrouter.IQRMessageHandler;
import com.fd.delta.stream.queryrouter.entity.QPResultMessage;
import com.fd.delta.stream.queryrouter.entity.QRResponseMessage;
import com.kx.q.c;
import com.kx.q.c.Dict;
import com.kx.q.c.Flip;

public class QueryData {

	public static void main(String[] args) throws DeltaStreamException, TimeoutException, IOException, InterruptedException {

		// creation of a single DeltaStream
		DeltaStream stream = DeltaStreamFactory.newDeltaStream();
		
		// Initialise the connection with DeltaControl

		String primaryHost = "HOST_NAME";
		int primaryPort = 11000; // Delta Control port
		String secondaryHost = "";
		int secondaryPort = 0;
		String instanceName = "DataQueryDemo";
		int instanceId = 100;
		boolean exclusive = true;
		String loginString = "USERNAME:PASSWORD";
		int retryMaxTimeOutPeriod = 1000;
        	int retryTimeoutStep = 1000;
        	boolean encrypted = false;

		stream.initialiseDeltaControl(primaryHost, primaryPort, secondaryHost, secondaryPort, instanceName, instanceId, 
				exclusive, loginString, retryMaxTimeOutPeriod, retryTimeoutStep, encrypted);
	

		// Data Query
		stream.initialiseDeltaAPIService(loginString, null);
		
		
		// Get the available API list from Delta Control
		Map<String, IDeltaAPI> apis = stream.getDeltaAPIService().getAPI();
		System.out.println("Getting API List from Delta Control");

		
		while(apis.size()==0) {
			stream.getDeltaAPIService().refresh();
			apis = stream.getDeltaAPIService().getAPI();
		}

		System.out.println("========= API List =========");
		for(String name:apis.keySet()) {
			String key = name.toString();
			System.out.println(key);
		}
	
		// Get API name 
		IDeltaAPI api = apis.get(".sp.queryTable");
		IDeltaQuery query = stream.getDeltaAPIService().createDeltaQuery(api);
		
		Thread.sleep(5000);
		
		// Create Argument as Dictionary
		Map<String, Object> params = new HashMap<String, Object>();
		String symList[] = {"EUR/USD","USD/JPY"};
		params.put("sym",  symList );
		params.put("startTS", Timestamp.valueOf("2024-09-30 16:50:00.0000"));
		params.put("endTS", Timestamp.valueOf("2024-09-30 17:00:00.0000"));
		query.addParameterValues(params);

		System.out.println("Running query....");
		long start = System.currentTimeMillis();
		c.Flip result = (c.Flip) stream.getDeltaAPIService().runQuery(query, "QueryDemo",null);
		long end = System.currentTimeMillis();
		Timestamp elapsedTime = new Timestamp(end-start);
		System.out.println("Query Time: ");
		System.out.println(elapsedTime.getTime());
		
		QResultPrinter resultPrinter = new QResultPrinter(System.out);
		resultPrinter.printResult(result);

		
/*		for(String col : result.x)
			System.out.print(col + " " + "\t\t\t");

		System.out.println("");
		System.out.println("===================================");
		
		Object[] data = (Object[]) result.y;
		Timestamp[] time = (Timestamp[])data[0];
		String[] sym = (String[])data[1];
		double[] ask = (double[])data[2];
		double[] bid = (double[])data[3];
		String[] side = (String[])data[4];
		
		for(int i=0; i<time.length;i++)
			System.out.println(time[i] + "\t" + sym[i] + "\t" + ask[i] +"\t" + bid[i] + "\t" + side[i]);
			
*/
		// Close the connection
		stream.close();	
		
	}

}
