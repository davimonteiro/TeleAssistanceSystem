package tas.configuration;

import java.nio.file.Paths;

import service.adaptation.effector.WorkflowEffector;
import tas.adaptation.simple.LotusAdaptationEngine;
import tas.adaptation.simple.LotusProbe;
import tas.services.assistance.AssistanceService;
import br.com.davimonteiro.lotus_runtime.LotusRuntime;
import br.com.davimonteiro.lotus_runtime.config.ConfigurationUtil;

public class LotusTASConfiguration implements TASConfiguration {

	private static final String CONFIG_PATH = "/Users/davimonteiro/Desktop/LoTuS files/config.json";
	
	private LotusProbe lotusProbe;
	private WorkflowEffector effector;
	private AssistanceService assistanceService;
	private LotusAdaptationEngine adaptationEngine;
	private LotusRuntime lotusRuntime;

	public LotusTASConfiguration(AssistanceService assistanceService) throws Exception {
		this.assistanceService = assistanceService;
		lotusProbe = new LotusProbe();
		effector = new WorkflowEffector(assistanceService);
		lotusRuntime = new LotusRuntime(ConfigurationUtil.load(Paths.get(CONFIG_PATH)), new LotusRuntimeHandler(assistanceService));
		adaptationEngine = new LotusAdaptationEngine(lotusProbe, effector);
	}

	@Override
	public void setConfiguration() {
		assistanceService.getWorkflowProbe().register(lotusProbe);
		effector.refreshAllServices();
	}

	@Override
	public void removeConfiguration() {
		assistanceService.getWorkflowProbe().unRegister(lotusProbe);
		adaptationEngine.notify();
	}
	
	public void startLotusRuntime() throws Exception {
		System.err.println("Starting the Lotus@Runtime");
		this.lotusRuntime.start();
	}
	
	public void stopLotusRuntime() throws Exception {
		System.err.println("Stopping the Lotus@Runtime");
		this.lotusRuntime.stop();
	}
	
}
