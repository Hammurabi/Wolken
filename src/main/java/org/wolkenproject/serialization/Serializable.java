package org.wolkenproject.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serializable {
    FieldType type();
    // defines the serialization function over 'network'
    FieldType net() default FieldType.base;
    // defines the serialization function over 'local storage'
    FieldType local() default FieldType.base;
    // defines the serialization policy
    SerializationPolicy policy() default SerializationPolicy.All;
}
