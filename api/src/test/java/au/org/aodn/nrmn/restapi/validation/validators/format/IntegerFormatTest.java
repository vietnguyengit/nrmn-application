package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import cyclops.companion.Monoids;
import cyclops.data.Seq;
import cyclops.data.Vector;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import org.junit.jupiter.params.ParameterizedTest;

import static au.org.aodn.nrmn.restapi.util.ValidatorHelpers.toErrorList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
                stage.setLMax("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getLMax, "Lmax", Collections.emptyList()).valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setLMax("10");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getLMax, "Lmax", Collections.emptyList()).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void withinCategoryShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Lmax", Stream.of(1,2,3,4,7,8).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void singleValidValueErrorMessage() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Lmax", Stream.of(8).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isInvalid());
        assertEquals("7 is invalid. Must be 8", toErrorList(res).get(0).getId().getMessage());
    }

    @Test
    void multipleValidValueErrorMessage() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Lmax",
         Stream.of(8, 9, 10).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isInvalid());
        assertEquals("7 is invalid. Must be 8, 9 or 10", toErrorList(res).get(0).getId().getMessage());
    }
}
