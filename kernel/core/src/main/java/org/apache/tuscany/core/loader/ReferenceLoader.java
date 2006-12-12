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

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * Loads a reference from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ReferenceLoader extends LoaderExtension<ReferenceDefinition> {
    public static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");

    @Constructor({"registry"})
    public ReferenceLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return REFERENCE;
    }

    public ReferenceDefinition load(CompositeComponent parent,
                                    ModelObject object, XMLStreamReader reader,
                                    DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert REFERENCE.equals(reader.getName());

        String name = reader.getAttributeValue(null, "name");
        Multiplicity multiplicity =
            StAXUtil.multiplicity(reader.getAttributeValue(null, "multiplicity"), Multiplicity.ONE_ONE);
        Binding binding = null;
        ServiceContract serviceContract = null;
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(parent, null, reader, deploymentContext);
                    if (o instanceof ServiceContract) {
                        serviceContract = (ServiceContract) o;
                    } else if (o instanceof Binding) {
                        binding = (Binding) o;
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (binding == null) {
                        ReferenceDefinition referenceDefinition = new ReferenceDefinition(name, serviceContract);
                        referenceDefinition.setMultiplicity(multiplicity);
                        return referenceDefinition;
                    } else {
                        BoundReferenceDefinition<Binding> referenceDefinition = new BoundReferenceDefinition<Binding>();
                        referenceDefinition.setName(name);
                        referenceDefinition.setServiceContract(serviceContract);
                        referenceDefinition.setMultiplicity(multiplicity);
                        referenceDefinition.setBinding(binding);
                        return referenceDefinition;
                    }
            }
        }
    }
}
