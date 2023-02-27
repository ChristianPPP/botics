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

    @Column(name = "lab_name")
    private String labName;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "code_cpu")
    private String codeCpu;

    @Column(name = "lab_reference")
    private long labReference;

    @Column(name = "computer_reference")
    private long computerReference;

    //Timestamps
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    public History(boolean state, long labReference, long computerReference, String labName, String hostName, String codeCpu) {
        this.state = state;
        this.labReference = labReference;
        this.computerReference = computerReference;
        this.labName = labName;
        this.hostName = hostName;
        this.codeCpu = codeCpu;
    }
}
