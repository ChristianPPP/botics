package esfot.tesis.botics.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ColumnDefault("1")
    @Column(name = "state")
    private boolean state;

    @Column(name = "change_details")
    private String changeDetails;

    @Column(name = "lab_reference")
    private long labReference;

    @Column(name = "computer_reference")
    private long computerReference;

    public History(boolean state, long labReference, long computerReference) {
        this.state = state;
        this.labReference = labReference;
        this.computerReference = computerReference;
    }
}
