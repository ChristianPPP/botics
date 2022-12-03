package esfot.tesis.botics.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import esfot.tesis.botics.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "commentaries")
public class Commentary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "subject", length = 50, nullable = false)
    private String subject;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "state")
    private boolean state;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    private Response response;

    public Commentary(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}
