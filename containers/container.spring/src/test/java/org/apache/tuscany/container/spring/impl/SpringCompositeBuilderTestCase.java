/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.container.spring.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import static org.apache.tuscany.container.spring.SpringTestUtils.createContext;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.VMBinding;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.apache.tuscany.test.ArtifactFactory;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilderTestCase extends TestCase {

    /**
     * Verfies basic build of a spring context
     */
    public void testBuild() throws Exception {
        // Create an assembly model consisting of a component implemented by Spring
        SpringImplementation impl = new SpringImplementation(new SpringComponentType(createContext()));
        ComponentDefinition<SpringImplementation> componentDefinition =
            new ComponentDefinition<SpringImplementation>("spring", impl);

        // Configure the mock builder registry
        BuilderRegistry registry = createNiceMock(BuilderRegistry.class);

        // Test the SpringCompositeBuilder
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setBuilderRegistry(registry);
        CompositeComponent parent = createNiceMock(CompositeComponent.class);
        DeploymentContext context = createNiceMock(DeploymentContext.class);
        SpringCompositeComponent component =
            (SpringCompositeComponent) builder.build(parent, componentDefinition, context);
        TestBean bean = (TestBean) component.getApplicationContext().getBean("foo");
        assertEquals("call foo", bean.echo("call foo"));
    }

    /**
     * Verifies that the builder calls back into the registry to load services and wires them to bean targets when no
     * <code>sca:service</code> tag is specified in the Spring application.xml
     */
    public void testImplicitServiceWiring() throws Exception {

        // Create an assembly model consisting of a component implemented by Spring
        SpringImplementation impl = new SpringImplementation(createComponentType());
        ComponentDefinition<SpringImplementation> componentDefinition =
            new ComponentDefinition<SpringImplementation>("spring", impl);

        // Create a service instance that the mock builder registry will return
        WireService wireService = ArtifactFactory.createWireService();
        ServiceExtension<TestBean> serviceContext =
            new ServiceExtension<TestBean>("fooService", TestBean.class, null, wireService);
        InboundWire<TestBean> inboundWire = ArtifactFactory.createInboundWire("fooService", TestBean.class);
        OutboundWire<TestBean> outboundWire = ArtifactFactory.createOutboundWire("fooService", TestBean.class);
        ArtifactFactory.terminateWire(outboundWire);
        serviceContext.setInboundWire(inboundWire);
        serviceContext.setOutboundWire(outboundWire);
        Connector connector = ArtifactFactory.createConnector();
        connector.connect(inboundWire, outboundWire, true);

        // Configure the mock builder registry
        BuilderRegistry registry = createMock(BuilderRegistry.class);
        expect(registry.build(isA(CompositeComponent.class),
            isA(BoundServiceDefinition.class),
            isA(DeploymentContext.class))).andStubReturn(serviceContext);
        replay(registry);

        // Test the SpringCompositeBuilder
        SpringCompositeBuilder builder = new SpringCompositeBuilder();
        builder.setWireService(wireService);
        builder.setBuilderRegistry(registry);
        CompositeComponent parent = createNiceMock(CompositeComponent.class);
        DeploymentContext context = createNiceMock(DeploymentContext.class);
        CompositeComponent component = (CompositeComponent) builder.build(parent, componentDefinition, context);
        Service service = component.getService("fooService");
        TestBean bean = (TestBean) service.getServiceInstance();
        assertEquals("call foo", bean.echo("call foo"));
        verify(registry);
    }

    private SpringComponentType createComponentType() {
        SpringComponentType componentType = new SpringComponentType(createContext());
        BoundServiceDefinition<VMBinding> serviceDefinition = new BoundServiceDefinition<VMBinding>();
        serviceDefinition.setName("fooService");
        serviceDefinition.setBinding(new VMBinding());
        try {
            serviceDefinition.setTarget(new URI("foo"));
        } catch (URISyntaxException e) {
            throw new AssertionError();
        }
        componentType.add(serviceDefinition);
        return componentType;
    }

}
