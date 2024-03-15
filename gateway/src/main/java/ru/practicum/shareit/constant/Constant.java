package ru.practicum.shareit.constant;

import lombok.Getter;
import lombok.experimental.UtilityClass;
//import org.springframework.data.domain.Sort;

@Getter
@UtilityClass
public class Constant {
    public static final String USER_ID = "X-Sharer-User-Id";
//    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start"); // сортировка букинга
//    public static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created"); // сортировка реквестов
}
