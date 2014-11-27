package comanche.opencom;

public class TesteClientOpenCOM {

  public static void main(String[] args) {

    OpenCOM runtime = new OpenCOM();
    IOpenCOM oc =  (IOpenCOM) runtime.QueryInterface("OpenCOM.IOpenCOM"); 
       
    IUnknown pRequestReceiverUnk = (IUnknown) oc.createInstance("comanche.opencom.RequestReceiver", "RequestReceiver");
    ILifeCycle pRequestReceiverLife =  (ILifeCycle) pRequestReceiverUnk.QueryInterface("OpenCOM.ILifeCycle");
    pRequestReceiverLife.startup(oc);
        
    IUnknown pSchedulerUnk = (IUnknown) oc.createInstance("comanche.opencom.MultiThreadScheduler", "Scheduler");
    ILifeCycle pSchedulerLife =  (ILifeCycle) pSchedulerUnk.QueryInterface("OpenCOM.ILifeCycle");
    pSchedulerLife.startup(oc);
        
    oc.connect(pRequestReceiverUnk, pSchedulerUnk, "comanche.opencom.Scheduler");

    IFrame iFrame = new InteropFrame();//passar runtime do OpenCOM por parametro do construtor	
    Parameters parameters = new Parameters("comanche.opencom.RequestReceiver", "comanche.opencom.RequestHandler", "OpenCOM", "comanche.fractal.RequestAnalyzer", "comanche.fractal.RequestHandler", "Fractal");
    ParametersBinding pBinding = new ParametersBinding("RMI");	// retirar o segundo parâmetro "localhost"
	iFrame.remoteBind(oc,parameters, pBinding, "localhost");
    // iFrame.startClient("localhost", 6666); 
    // iFrame.connectClient(oc);
		
    Runner r = (Runner) pRequestReceiverUnk.QueryInterface("comanche.opencom.Runner");
    r.run();
	
	
  }
}