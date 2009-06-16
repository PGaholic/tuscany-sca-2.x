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

package org.apache.tuscany.sca.binding.sca.axis2.impl;

import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.axis2.Axis2ReferenceBindingProvider;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * The reference binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for sending messages to remote services so this provider
 * uses the ws-axis provider under the covers. 
 */
public class Axis2SCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(Axis2SCAReferenceBindingProvider.class.getName());
    
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private Axis2ReferenceBindingProvider axisReferenceBindingProvider;
    private WebServiceBinding wsBinding;

    public Axis2SCAReferenceBindingProvider(RuntimeComponent component,
                                            RuntimeComponentReference reference,
                                            DistributedSCABinding binding,
                                            ExtensionPointRegistry extensionPoints) {

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        DataBindingExtensionPoint dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);

        this.component = component;
        this.reference = reference;
        this.binding = binding.getSCABinding();
        
        // build a ws binding model
        wsBinding = modelFactories.getFactory(WebServiceBindingFactory.class).createWebServiceBinding();
        wsBinding.setName(this.binding.getName());         
       
        // Turn the java interface contract into a WSDL interface contract
        BindingWSDLGenerator.generateWSDL(component, reference, wsBinding, extensionPoints, null);
        
        // Set to use the Axiom data binding
        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(OMElement.class.getName());
        
        // create the real Axis2 reference binding provider
        axisReferenceBindingProvider = new Axis2ReferenceBindingProvider(component,
                                                                         reference,
                                                                         wsBinding,
                                                                         modelFactories,
                                                                         dataBindings);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        return axisReferenceBindingProvider.createInvoker(operation);
    }   
    
    public SCABinding getSCABinding () {
        return binding;
    }  

    public void start() {
        axisReferenceBindingProvider.start();
    }

    public void stop() {
        axisReferenceBindingProvider.stop();
    }
}
