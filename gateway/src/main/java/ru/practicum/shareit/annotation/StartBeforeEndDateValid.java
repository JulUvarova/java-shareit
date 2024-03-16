package ru.practicum.shareit.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented // аннотация должна быть добавлена в javadoc поля/метода
@Target(ElementType.TYPE_USE) // что мы можем пометить этой аннотацией
@Retention(RetentionPolicy.RUNTIME) // жизненный цикл аннотации - когда она будет присутствовать
@Constraint(validatedBy = DateValidator.class) // список реализаций данного интерфейса
public @interface StartBeforeEndDateValid {
    String message() default "Начало бронирования не может быть позже его окончания";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
