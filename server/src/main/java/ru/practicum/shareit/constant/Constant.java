package ru.practicum.shareit.constant;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@Getter
@UtilityClass
public class Constant {
    public static final String OWNER_ID = "X-Sharer-User-Id"; // имя заголовка с id пользователя, отправившего запрос
    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start"); // сортировка букинга
    public static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created"); // сортировка реквестов
}
