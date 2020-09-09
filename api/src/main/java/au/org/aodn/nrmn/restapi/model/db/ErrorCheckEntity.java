package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "error_check")
public class ErrorCheckEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ErrorID id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private ValidationLevelType Type;

    @Column(name = "column_target")
    private String ColunmTarget;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private StagedSurveyEntity row;
}