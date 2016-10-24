package tas.adaptation.simple;

import service.adaptation.effector.WorkflowEffector;
import service.auxiliary.ServiceDescription;

/**
 * 
 * @author M. Usman Iftikhar
 *
 */
public class MyAdaptationEngine {

	private WorkflowEffector myEffector;

	public MyAdaptationEngine(MyProbe myProbe, WorkflowEffector workflowEffector) {
		myProbe.connect(this);
		this.myEffector = workflowEffector;
	}

	public void handleServiceFailure(ServiceDescription service, String opName) {
		this.myEffector.removeService(service);
	}

	public void handleServiceNotFound(String serviceType, String opName) {
		myEffector.refreshAllServices(serviceType, opName);
	}
}
