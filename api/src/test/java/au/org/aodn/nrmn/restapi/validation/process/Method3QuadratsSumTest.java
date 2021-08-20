package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import lombok.val;

class Method3QuadratsSumTest extends FormattedTestProvider {
    @Test
    void quadratsSumUnder50ShouldFail() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(2, 2);
            }
        });

        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 8);
                put(50, 5);
            }
        });

        val r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 4);
        
            }
        });

        val r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(3, 7);
            }
        });
        val date = LocalDate.now();

        val a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 4);
                put(3, 7);
            }
        });

        val a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2,3);
            }
        });

        val a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(4,10);
            }
        });

        val a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5,6);
            }
        });

        ValidationError error = validationProcess.validateMethod3QuadratsGT50("", Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4));
        assertTrue(error != null && error.getMessage().startsWith("Quadrats do not sum to at least 50 in transect"));
    }

    @Test
    void quadratsSumUnder50ShouldSuccess() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 40);
                put(2, 20);
            }
        });

        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(3, 80);
                put(4, 45);
                put(5, 30);
            }
        });

        val r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2, 10);
                put(4, 6);
            }
        });

        val r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 140);
                put(2, 20);
                put(3, 70);
            }
        });
        val date = LocalDate.now();

        val a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 42);
                put(3, 70);
            }
        });

        val a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(1, 10);
                put(2, 35);
            }
        });

        val a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(2,16);
                put(4,100);
                put(5,52);
            }
        });

        val a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(new HashMap<Integer, Integer>() {
            {
                put(5,6);
            }
        });

        ValidationError error = validationProcess.validateMethod3QuadratsGT50("", Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4));
        assertFalse(error != null && error.getMessage().startsWith("Quadrats do not sum to at least 50 in transect"));
    }
}
