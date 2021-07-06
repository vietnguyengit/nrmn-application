package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import java.util.Optional;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import cyclops.control.Validated;
import lombok.val;

public class SpeciesBelongToMethodCheck extends BaseFormattedValidator {
    public SpeciesBelongToMethodCheck() {
        super("Species");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (!target.getSpecies().isPresent()) {
            return Validated.valid("Not applicable");
        }

        val species =  target.getSpecies().get();

        if (species.getMethods().stream().anyMatch(method -> method.getMethodId().equals(target.getMethod()))) {
            return Validated.valid("Species match method");
        }

        return invalid(target,
                "Species method didn't match method  (" + target.getMethod() + ").",
                ValidationCategory.DATA,
                ValidationLevel.WARNING, Optional.empty());
    }
}
