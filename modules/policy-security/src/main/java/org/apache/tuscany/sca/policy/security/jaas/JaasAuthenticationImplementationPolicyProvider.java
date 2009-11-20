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
package org.apache.tuscany.sca.policy.security.jaas;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Policy handler to handle PolicySet containing JaasAuthenticationPolicy instances
 *
 * @version $Rev$ $Date$
 */
public class JaasAuthenticationImplementationPolicyProvider implements PolicyProvider {
    private RuntimeComponent component;
    private Implementation implementation;

    public JaasAuthenticationImplementationPolicyProvider(RuntimeComponent component) {
        super();
        this.component = component;
        this.implementation = component.getImplementation();
    }

    private List<JaasAuthenticationPolicy> findPolicies(Operation op) {
        List<JaasAuthenticationPolicy> polices = new ArrayList<JaasAuthenticationPolicy>();
        /*
        // FIXME: How do we get a list of effective policySets for a given operation?
        if (implementation instanceof OperationsConfigurator) {
            OperationsConfigurator operationsConfigurator = (OperationsConfigurator)implementation;
            for (ConfiguredOperation cop : operationsConfigurator.getConfiguredOperations()) {
                if (cop.getName().equals(op.getName())) {
                    for (PolicySet ps : cop.getPolicySets()) {
                        for (Object p : ps.getPolicies()) {
                            if (JaasAuthenticationPolicy.class.isInstance(p)) {
                                polices.add((JaasAuthenticationPolicy)p);
                            }
                        }
                    }
                }
            }
        }
        */
        
        List<PolicySet> policySets = component.getPolicySets();
        for (PolicySet ps : policySets) {
            for (Object p : ps.getPolicies()) {
                if (JaasAuthenticationPolicy.class.isInstance(p)) {
                    polices.add((JaasAuthenticationPolicy)p);
                }
            }
        }
        return polices;
    }

    public PhasedInterceptor createInterceptor(Operation operation) {
        List<JaasAuthenticationPolicy> policies = findPolicies(operation);
        if (policies == null || policies.isEmpty()) {
            return null;
        } else {
            return new JaasAuthenticationInterceptor(findPolicies(operation));
        }
    }

    public void start() {
    }

    public void stop() {
    }    
}
