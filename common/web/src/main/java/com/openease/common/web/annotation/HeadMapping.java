package com.openease.common.web.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping HTTP {@code HEAD} requests onto specific handler
 * methods.
 *
 * <p>Specifically, {@code @HeadMapping} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @RequestMapping(method = RequestMethod.HEAD)}.
 *
 * @author Alan Czajkowski
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see PatchMapping
 * @see RequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.HEAD)
public @interface HeadMapping {

  /**
   * Alias for {@link RequestMapping#name}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String name() default "";

  /**
   * Alias for {@link RequestMapping#value}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] value() default {};

  /**
   * Alias for {@link RequestMapping#path}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] path() default {};

  /**
   * Alias for {@link RequestMapping#params}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] params() default {};

  /**
   * Alias for {@link RequestMapping#headers}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] headers() default {};

  /**
   * Alias for {@link RequestMapping#consumes}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] consumes() default {};

  /**
   * Alias for {@link RequestMapping#produces}.
   */
  @AliasFor(annotation = RequestMapping.class)
  String[] produces() default {};

}
