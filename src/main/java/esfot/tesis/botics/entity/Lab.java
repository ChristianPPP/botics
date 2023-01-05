package esfot.tesis.botics.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import esfot.tesis.botics.entity.enums.ELab;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "labs",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Lab {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, nullable = false)
    private ELab name;

    @ColumnDefault("1")
    @Column(name = "state", nullable = false)
    private boolean state;

    @Column(name = "image")
    private String image;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "lab_computers",
            joinColumns = @JoinColumn(name = "lab_id"),
            inverseJoinColumns = @JoinColumn(name = "computer_id"))
    private Set<Computer> computers = new HashSet<>();

}
