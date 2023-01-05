package esfot.tesis.botics.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

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

    //Timestamps
    @Column(name = "created_at")
    @CreationTimestamp
    private Date created_at;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updated_at;

    public History(boolean state, long labReference, long computerReference) {
        this.state = state;
        this.labReference = labReference;
        this.computerReference = computerReference;
    }
}
