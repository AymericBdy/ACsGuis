package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {

    String name();
    String[] description();
    String[] examples();
    String[] patterns();
    Side side() default Side.BOTH;

    /**
     * Set the priority at which the expression will be checked.
     * Highest priorities are checked first.
     * @return The priority of this expression.
     */
    int priority() default 0;


}
