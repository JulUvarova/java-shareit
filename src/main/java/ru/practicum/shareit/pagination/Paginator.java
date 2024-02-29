package ru.practicum.shareit.pagination;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Paginator {
    public static PageRequest withSort(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }

    public static PageRequest simplePage(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
