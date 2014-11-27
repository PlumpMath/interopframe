package interopframe.core;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;

public interface IBindingGenerator {
	public StringWriter getWriterISkeleton();
	public VelocityContext getSkeletonContext();
	public VelocityContext getProxyContext();
}