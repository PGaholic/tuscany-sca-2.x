package org.apache.tuscany.core.implementation.processor;

import java.util.Collection;
import java.util.List;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.implementation.ConstructorDefinition;

/**
 * Verfies component type information is properly introspected from an unadorned POJO according to the SCA Java Client
 * and Implementation Model Specification
 *
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessorTestCase extends TestCase {

    private HeuristicPojoProcessor processor = new HeuristicPojoProcessor();

    /**
     * Verifies a single service interface is computed when only one interface is implemented
     */
    @SuppressWarnings("unchecked")
    public void testSingleInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, SingleInterfaceImpl.class, type, null);
        assertEquals(1, type.getServices().size());
        assertEquals(PropertyInterface.class,
            type.getServices().get("HeuristicPojoProcessorTestCase$PropertyInterface")
                .getServiceContract().getInterfaceClass());
        assertTrue(type.getProperties().isEmpty());
        assertTrue(type.getReferences().isEmpty());
    }

    /**
     * Verifies property and reference setters are computed
     */
    @SuppressWarnings("unchecked")
    public void testPropertyReference() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, SingleInterfaceWithPropertyReferenceImpl.class, type, null);
        assertEquals(1, type.getServices().size());
        assertEquals(Interface1.class,
            type.getServices().get("HeuristicPojoProcessorTestCase$Interface1")
                .getServiceContract().getInterfaceClass());
        assertEquals(1, type.getProperties().size());
        assertEquals(ComplexProperty.class, type.getProperties().get("property").getJavaType());
        assertEquals(1, type.getReferences().size());
        assertEquals(Ref.class, type.getReferences().get("reference").getServiceContract().getInterfaceClass());
    }

    /**
     * Verifies that a property setter is not introspected if an analogous operation is in the service interface
     */
    @SuppressWarnings("unchecked")
    public void testPropertySetterInInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, SingleInterfaceImpl.class, type, null);
        assertEquals(0, type.getProperties().size());
    }

    /**
     * Verifies that a reference setter is not introspected if an analogous operation is in the service interface
     */
    @SuppressWarnings("unchecked")
    public void testReferenceSetterInInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, RefInterfaceImpl.class, type, null);
        assertEquals(0, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as references according to spec rules
     */
    @SuppressWarnings("unchecked")
    public void testReferenceCollectionType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, ReferenceCollectionImpl.class, type, null);
        assertEquals(0, type.getProperties().size());
        assertEquals(4, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as properties according to spec rules
     */
    @SuppressWarnings("unchecked")
    public void testPropertyCollectionType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, PropertyCollectionImpl.class, type, null);
        assertEquals(0, type.getReferences().size());
        assertEquals(4, type.getProperties().size());
    }

    /**
     * Verifies references are calculated when the type marked with is @Remotable
     */
    @SuppressWarnings("unchecked")
    public void testRemotableRef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, RemotableRefImpl.class, type, null);
        assertEquals(2, type.getReferences().size());
        assertEquals(0, type.getProperties().size());
    }

    @SuppressWarnings("unchecked")
    public void testParentInterface() throws ProcessingException {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setConstructorDefinition(new ConstructorDefinition(null));
        processor.visitEnd(null, Child.class, type, null);
        assertTrue(type.getServices().containsKey("HeuristicPojoProcessorTestCase$Interface1"));
    }

    private interface PropertyInterface {
        void setString1(String val);
    }

    private interface Interface1 {
    }

    private class Parent implements Interface1 {

    }

    private class Child extends Parent {

    }

    private class SingleInterfaceImpl implements PropertyInterface {
        public void setString1(String val) {
        }
    }

    private interface HeuristicServiceInterface {
        void fooOperation(String ref);
    }

    public static class ServiceImpl implements PropertyInterface, RefInterface, HeuristicServiceInterface {

        @Property
        public void setString1(String val) {
        }

        @Property
        public void setString2(String val) {
        }

        @Reference
        public void setReference(Ref ref) {
        }

        @Reference
        public void setReference2(Ref ref) {
        }

        public void fooOperation(String ref) {

        }

    }

    /**
     * Verifies a service inteface is calculated when only props and refs are given
     */
    public void testExcludedPropertyAndReference() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        JavaMappedReference ref = new JavaMappedReference();
        ref.setName("reference");
        type.add(ref);
        JavaMappedReference ref2 = new JavaMappedReference();
        ref2.setName("reference2");
        type.add(ref2);
        JavaMappedProperty<?> prop1 = new JavaMappedProperty();
        prop1.setName("string1");
        type.add(prop1);
        JavaMappedProperty<?> prop2 = new JavaMappedProperty();
        prop2.setName("string2");
        type.add(prop2);
        processor.visitEnd(null, ServiceImpl.class, type, null);
        assertEquals(1, type.getServices().size());
    }

    @Service
    private interface Ref {
    }

    private class ComplexProperty {
    }

    private interface RefInterface {
        void setReference(Ref ref);
    }

    private class RefInterfaceImpl implements RefInterface {
        public void setReference(Ref ref) {
        }
    }

    private class SingleInterfaceWithPropertyReferenceImpl implements Interface1 {
        public void setReference(Ref ref) {
        }

        public void setProperty(ComplexProperty prop) {
        }
    }

    private class ReferenceCollectionImpl implements Interface1 {
        public void setCollectionReference(Collection<Ref> ref) {
        }

        public void setNonGenericCollectionReference(Collection ref) {
        }

        public void setListReference(List<Ref> ref) {
        }

        public void setArrayReference(Ref[] ref) {
        }
    }

    private class PropertyCollectionImpl implements Interface1 {
        public void setCollectionProperty(Collection<ComplexProperty> prop) {
        }

        public void setCollectionProperty2(Collection<String> prop) {
        }

        public void setArrayProperty(ComplexProperty[] prop) {
        }

        public void setArrayProperty2(String[] prop) {
        }
    }

    @Remotable
    private interface RemotableRef {
    }

    private class RemotableRefImpl implements Interface1 {

        protected RemotableRef otherRef;

        public void setRef(RemotableRef ref) {

        }
    }

}
