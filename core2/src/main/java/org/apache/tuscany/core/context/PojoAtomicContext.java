package org.apache.tuscany.core.context;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.event.InstanceCreated;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class PojoAtomicContext extends AbstractContext implements AtomicContext {

    private ScopeContext<AtomicContext> scopeContext;

    private boolean eagerInit;

    private EventInvoker<Object> initInvoker;

    private EventInvoker<Object> destroyInvoker;


    private ObjectFactory<?> objectFactory;

    public PojoAtomicContext(String name, ScopeContext<AtomicContext> scopeContext, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker) {
        super(name);
        assert (scopeContext != null) : "Scope context was null";
        assert (objectFactory != null) : "Object factory was null";
        if (eagerInit && initInvoker == null) {
            throw new AssertionError("No intialization method found for eager init implementation");
        }
        this.scopeContext = scopeContext;
        this.objectFactory = objectFactory;
        this.eagerInit = eagerInit;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void init(Object instance) throws TargetException {
        if (initInvoker != null) {
            initInvoker.invokeEvent(instance);
        }
    }

    public void destroy(Object instance) throws TargetException {
        if (destroyInvoker != null) {
            destroyInvoker.invokeEvent(instance);
        }
    }

    public boolean isDestroyable() {
        return (destroyInvoker != null);
    }

    public Object getTargetInstance() throws TargetException {
        return scopeContext.getInstance(this);
    }

    public Scope getScope() {
        return scopeContext.getScope();
    }

    public void addProperty(String propertyName, Object value) {

    }

    public InstanceContext createInstance() throws ObjectCreationException {
        InstanceContext ctx = new PojoInstanceContext(objectFactory.getInstance());
        ctx.start();
        publish(new InstanceCreated(this, ctx));
        return ctx;
    }


}
