package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple2;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory.FORMAT;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.BLOCKING;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.WARNING;


public class ATRCDepthValidation extends BaseRowValidator {

    private static final Pattern VALID_DEPTH_SURVEY_NUM = Pattern.compile("^[0-9]+(\\.[1-4])?$");

    public ATRCDepthValidation() {
        super("Depth");
    }

    @Override
    public Validated<StagedRowError, Tuple2<Integer, Optional<Integer>>> valid(StagedRow target) {
        String value = target.getDepth();
        if (!VALID_DEPTH_SURVEY_NUM.matcher(value).matches()) {
            return getError(target, "Depth is invalid, expected: {depth}[.{surveyNum}]", FORMAT, BLOCKING);
        }
        String[] split = value.split("\\.");
        Integer depth = Integer.parseInt(split[0]);
        Optional<Integer> surveyNum = split.length > 1 ? Optional.of(Integer.parseInt(split[1])) : Optional.empty();
        if (surveyNumIsRequired(target.getMethod()) && !surveyNum.isPresent()) {
            return getError(target, "Survey number not specified", FORMAT, WARNING);
        }
        return Validated.valid(new Tuple2<>(depth, surveyNum));
    }

    private boolean surveyNumIsRequired(String method) {
        return Arrays.asList("1", "2", "3", "4", "5", "7").contains(method);
    }

}
