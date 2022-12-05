package com.kamikazejamplugins.kamicommon.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {
    /**
     * The key of the config value
     */
    String key() default "";

    /**
     * The comment to be placed after (inline with) the config option
     */
    String inline() default "";

    /**
     * The comment(s) to be placed above the config option(s) <p>
     * In the form String[] where each element matches the keys of increasing depth <p>
     * Example: <b>key = "A.B", above = {"CommentA", "CommentB"}</b> results in the following: <p>
     * # CommentA                                 <p>
     * A:                                         <p>
     * &nbsp; # CommentB                          <p>
     * &nbsp; B: ...                              <p>
     * If you were to remove CommentB, it would only write CommentA, but to only write CommentB you need {"", "CommentB"}
     */
    String[] above() default {};
}
