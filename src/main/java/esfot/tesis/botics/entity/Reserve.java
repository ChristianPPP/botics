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
@Table(name = "reserves")
public class Reserve {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "lab_name", length = 50, nullable = false)
    private String labName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "state")
    private boolean state;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    private Response response;

    public Reserve(String labName, String description) {
        this.labName = labName;
        this.description = description;
    }
}
