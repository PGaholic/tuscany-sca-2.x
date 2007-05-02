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

package org.apache.tuscany.databinding.axiom.module;

import java.util.Map;

import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.axiom.AxiomDataBinding;
import org.apache.tuscany.databinding.axiom.OMElement2Object;
import org.apache.tuscany.databinding.axiom.OMElement2String;
import org.apache.tuscany.databinding.axiom.OMElement2XMLStreamReader;
import org.apache.tuscany.databinding.axiom.Object2OMElement;
import org.apache.tuscany.databinding.axiom.String2OMElement;
import org.apache.tuscany.databinding.axiom.XMLStreamReader2OMElement;

/**
 * Module activator for AXIOM databinding
 * 
 * @version $Rev$ $Date$
 */
public class AxiomDataBindingModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindingRegistry = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        dataBindingRegistry.addDataBinding(new AxiomDataBinding());

        TransformerExtensionPoint transformerRegistry = registry.getExtensionPoint(TransformerExtensionPoint.class);
        transformerRegistry.addTransformer(new Object2OMElement());
        transformerRegistry.addTransformer(new OMElement2Object());
        transformerRegistry.addTransformer(new OMElement2String());
        transformerRegistry.addTransformer(new OMElement2XMLStreamReader());
        transformerRegistry.addTransformer(new String2OMElement());
        transformerRegistry.addTransformer(new XMLStreamReader2OMElement());
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
