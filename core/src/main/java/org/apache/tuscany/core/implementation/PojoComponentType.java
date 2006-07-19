package org.apache.tuscany.core.implementation;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * A component type specialization for POJO implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType<S extends ServiceDefinition, R extends ReferenceDefinition,  P extends Property<?>>
    extends ComponentType<S, R, P> {

    private Scope implementationScope = Scope.UNDEFINED;
    private ConstructorDefinition constructorDefinition;
    private Method initMethod;
    private Method destroyMethod;

    /**
     * Returns the component implementation scope
     */
    public Scope getImplementationScope() {
        return implementationScope;
    }

    /**
     * Sets the component implementation scope
     */
    public void setImplementationScope(Scope implementationScope) {
        this.implementationScope = implementationScope;
    }

    /**
     * Returns the constructor used to instantiate implementation instances
     */
    public ConstructorDefinition<?> getConstructorDefinition() {
        return constructorDefinition;
    }

    /**
     * Sets the constructor used to instantiate implementation instances
     */
    public void setConstructorDefinition(ConstructorDefinition definition) {
        this.constructorDefinition = definition;
    }

    /**
     * Returns the component initializer method
     */
    public Method getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the component initializer method
     */
    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Returns the component destructor method
     */
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the component destructor method
     */
    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

}
