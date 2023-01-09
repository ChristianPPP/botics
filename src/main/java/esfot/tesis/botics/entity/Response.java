package esfot.tesis.botics.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import esfot.tesis.botics.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "responses")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "subject", length = 50, nullable = false)
    private String subject;

    @Column(name = "details", nullable = false)
    private String details;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonBackReference
    @OneToOne(mappedBy = "response")
    private Commentary commentary;

    @JsonBackReference
    @OneToOne(mappedBy = "response")
    private Ticket ticket;

    @JsonBackReference
    @OneToOne(mappedBy = "response")
    private Reserve reserve;

    //Timestamps
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;
}
