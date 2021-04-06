package com.tl.validation;

import javax.ws.rs.BadRequestException;
import java.lang.reflect.Field;

public class Validation {

    /**
     * This method checks whether all @Required-Annotated fields are in fact not null.
     * @param instance  The instance to check
     * @param <T> The class of the instance to check
     */
    public static <T> void checkForNull(T instance) {
        checkFields(instance.getClass().getDeclaredFields(), instance);
    }

    /**
     * This method checks whether all @Required-Annotated fields are in fact not null.
     * @param fields The fields to check
     * @param instance The instance containing the fields
     */
    private static void checkFields(Field[] fields, Object instance) {
        for (Field f : fields) {
            var required = f.getAnnotation(Required.class) != null;
            var recursive = f.getAnnotation(Recursive.class) != null;
            if (required) {
                try {
                    if (f.get(instance) == null) {
                        throw new BadRequestException();
                    }
                    if (recursive) {
                        checkFields(f.get(instance).getClass().getFields(), f.get(instance));
                    }
                } catch (IllegalAccessException e) {
                    throw new BadRequestException();
                }

            }
        }
    }
}
