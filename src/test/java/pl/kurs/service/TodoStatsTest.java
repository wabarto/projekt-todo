package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class TodoStatsTest {
    TodoStats stats;

    @BeforeEach
    void setUp() {
        stats = new TodoStats();
    }

    @Test
    void shouldComputeRatioForPartiallyCompletedList() {
        List<Boolean> flags = List.of(true, false, false, false);

        assertThat(stats.completionRatio(flags)).isCloseTo(0.25, within(0.01));
    }

    @Test
    void shouldReturnZeroWhenNothingCompleted() {
        List<Boolean> flags = List.of(false, false, false);

        assertThat(stats.completionRatio(flags)).isCloseTo(0.0, within(0.01));
    }

    @Test
    void shouldRejectEmptyList() {
        assertThatThrownBy(() -> stats.completionRatio(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
