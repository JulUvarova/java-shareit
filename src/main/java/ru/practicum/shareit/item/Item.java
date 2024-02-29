package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JoinFormula;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private User owner;

    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT b.id FROM bookings b " +
            " WHERE b.item_id = id " +
            " AND b.start_date > LOCALTIMESTAMP(2) " +
            " AND b.status = 'APPROVED' " +
            " ORDER BY b.start_date ASC LIMIT 1)")
    private Booking nextBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(SELECT b.id FROM bookings b " +
            " WHERE b.item_id = id " +
            " AND b.start_date <= LOCALTIMESTAMP(2) " +
            " AND b.status = 'APPROVED' " +
            " ORDER BY b.end_date DESC LIMIT 1)")
    private Booking lastBooking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(owner, item.owner) && Objects.equals(requestId, item.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, requestId);
    }
}
