package org.apache.tuscany.core.implementation.system.builder;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentBuilderResourceTestCase extends TestCase {

    @SuppressWarnings("uncjecked")
    public void testResourceInjection() throws Exception {
        ScopeContainer container = EasyMock.createNiceMock(ScopeContainer.class);
        DeploymentContext ctx = EasyMock.createNiceMock(DeploymentContext.class);
        ScopeRegistry registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(Scope.STATELESS)).andReturn(container);
        EasyMock.replay(registry);
        SystemComponentBuilder builder = new SystemComponentBuilder();
        builder.setScopeRegistry(registry);
        ConstructorDefinition<Foo> ctorDef = new ConstructorDefinition<SystemComponentBuilderResourceTestCase.Foo>(
            SystemComponentBuilderResourceTestCase.Foo.class.getConstructor());
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        Resource resource = new Resource();
        resource.setType(String.class);
        resource.setName("resource");
        resource.setMember(SystemComponentBuilderResourceTestCase.Foo.class.getDeclaredField("resource"));
        type.add(resource);
        type.setImplementationScope(Scope.STATELESS);
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setImplementationClass(SystemComponentBuilderResourceTestCase.Foo.class);
        impl.setComponentType(type);
        ComponentDefinition<SystemImplementation> definition =
            new ComponentDefinition<SystemImplementation>("foo", impl);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveSystemInstance(String.class)).andReturn("result");
        EasyMock.replay(parent);
        SystemAtomicComponent component = (SystemAtomicComponent) builder.build(parent, definition, ctx);
        SystemComponentBuilderResourceTestCase.Foo foo =
            (SystemComponentBuilderResourceTestCase.Foo) component.createInstance();
        assertEquals("result", foo.resource);
        EasyMock.verify(parent);
    }

    private static class Foo {

        protected String resource;

        public Foo() {
        }

    }
}
