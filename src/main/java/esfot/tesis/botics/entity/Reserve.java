package esfot.tesis.botics.entity;


import esfot.tesis.botics.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reserves")
public class Reserve {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "subject", length = 50, nullable = false)
    private String subject;

    @NotBlank
    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Reserve(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}
