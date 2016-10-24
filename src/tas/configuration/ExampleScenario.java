/**
 * 
 */
package tas.configuration;

import service.adaptation.effector.WorkflowEffector;
import tas.adaptation.simple.MyAdaptationEngine;
import tas.adaptation.simple.MyProbe;
import tas.services.assistance.AssistanceService;

/**
 * @author M. Usman Iftikhar
 * @email muusaa@lnu.se
 *
 */
public class ExampleScenario implements TASConfiguration {

	MyProbe myProbe;
	WorkflowEffector myEffector;
	AssistanceService assistanceService;

	public ExampleScenario(AssistanceService assistanceService) {
		this.assistanceService = assistanceService;
		myProbe = new MyProbe();
		myEffector = new WorkflowEffector(assistanceService);
		MyAdaptationEngine myAdaptationEngine = new MyAdaptationEngine(myProbe, myEffector);
		System.out.println(myAdaptationEngine);
	}

	@Override
	public void setConfiguration() {
		assistanceService.getWorkflowProbe().register(myProbe);
		myEffector.refreshAllServices();
	}

	@Override
	public void removeConfiguration() {
		assistanceService.getWorkflowProbe().unRegister(myProbe);
	}
}
