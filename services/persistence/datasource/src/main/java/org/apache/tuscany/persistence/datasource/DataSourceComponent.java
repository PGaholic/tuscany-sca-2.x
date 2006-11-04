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
package org.apache.tuscany.persistence.datasource;

import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.SystemAtomicComponentExtension;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * A system component that creates <code>DataSource</code> instances. The component uses a {@link ProviderObjectFactory}
 * to instantiate the actual <code>DataSource</code>
 *
 * @version $Rev$ $Date$
 */
public class DataSourceComponent extends SystemAtomicComponentExtension {
    private ProviderObjectFactory instanceFactory;

    /**
     * Creates a <code>DataSourceComponent</code>
     *
     * @param name            the name of the component
     * @param instanceFactory the provider factory
     * @param parent          the parent composite
     * @param scopeContainer  the scope container for implementation instances
     * @param initLevel       the initialization level
     */
    public DataSourceComponent(String name,
                               ProviderObjectFactory instanceFactory,
                               CompositeComponent parent,
                               ScopeContainer scopeContainer,
                               int initLevel) {

        super(name, parent, scopeContainer, initLevel);
        this.instanceFactory = instanceFactory;
    }

    public Object createInstance() throws ObjectCreationException {
        return instanceFactory.getInstance();
    }

    public Object getTargetInstance() throws TargetException {
        return scopeContainer.getInstance(this);
    }

    public void destroy(Object instance) throws TargetException {
        if (instance instanceof DataSourceProvider) {
            try {
                ((DataSourceProvider) instance).close();
            } catch (ProviderException e) {
                throw new TargetException(e);
            }
        }
    }

    protected void onReferenceWire(OutboundWire wire) {
        throw new UnsupportedOperationException("Wires not supported for DataSource components");
    }

    protected void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        throw new UnsupportedOperationException("Wires not supported for DataSource components");
    }

}
