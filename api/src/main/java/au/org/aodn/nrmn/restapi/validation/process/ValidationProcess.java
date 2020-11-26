package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import com.oath.cyclops.hkt.DataWitness;
import cyclops.companion.Monoids;
import cyclops.control.Future;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ValidationProcess extends ValidatorHelpers {


    @Autowired
    StagedRowRepository rowRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    StagedRowErrorRepository errorRepo;

    @Autowired
    FormattedValidation postProcess;
    @Autowired
    GlobalValidation globalProcess;

    @Autowired
    RawValidation preProcess;


    public List<StagedRowError> process(StagedJob job) {
        val stagedRows = rowRepo.findRowsByReference(job.getReference());
        val rawValidators = preProcess.getRawValidators(job);
        val preCheck =
                stagedRows.stream()
                        .map(row -> preProcess.validate(row, rawValidators).bimap(err -> err, Seq::of))

                        .reduce(
                                Validated.valid(Seq.empty()),
                                (v1, v2) -> v1.combine(Monoids.seqConcat(), v2));
        if (preCheck.isInvalid()) {
            return toErrorList(preCheck);
        }

        val rowValidations = preCheck.orElseGet(Seq::empty);

        val rowValidationHMap =
                rowValidations.map(seq -> seq.toHashMap(Tuple2::_1, Tuple2::_2));

        val formattedRows = rowValidationHMap.map(preProcess::toFormat).toList();

        val futureFormattedResult = Future.of(() -> postProcess.process(formattedRows, job));
        val futureGlobalResult = Future.of(() -> globalProcess.process(job));

        val combineResult = futureFormattedResult
                .zip(futureGlobalResult, (v1, v2) -> v1.combine(Monoids.stringConcat, v2));
        return combineResult.fold(
                this::toErrorList,
                err -> Collections.singletonList(
                        new StagedRowError(
                                new ErrorID(
                                        null,
                                        job.getId(),
                                        err.getMessage()),
                                ValidationCategory.RUNTIME,
                                "Process",
                                null
                        )));
    }
}