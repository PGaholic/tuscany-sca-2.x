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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKInboundInvocationHandlerSerializationTestCase extends TestCase {
    private InboundWire wire;
    private WorkContext workContext;
    private TargetInvoker invoker;

    public void testSerializeDeserialize() throws Throwable {
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(Foo.class, wire, workContext);
        handler.invoke(Foo.class.getMethod("invoke"), null);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(stream);
        ostream.writeObject(handler);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        JDKInboundInvocationHandler externalizable = (JDKInboundInvocationHandler) istream.readObject();

        externalizable.setWorkContext(workContext);
        externalizable.reactivate();
        externalizable.invoke(Foo.class.getMethod("invoke"), null);
        EasyMock.verify(wire);
        EasyMock.verify(invoker);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wire = EasyMock.createMock(InboundWire.class);
        Map<Operation<?>, InboundInvocationChain> map = new HashMap<Operation<?>, InboundInvocationChain>();
        Operation<Object> operation = new Operation<Object>("invoke", null, null, null, false, null, NO_CONVERSATION);
        map.put(operation, createChain(operation));

        URI uri = URI.create("component#foo");
        EasyMock.expect(wire.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn(map).times(2);
        EasyMock.replay(wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getInboundWire(EasyMock.eq("foo"))).andReturn(wire);
        EasyMock.replay(component);
        workContext = new WorkContextImpl();
        workContext.setCurrentAtomicComponent(component);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workContext.setCurrentAtomicComponent(null);
    }

    private InboundInvocationChain createChain(Operation operation) {
        invoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl()).times(2);
        EasyMock.expect(invoker.isCacheable()).andReturn(false).atLeastOnce();
        EasyMock.replay(invoker);
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.setTargetInvoker(invoker);
        chain.addInterceptor(new InvokerInterceptor());
        chain.prepare();
        return chain;
    }

    public class Foo {
        public void invoke() {
        }
    }


}
