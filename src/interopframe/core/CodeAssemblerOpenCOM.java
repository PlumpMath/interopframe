package interopframe.core;

import interopframe.api.Parameters;
import interopframe.utils.CustomClassLoader;

import java.util.Vector;

import OpenCOM.ILifeCycle;
import OpenCOM.IOpenCOM;
import OpenCOM.IUnknown;

public class CodeAssemblerOpenCOM implements ICodeAssembler {
	
	public CodeAssemblerOpenCOM() {
		CustomClassLoader.addPath("compiled/opencom");
	}
	
	public void mountClient(Parameters parameters, Object runtime) {
		IOpenCOM oc = (IOpenCOM) runtime;
		Vector<IUnknown> vec = new Vector<IUnknown>();
		int a = oc.enumComponents(vec);		
		
		IUnknown unk = (IUnknown) oc.createInstance(parameters.getCanonicalProxyName(), parameters.getCanonicalProxyName());
		ILifeCycle life = (ILifeCycle) unk.QueryInterface("OpenCOM.ILifeCycle");
		life.startup(oc);
		
		Class<?> clas;
		for (int i=0; i<a; i++) {			
			clas = oc.getComponentCLSID(vec.get(i));
			if (clas.getCanonicalName().equals(parameters.getCanonicalClassNameClient())) {
				oc.connect(vec.get(i), unk , parameters.getCanonicalInterfaceNameClient());
			}
		}
	}
	
	public void mountServer(Parameters parameters, Object runtime) {
		IOpenCOM oc = (IOpenCOM) runtime;
		
		Vector<IUnknown> vec = new Vector<IUnknown>();
		int a = oc.enumComponents(vec);		
		
		IUnknown unk = (IUnknown) oc.createInstance(parameters.getCanonicalSkeletonName(), parameters.getCanonicalSkeletonName());
		ILifeCycle life = (ILifeCycle) unk.QueryInterface("OpenCOM.ILifeCycle");
		life.startup(oc);
		
		Class<?> clas;
		for (int i=0; i<a; i++) {			
			clas = oc.getComponentCLSID(vec.get(i));
			if (clas.getCanonicalName().equals(parameters.getCanonicalClassNameServer())) {
				oc.connect(unk, vec.get(i), parameters.getCanonicalInterfaceNameServer());
			}
		}
	}
}