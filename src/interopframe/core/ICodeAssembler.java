package interopframe.core;

import interopframe.api.Parameters;

public interface ICodeAssembler {
	public void mountClient(Parameters p, Object runtime);
	public void mountServer(Parameters p, Object runtime);
}