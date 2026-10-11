package com.devvault.drill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrillCatalogTest {

    static final List<Drill> DRILLS = DrillCatalog.build();

    @Test
    void buildsTenThousandWellFormedDrills() {
        assertThat(DRILLS).hasSize(10_000);
        Map<String, Integer> families = new HashMap<>();
        for (Drill d : DRILLS) {
            assertThat(d.answer()).as("answer of %d family %s: %s", d.id(), d.familyKey(), d.question()).isNotBlank();
            assertThat(d.question()).isNotBlank();
            assertThat(d.explanation()).isNotBlank();
            if (d.kind() == Spec.Kind.CHOICE) {
                assertThat(d.choices()).contains(d.answer());
            }
            families.merge(d.familyKey(), 1, Integer::sum);
        }
        System.out.println("DRILL FAMILIES " + families);
        assertThat(families.size()).isGreaterThanOrEqualTo(50);
    }

    @Test
    void isDeterministic() {
        List<Drill> again = DrillCatalog.build();
        assertThat(again.get(1234)).isEqualTo(DRILLS.get(1234));
        assertThat(again.get(9999)).isEqualTo(DRILLS.get(9999));
    }
}
