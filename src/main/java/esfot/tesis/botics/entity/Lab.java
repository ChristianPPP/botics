package esfot.tesis.botics.entity;


import esfot.tesis.botics.entity.enums.ELab;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @NotBlank
    @ColumnDefault("1")
    @Column(name = "state", nullable = false)
    private boolean state;

    @Column(name = "image")
    private String image;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "lab_softwares",
            joinColumns = @JoinColumn(name = "lab_id"),
            inverseJoinColumns = @JoinColumn(name = "software_id"))
    private Set<Software> softwares = new HashSet<>();

    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Computer> computers = new ArrayList<>();

    public Lab(ELab name, boolean state, String image) {
        this.name = name;
        this.state = state;
        this.image = image;
    }
}