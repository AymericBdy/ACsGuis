package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Block {

    String name();
    String description();
    String[] examples();
    String[] fields() default {};
    String regex();
    Side side() default Side.BOTH;
    boolean reloadable() default true;

}
