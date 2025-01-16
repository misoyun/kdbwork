\d .sp

//
// @desc initialize the connection with SDL and QRProxy process
//
init:{[]
    
    FEED::first exec name from .feed.cfg where .cfgp.node in' nodes,name<>`MD;
    .pubsub.registerPub[;enlist string .sp.FEED]each `$"sdlIn",/:string distinct raze exec 
		nodes from .feed.cfg where .cfgp.node in'nodes,name<>`MD; / Register to all SDLs individually
    .log.LOG.info"Simulator initialization completed.";
    
    //.misc.regQrClient[]; / Register as the QR client
    //.sp.QRPROXY:hopen 8351; / Port number of QRProxy process
    }


//
// @desc Request the API call to QR.
//
// scala>.spark.read.format("kdb").
//          option("sensorid";"sensor_").
//          option("startts";"2020.05.07D00:00:00")
//          option("api";"apiname").
//          option("func";".sp.request")
//

request:{[opt] 
    //args:get opt`args; / Get argument from Spark
    sensorID:`$opt`sensorid; / Get sensorID as an API argument
    startTS:-12h$opt`startts; / Get startTS 
    endTS:-12h$opt`endts; / Get endTS
    args:`sensorID`startTS`endTS!(sensorID;startTS;endTS); / Build the dictionary for API argument
    args[`hdr]:.sapi.buildHdr[0i;0i]; / Add header to argument dictionary
    api:`$opt`api; / Get Apiname from Spark
    .sp.QRPROXY(`request;api;args); / Make request to QRProxy 
    while[not 2=count rsp:.sp.QRPROXY[];rsp]; / Synchronous block and wait for response
    last rsp / Return the result table to Spark
	
    }

//
// @desc Ingest the data from Spark through SDL
//
// 
// scala> df.write.format("kdb").options(conn).
//              option("batchsize",5).
//              option("func",".sp.ingest").
//              option("writeaction","append").
//              option("sdlsrcname","sparkIngest").
//              option("sdlmsgtype",".kxstr.readingSpark").
//              option("feed","feed1").
//              save
//
    .sp.setLogLevel .sp.optGet[opt;`loglevel;`warn];
	.sp.logDebugOptions[opt];
    
    sdlSrcName:opt`sdlsrcname; / Get SDL source name
    feed:`$opt`feed; / Get feed
    sdlMsgType:`$opt`sdlmsgtype; / Get SDL message type
    
    hdr:.sapi.buildHdr[0Ni;0Ni],`sdlCollectionTS`sdlSrcName`feed`sdlMsgType`isNotify!(.z.P;sdlSrcName;feed;sdlMsgType;1b); / Build header
    msg:enlist `hdr`reading!(hdr;tbl); / Enlist the header and table
    .sp.msg:msg; 
    
    .log.LOG.debug("Pushed Reading to SDL from Spark");
    .dm.pubc[feed;feed;msg] / Publish the table to SDL process
    }