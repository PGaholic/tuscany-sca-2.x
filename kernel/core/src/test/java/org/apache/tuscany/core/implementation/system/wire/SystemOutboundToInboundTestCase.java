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
package org.apache.tuscany.core.implementation.system.wire;

import org.apache.tuscany.spi.QualifiedName;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.easymock.EasyMock;

/**
 * Tests connecting an outbound system wire to an inbound system wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemOutboundToInboundTestCase extends TestCase {

    public void testWire() throws Exception {
        Target target = new TargetImpl();
        SystemInboundWire inboundWire = EasyMock.createMock(SystemInboundWire.class);
        EasyMock.expect(inboundWire.getTargetService()).andReturn(target);
        EasyMock.replay(inboundWire);
        QualifiedName qName = new QualifiedName("service");
        SystemOutboundWire outboundWire = new SystemOutboundWireImpl("setTarget", qName, Target.class);
        outboundWire.setTargetWire(inboundWire);
        assertSame(outboundWire.getTargetService(), target);
        EasyMock.verify(inboundWire);
    }

}
