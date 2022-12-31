		//eg370.java

package com.thinking.machines.nframework.server.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Path
{
public String value();
}