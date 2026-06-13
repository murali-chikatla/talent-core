package com.nexora.rsp.talentcore.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchField {

    String path() default "";

    SearchOperation operation() default SearchOperation.EQUAL;

    Class<? extends SearchValueTransformer> transformer() default IdentitySearchValueTransformer.class;
}
