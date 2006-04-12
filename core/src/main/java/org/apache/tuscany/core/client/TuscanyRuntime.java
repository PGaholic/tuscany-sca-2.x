/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.client;

import java.util.List;

import org.osoa.sca.ModuleContext;
import org.osoa.sca.SCA;
import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.LogLevel;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.event.ModuleStartEvent;
import org.apache.tuscany.core.context.event.RequestStartEvent;
import org.apache.tuscany.core.context.event.RequestEndEvent;
import org.apache.tuscany.core.context.event.ModuleStopEvent;
import org.apache.tuscany.core.context.event.HttpSessionBoundEvent;
import org.apache.tuscany.core.context.event.HttpSessionEndEvent;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Create and initialize a Tuscany SCA runtime environment.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyRuntime extends SCA {
    private final Monitor monitor;
    private final Object sessionKey = new Object();

    private final RuntimeContext runtime;
    private final CompositeContext moduleContext;

    private static final String SYSTEM_MODULE_COMPONENT = "org.apache.tuscany.core.system";

    /**
     * Construct a runtime using a null MonitorFactory.
     *
     * @param name the name of the module component
     * @param uri  the URI to assign to the module component
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     * @see TuscanyRuntime#TuscanyRuntime(String, String, org.apache.tuscany.common.monitor.MonitorFactory)
     */
    public TuscanyRuntime(String name, String uri) throws ConfigurationException {
        this(name, uri, new NullMonitorFactory());
    }

    /**
     * Construct a runtime containing a single module component with the
     * specified name. The module definition is loaded from a "/sca.module"
     * resource found on the classpath of the current Thread context classloader.
     *
     * @param name           the name of the module component
     * @param uri            the URI to assign to the module component
     * @param monitorFactory the MonitorFactory for this runtime
     * @throws ConfigurationException if there was a problem loading the SCA configuration
     */
    public TuscanyRuntime(String name, String uri, MonitorFactory monitorFactory) throws ConfigurationException {
        this.monitor = monitorFactory.getMonitor(Monitor.class);

        // Create an assembly model context
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        AssemblyModelContext modelContext = BootstrapHelper.getModelContext(classLoader);

        // Create a runtime context and start it
        List<ContextFactoryBuilder> configBuilders = BootstrapHelper.getBuilders(monitorFactory);
        runtime = new RuntimeContextImpl(monitorFactory, configBuilders, new DefaultWireBuilder());
        runtime.start();

        // Load and start the system configuration
        SystemCompositeContext systemContext = runtime.getSystemContext();
        BootstrapHelper.bootstrapStaxLoader(systemContext, modelContext);
        ModuleComponentConfigurationLoader loader = BootstrapHelper.getConfigurationLoader(systemContext, modelContext);
        ModuleComponent systemModuleComponent = loader.loadSystemModuleComponent(SYSTEM_MODULE_COMPONENT, SYSTEM_MODULE_COMPONENT);
        CompositeContext context = BootstrapHelper.registerModule(systemContext, systemModuleComponent);
        context.publish(new ModuleStartEvent(this));


        // Load the SCDL configuration of the application module
        CompositeContext rootContext = runtime.getRootContext();
        ModuleComponent moduleComponent = loader.loadModuleComponent(name, uri);
        moduleContext = BootstrapHelper.registerModule(rootContext, moduleComponent);
    }

    /**
     * Start the runtime and associate the module context with the calling thread.
     */
    @Override
    public void start() {
        setModuleContext((ModuleContext) moduleContext);
        try {
            //moduleContext.start();
            moduleContext.publish(new ModuleStartEvent(this));
            moduleContext.publish(new RequestStartEvent(this, new Object()));
            moduleContext.publish(new HttpSessionBoundEvent(this, sessionKey));
            monitor.moduleStarted(moduleContext.getName());
        } catch (CoreRuntimeException e) {
            setModuleContext(null);
            monitor.moduleStartFailed(moduleContext.getName(), e);
            //FIXME throw a better exception
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Disassociate the module context from the current thread and shut down the runtime.
     */
    @Override
    public void stop() {
        setModuleContext(null);
        moduleContext.publish(new RequestEndEvent(this, new Object()));
        moduleContext.publish(new HttpSessionEndEvent(this, sessionKey));
        moduleContext.publish(new ModuleStopEvent(this));
        moduleContext.stop();
        monitor.moduleStopped(moduleContext.getName());
    }

    /**
     * Shut down the Tuscany runtime.
     */
    public void shutdown() {
        runtime.stop();
    }

    /**
     * Monitor interface for a TuscanyRuntime.
     */
    public static interface Monitor {
        /**
         * Event emitted after an application module has been started.
         *
         * @param name the name of the application module
         */
        @LogLevel("INFO")
        void moduleStarted(String name);

        /**
         * Event emitted when an attempt to start an application module failed.
         *
         * @param name the name of the application module
         * @param e    the exception that caused the failure
         */
        @LogLevel("SEVERE")
        void moduleStartFailed(String name, CoreRuntimeException e);

        /**
         * Event emitted after an application module has been stopped.
         *
         * @param name the name of the application module
         */
        @LogLevel("INFO")
        void moduleStopped(String name);
    }
}
