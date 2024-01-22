package ru.practicum.shareit.constant;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@UtilityClass
public class Constant {
    public static final String OWNER_ID = "X-Sharer-User-Id"; // имя заголовка с id пользователя, отправившего запрос
}
