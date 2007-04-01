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

package org.apache.tuscany.scdl.stax.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Callback;
import org.apache.tuscany.assembly.model.Component;
import org.apache.tuscany.assembly.model.ComponentProperty;
import org.apache.tuscany.assembly.model.ComponentReference;
import org.apache.tuscany.assembly.model.ComponentService;
import org.apache.tuscany.assembly.model.Composite;
import org.apache.tuscany.assembly.model.CompositeReference;
import org.apache.tuscany.assembly.model.CompositeService;
import org.apache.tuscany.assembly.model.Contract;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.assembly.model.Wire;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.sca.idl.Operation;
import org.apache.tuscany.scdl.stax.Loader;
import org.apache.tuscany.scdl.stax.LoaderRegistry;

/**
 * A composite content handler.
 * 
 * @version $Rev$ $Date$
 */
public class CompositeLoader extends BaseLoader implements Loader<Composite> {

	/**
	 * Construct a new composite loader
	 * @param assemblyFactory 
	 * @param policyFactory
	 * @param registry
	 */
    public CompositeLoader(AssemblyFactory assemblyFactory, PolicyFactory policyFactory, LoaderRegistry registry) {
        super(assemblyFactory, policyFactory, registry);
    }

    /**
     * Read an SCA composite
     * @param reader
     * @return a composite model
     * @throws XMLStreamException
     */
    public Composite load(XMLStreamReader reader) throws XMLStreamException {
        Composite composite = null;
        Composite include = null;
        Component component = null;
        Property property = null;
        ComponentService componentService = null;
        ComponentReference componentReference = null;
        ComponentProperty componentProperty = null;
        CompositeService compositeService = null;
        CompositeReference compositeReference = null;
        Contract contract = null;
        Wire wire = null;
        Callback callback = null;
        QName name = null;

        // Read the composite document
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    name = reader.getName();
                    
                    if (COMPOSITE_QNAME.equals(name)) {
                    	
                    	// Read a <composite>
                        composite = factory.createComposite();
                        composite.setName(getQName(reader, NAME));
                        composite.setAutowire(getBoolean(reader, AUTOWIRE));
                        composite.setLocal(getBoolean(reader, LOCAL));
                        composite.setConstrainingType(getConstrainingType(reader));
                        readPolicies(composite, reader);
                        
                    } else if (INCLUDE_QNAME.equals(name)) {
                    	
                    	// Read an <include> 
                        include = factory.createComposite();
                        include.setUnresolved(true);
                        composite.getIncludes().add(include);
                        
                    } else if (SERVICE_QNAME.equals(name)) {
                        if (component != null) {
                        	
                        	// Read a <component><service>
                            componentService = factory.createComponentService();
                            contract = componentService;
                            componentService.setName(getString(reader, NAME));
                            component.getServices().add(componentService);
                            readPolicies(contract, reader);
                        } else {
                        	
                        	// Read a <composite><service>
                            compositeService = factory.createCompositeService();
                            contract = compositeService;
                            compositeService.setName(getString(reader, NAME));

                            ComponentService promoted = factory.createComponentService();
                            promoted.setUnresolved(true);
                            promoted.setName(getString(reader, PROMOTE));
                            compositeService.setPromotedService(promoted);

                            composite.getServices().add(compositeService);
                            readPolicies(contract, reader);
                        }

                    } else if (REFERENCE_QNAME.equals(name)) {
                        if (component != null) {
                        	
                        	// Read a <component><reference>
                            componentReference = factory.createComponentReference();
                            contract = componentReference;
                            componentReference.setName(getString(reader, NAME));

                            // TODO support multivalued attribute
                            ComponentService target = factory.createComponentService();
                            target.setUnresolved(true);
                            target.setName(getString(reader, TARGET));
                            componentReference.getTargets().add(target);

                            component.getReferences().add(componentReference);
                            readPolicies(contract, reader);
                        } else {
                        	
                        	// Read a <composite><reference>
                            compositeReference = factory.createCompositeReference();
                            contract = compositeReference;
                            compositeReference.setName(getString(reader, NAME));

                            // TODO support multivalued attribute
                            ComponentReference promoted = factory.createComponentReference();
                            promoted.setUnresolved(true);
                            promoted.setName(getString(reader, PROMOTE));
                            compositeReference.getPromotedReferences().add(promoted);

                            composite.getReferences().add(compositeReference);
                            readPolicies(contract, reader);
                        }
                        
                    } else if (PROPERTY_QNAME.equals(name)) {
                        if (component != null) {
                        	
                        	// Read a <component><property>
                            componentProperty = factory.createComponentProperty();
                            property = componentProperty;
                            readProperty(componentProperty, reader);
                            component.getProperties().add(componentProperty);
                        } else {
                        	
                        	// Read a <composite><property>
                            property = factory.createProperty();
                            readProperty(property, reader);
                            composite.getProperties().add(property);
                        }
                        readPolicies(property, reader);
                        
                    } else if (COMPONENT_QNAME.equals(name)) {
                    	
                    	// Read a <component>
                        component = factory.createComponent();
                        component.setName(getString(reader, NAME));
                        component.setConstrainingType(getConstrainingType(reader));
                        composite.getComponents().add(component);
                        readPolicies(component, reader);
                        
                    } else if (WIRE_QNAME.equals(name)) {
                    	
                    	// Read a <wire>
                        wire = factory.createWire();
                        ComponentReference source = factory.createComponentReference();
                        source.setUnresolved(true);
                        source.setName(getString(reader, SOURCE));
                        wire.setSource(source);

                        ComponentService target = factory.createComponentService();
                        target.setUnresolved(true);
                        target.setName(getString(reader, TARGET));
                        wire.setTarget(target);

                        composite.getWires().add(wire);
                        readPolicies(wire, reader);
                        
                    } else if (CALLBACK_QNAME.equals(name)) {
                    	
                    	// Read a <callback> 
                        callback = factory.createCallback();
                        contract.setCallback(callback);
                        readPolicies(callback, reader);
                        
        	        } else if (OPERATION.equals(name)) {
        	        	
        	        	// Read an <operation>
                		Operation operation = factory.createOperation();
                		operation.setName(getString(reader, NAME));
                		operation.setUnresolved(true);
                		if (callback != null) {
                			readPolicies(callback, operation, reader);
                		} else {
                			readPolicies(contract, operation, reader);
                		}
                    } else {
                    	
                        // Read extension elements
                        // <service><interface>
                        // <service><binding>
                    	// <component><implementation>
                        if (nextChildElement(reader)) {
                            registry.load(reader);
                            continue;
                        }
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                	
                	// Read an <include>qname</include>
                    if (include != null && INCLUDE_QNAME.equals(name)) {
                        include.setName(getQNameValue(reader, reader.getText().trim()));
                    }
                    
                    // Read a <property>value</property>
                    else if (property != null && PROPERTY_QNAME.equals(name)) {
                        property.setDefaultValue(reader.getText().trim());
                    }
                    break;
                
                case END_ELEMENT:
                    name = reader.getName();
                	
                	// Clear current state when reading reaching end element
                    if (SERVICE_QNAME.equals(name)) {
                        componentService = null;
                        compositeService = null;
                        contract = null;
                    } else if (INCLUDE_QNAME.equals(name)) {
                        include = null;
                    } else if (REFERENCE_QNAME.equals(name)) {
                        componentReference = null;
                        compositeReference = null;
                        contract = null;
                    } else if (PROPERTY_QNAME.equals(name)) {
                        componentProperty = null;
                        property = null;
                    } else if (COMPONENT_QNAME.equals(name)) {
                        component = null;
                    } else if (WIRE_QNAME.equals(name)) {
                        wire = null;
                    } else if (CALLBACK_QNAME.equals(name)) {
                        callback = null;
                    }
                    break;
            }
            
            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return composite;
    }

}
