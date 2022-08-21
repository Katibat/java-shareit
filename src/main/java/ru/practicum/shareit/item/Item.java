package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
//    @OneToMany
//    @JsonIgnore
//    private List<Comment> comments = new ArrayList<>();
//    @Transient
//    private Booking lastBooking;
//    @Transient
//    private Booking nextBooking;
//
//    public void setComments(List<Comment> comments) {
//        this.comments = comments;
//    }
//
//    public void setLastBooking(Booking booking) {
//        if (booking != null) {
//            this.lastBooking  = new Booking(
//                    booking.getId(),
//                    booking.getStart(),
//                    booking.getEnd(),
//                    booking.getItem(),
//                    booking.getBooker(),
//                    booking.getStatus()
//            );
//        }
//    }
//
//    public void setNextBooking(Booking booking) {
//        if (booking != null) {
//            this.nextBooking  = new Booking(
//                    booking.getId(),
//                    booking.getStart(),
//                    booking.getEnd(),
//                    booking.getItem(),
//                    booking.getBooker(),
//                    booking.getStatus()
//            );
//        }
//    }
}
