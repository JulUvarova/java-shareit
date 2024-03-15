package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemStorageTest {
    @Autowired
    ItemStorage itemStorage;
    @Autowired
    UserStorage userStorage;

    @BeforeEach
    void init() {
        User user = User.builder()
                .id(1L)
                .name("UserName")
                .email("email@mail.ru")
                .build();
        userStorage.save(user);

        itemStorage.save(Item.builder()
                .id(1L)
                .name("Brain")
                .description("Amazing brain")
                .owner(user)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .build()
        );
    }

    @AfterEach
    void clear() {
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void search() {
        List<Item> actualItems = itemStorage.search("brain", Paginator.simplePage(0, 5));

        assertEquals(1, actualItems.size());
        assertEquals("Amazing brain", actualItems.get(0).getDescription());
    }
}