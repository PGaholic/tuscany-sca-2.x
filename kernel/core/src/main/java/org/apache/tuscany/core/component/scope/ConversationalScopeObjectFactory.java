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
package org.apache.tuscany.core.component.scope;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.Store;

import org.apache.tuscany.api.annotation.Monitor;

/**
 * Creates a new Session Scope context
 *
 * @version $$Rev: 450456 $$ $$Date: 2006-09-27 10:28:36 -0400 (Wed, 27 Sep 2006) $$
 */
@EagerInit
public class ConversationalScopeObjectFactory implements ObjectFactory<ConversationalScopeContainer> {
    private WorkContext context;
    private Store store;
    private ScopeContainerMonitor monitor;

    public ConversationalScopeObjectFactory(@Reference ScopeRegistry registry,
                                            @Reference WorkContext context,
                                            @Reference Store store,
                                            @Monitor ScopeContainerMonitor monitor) {
        registry.registerFactory(Scope.CONVERSATION, this);
        this.context = context;
        this.store = store;
        this.monitor = monitor;
    }

    public ConversationalScopeContainer getInstance() throws ObjectCreationException {
        return new ConversationalScopeContainer(store, context, monitor);
    }
}
