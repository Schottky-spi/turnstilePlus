package de.schottky.turnstile.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark that a constructor is required
 * for serialization and should therefore not be deleted.
 * Required constructors should be private and not
 * used in a regular fashion
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.CONSTRUCTOR)
public @interface RequiredConstructor {
}
