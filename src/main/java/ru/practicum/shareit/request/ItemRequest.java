package ru.practicum.shareit.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description; // текст запроса с описанием item

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    @ToString.Exclude
    private User requestor;

    @Column(name = "created_date")
    private LocalDateTime created; // когда создан запрос
}
