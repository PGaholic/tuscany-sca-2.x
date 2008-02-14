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
package org.apache.tuscany.sca.binding.jms.mock;

import javax.jms.Destination;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.easymock.EasyMock;

/**
 * This mock JMSResourceFactory will create a mock Destination when the lookupDestination() method is called
 */
public class MockJMSResourceFactoryQueueExist extends MockJMSResourceFactory {
    /**
     * Constructor
     * 
     * @param jmsBinding Ignored
     */
    public MockJMSResourceFactoryQueueExist(JMSBinding jmsBinding) {
    }

    /**
     * Return a mock Destination object
     */
    public Destination lookupDestination(String jndiName) throws NamingException {
        final Destination d = EasyMock.createMock(Destination.class);
        EasyMock.replay(d);
        return d;
    }

    public Object startBroker() {
        return null;
    }
    
    public void stopBroker(Object broker) {
    }
}
