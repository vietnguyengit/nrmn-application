package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesBelongToMethodCheckTest extends FormattedTestProvider{
    @Test
    public void matchingMethodShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(1);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                        .obsItemAttribute(ImmutableMap.<String, String>builder()
                                .put("is_M1", "true").build())
                        .observableItemName("THE SPECIES").build()));

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void nonMatchingMethodShouldFail() {
        val formatted = getDefaultFormatted().build();
        formatted.setMethod(2);
        formatted.setSpecies(Optional.of(
                ObservableItem.builder()
                        .obsItemAttribute(ImmutableMap.<String, String>builder()
                                .put("is_M1", "true").build())
                        .observableItemName("THE SPECIES").build()));

        val validator = new SpeciesBelongToMethodCheck();
        val res =validator.valid(formatted);
        assertTrue(res.isInvalid());
    }
}