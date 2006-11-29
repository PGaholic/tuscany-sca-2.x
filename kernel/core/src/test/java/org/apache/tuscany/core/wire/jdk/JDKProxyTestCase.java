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
package org.apache.tuscany.core.wire.jdk;

import java.util.HashMap;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * @version $Rev$ $Date$
 */
public class JDKProxyTestCase extends TestCase {
    private JDKWireService wireService;
    private JavaServiceContract serviceContract;
    private InboundWire inboundWire;
    private HashMap<Operation<?>,InboundInvocationChain> chains;

    public void testCreateProxy() {
        EasyMock.expect(inboundWire.getServiceName()).andReturn("service");
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(serviceContract);
        EasyMock.expect(inboundWire.getInvocationChains()).andReturn(chains);
        EasyMock.replay(inboundWire);
        TestInterface intf = wireService.createProxy(TestInterface.class, inboundWire);
        assertTrue(Proxy.isProxyClass(intf.getClass()));
        EasyMock.verify(inboundWire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new JDKWireService();
        inboundWire = EasyMock.createMock(InboundWire.class);
        serviceContract = new JavaServiceContract(TestInterface.class);
        chains = new HashMap<Operation<?>, InboundInvocationChain>();
    }

    public static interface TestInterface {
        int primitives(int i);

        String objects(String object);
    }
}
