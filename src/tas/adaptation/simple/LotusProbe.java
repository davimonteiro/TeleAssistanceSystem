package tas.adaptation.simple;

import br.com.davimonteiro.tracelog.TraceLog;
import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import service.auxiliary.TimeOutError;


public class LotusProbe implements WorkflowProbeInterface {
	
	private LotusAdaptationEngine lotusAdaptationEngine;
	
	private static final String PATH = "/Users/davimonteiro/Desktop/log/traces.csv";
	
	public void connect(LotusAdaptationEngine lotusAdaptationEngine) {
		this.lotusAdaptationEngine = lotusAdaptationEngine;
	}

	@Override
	public void workflowStarted(String qosRequirement, Object[] params) {
		if (TraceLog.openTrace(PATH).isEmpty()) {
			TraceLog.openTrace(PATH).addAction("start");
		}
	}

	@Override
	public void workflowEnded(Object result, String qosRequirement, Object[] params) {
		
		if (result instanceof TimeOutError) {
			System.err.println("TimeOutError");
		} else if (result == null) {
			System.err.println("null");
		} else {
			TraceLog.openTrace(PATH).addAction("exit erro").addAction(params.toString()).addAction(result + "").endTrace();
		}
		
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {
		if (opName.equals("analyzeData")) {
			TraceLog.openTrace(PATH).addAction("vitalParamMsg");
			TraceLog.openTrace(PATH).addAction(opName);
		} else if (opName.equals("changeDrug")) {
			TraceLog.openTrace(PATH).addAction(opName);
			TraceLog.openTrace(PATH).addAction("notifyPA");
		} else if (opName.equals("changeDoses")) {
			TraceLog.openTrace(PATH).addAction(opName);
			TraceLog.openTrace(PATH).addAction("notifyPA");
		} else if (opName.equals("triggerAlarm")) {
			
			String action = TraceLog.openTrace(PATH).lookAction();
			
			if (action.equals("analyzeData")) {
				TraceLog.openTrace(PATH).addAction("sendAlarm");
				TraceLog.openTrace(PATH).addAction("alarm");
				TraceLog.openTrace(PATH).addAction("callFAS");
				TraceLog.openTrace(PATH).addAction("attendToPA");
			} else if (action.equals("start")) {
				TraceLog.openTrace(PATH).addAction("pButtonMsg");
				TraceLog.openTrace(PATH).addAction("alarm");
				TraceLog.openTrace(PATH).addAction("callFAS");
				TraceLog.openTrace(PATH).addAction("attendToPA");
			}
			
		} else {
			TraceLog.openTrace(PATH).addAction("ERRO!");
		}
		
	}

	@Override
	public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {
		
	}

	// Fazer um rollback
	@Override
	public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
		
		if (opName.equals("triggerAlarm")) {
			
			// Rollback alarm 
			String action = TraceLog.openTrace(PATH).lookAction();
			if (action.equals("attendToPA")) {
				TraceLog.openTrace(PATH).removeAction();
				TraceLog.openTrace(PATH).removeAction();
				TraceLog.openTrace(PATH).addAction("failedAlarm");
				TraceLog.openTrace(PATH).addAction("failure").endTrace();
			}
			
		} else if (opName.equals("analyzeData")) {
			
			//action deve ser igual a vitalParamMsg
			TraceLog.openTrace(PATH).removeAction();
			TraceLog.openTrace(PATH).removeAction();
			
			TraceLog.openTrace(PATH).addAction("stopMsg");
			TraceLog.openTrace(PATH).addAction("exit").endTrace();;
		} else if (opName.equals("changeDrug")) {
			TraceLog.openTrace(PATH).removeAction();
			TraceLog.openTrace(PATH).addAction("failedChangeDrug");
			TraceLog.openTrace(PATH).addAction("failure").endTrace();
		} else if (opName.equals("changeDoses")) {
			TraceLog.openTrace(PATH).removeAction();
			TraceLog.openTrace(PATH).addAction("failedChangeDoses");
			TraceLog.openTrace(PATH).addAction("failure").endTrace();
		} else {
			TraceLog.openTrace(PATH).addAction("ERRO failure");
		}
		
	    
	    lotusAdaptationEngine.handleServiceFailure(service, opName);
	}
	
	@Override
	public void serviceNotFound(String serviceType, String opName){
		TraceLog.openTrace(PATH).addAction("serviceNotFound").endTrace();
	    lotusAdaptationEngine.handleServiceNotFound(serviceType, opName);
	}
	
}
