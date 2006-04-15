package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.container.java.invocation.mock.MockHandler;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.invocation.mock.SimpleTarget;
import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.container.java.mock.MockScopeContext;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.WireConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

public class JavaTargetWireBuilderTestCase extends TestCase {

    private Method hello;

    public JavaTargetWireBuilderTestCase() {
    }

    public JavaTargetWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }
    
    
    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvocation() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addSourceInterceptor(sourceInterceptor);

        ProxyFactory sourceFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> sourceInvocationConfigs = new MethodHashMap();
        sourceInvocationConfigs.put(hello, source);
        WireConfiguration sourceConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setProxyConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);
        
        InvocationConfiguration target = new InvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        ProxyFactory targetFactory = new JDKProxyFactory();
        Map<Method, InvocationConfiguration> targetInvocationConfigs = new MethodHashMap();
        targetInvocationConfigs.put(hello, target);
        WireConfiguration targetConfig = new WireConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setProxyConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // bootstrap a scope container with the target in it
        Map<String,Object> instances = new HashMap<String,Object>();
        SimpleTarget simpleTarget = new SimpleTargetImpl();
        instances.put("target",simpleTarget);
        MockScopeContext scopeCtx = new MockScopeContext(instances);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        builder.addWireBuilder(new JavaTargetWireBuilder());
        
        builder.connect(sourceFactory, targetFactory, JavaContextFactory.class, true, scopeCtx);
        source.build();
        target.build();
        Assert.assertNotNull(source.getTargetInvoker());
        
        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(source.getTargetInvoker());
        Message response = source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }


}

