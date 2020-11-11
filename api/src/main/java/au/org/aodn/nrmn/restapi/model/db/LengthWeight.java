package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited(withModifiedFlag = true)
@Table(name = "lengthweight_ref")
public class LengthWeight {
    @Id
    @Column(name = "observable_item_id", unique = true, updatable = false, nullable = false)
    private int observableItemId;

    @Basic
    @Column(name = "a")
    private Double a;

    @Basic
    @Column(name = "b")
    private Double b;

    @Basic
    @Column(name = "cf")
    private Double cf;

    @Basic
    @Column(name = "sgfgu")
    private String sgfgu;

    @OneToOne
    @MapsId
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "observable_item_id", foreignKey = @ForeignKey(name =
        "lengthweight_ref_observable_item_id_fkey"))
    @JsonBackReference
    private ObservableItem observableItem;
}