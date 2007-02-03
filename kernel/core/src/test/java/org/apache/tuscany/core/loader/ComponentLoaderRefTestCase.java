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
package org.apache.tuscany.core.loader;

import java.net.URI;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderRefTestCase extends TestCase {
    private ComponentLoader loader;

    public void testLoadReferenceNoFragment() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getElementText()).andReturn("target");
        EasyMock.replay(reader);
        loader.loadReference(reader, null, definition);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertEquals(1, target.getTargets().size());
        URI uri = target.getTargets().get(0);
        assertEquals("target", uri.getPath());
        assertNull(uri.getFragment());
        EasyMock.verify(reader);
    }

    public void testLoadReferenceWithFragment() throws LoaderException, XMLStreamException {
        PojoComponentType<?, MockReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, MockReferenceDefinition, Property<?>>();
        MockReferenceDefinition reference = new MockReferenceDefinition();
        reference.setUri(URI.create("#reference"));
        type.add(reference);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> definition = new ComponentDefinition<JavaImplementation>(impl);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("reference");
        EasyMock.expect(reader.getElementText()).andReturn("target#fragment");
        EasyMock.replay(reader);
        loader.loadReference(reader, null, definition);
        ReferenceTarget target = definition.getReferenceTargets().get("reference");
        assertEquals(1, target.getTargets().size());
        URI uri = target.getTargets().get(0);
        assertEquals("target", uri.getPath());
        assertEquals("fragment", uri.getFragment());
        EasyMock.verify(reader);
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ComponentLoader(mockRegistry, null);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        URI uri = URI.create("foo");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(parent);
    }

    private class MockReferenceDefinition extends ReferenceDefinition {

    }
}
