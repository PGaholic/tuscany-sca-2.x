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
package org.apache.tuscany.core.services.management.jmx;

import java.net.URI;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.services.management.ManagementService;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * JMX implementation of the management service.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class JmxManagementService implements ManagementService {

    /**
     * @see org.apache.tuscany.spi.services.management.ManagementService#registerComponent(java.net.URI, java.lang.String, org.apache.tuscany.spi.component.Component)
     */
    public void registerComponent(URI compositeURI, String name, Component component) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Starts the agent connector for the service.
     */
    @Init
    public abstract void start();
    
    /**
     * Stops the agent connector for the service.
     */
    @Destroy
    public abstract void stop();

}
