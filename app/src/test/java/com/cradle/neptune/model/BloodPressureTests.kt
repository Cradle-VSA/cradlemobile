package com.cradle.neptune.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BloodPressureTests {

    @Test
    fun unmarshal_isInverseOf_marshal() {
        val bp = BloodPressure(110, 90, 75)
        val json = bp.marshal()
        val actual = unmarshal(BloodPressure, json)
        assertEquals(bp, actual)
    }

    @Test
    fun bloodPressure_ifInSevereShock_thenRedDown() {
        val bp = BloodPressure(80, 60, 160)
        // i.e, shock index = 2.0
        assertEquals(ReadingAnalysis.RED_DOWN, bp.analysis)
    }

    @Test
    fun bloodPressure_ifVeryHighSystolic_thenRedUp() {
        val bp = BloodPressure(RED_SYSTOLIC, 60, 60)
        assertEquals(ReadingAnalysis.RED_UP, bp.analysis)
    }

    @Test
    fun bloodPressure_ifVeryHighDiastolic_thenRedUp() {
        val bp = BloodPressure(100, RED_DIASTOLIC, 60)
        assertEquals(ReadingAnalysis.RED_UP, bp.analysis)
    }

    @Test
    fun bloodPressure_ifInShock_thenYellowDown() {
        val bp = BloodPressure(80, 60, 80)
        // i.e., shock index = 1.0
        assertEquals(ReadingAnalysis.YELLOW_DOWN, bp.analysis)
    }

    @Test
    fun bloodPressure_ifHighSystolic_thenYellowUp() {
        val bp = BloodPressure(YELLOW_SYSTOLIC, 60, 60)
        assertEquals(ReadingAnalysis.YELLOW_UP, bp.analysis)
    }

    @Test
    fun bloodPressure_ifHighDiastolic_thenYellowUp() {
        val bp = BloodPressure(100, YELLOW_DIASTOLIC, 60)
        assertEquals(ReadingAnalysis.YELLOW_UP, bp.analysis)
    }

    @Test
    fun bloodPressure_ifNormal_thenGreen() {
        val bp = BloodPressure(100, 60, 60)
        assertEquals(ReadingAnalysis.GREEN, bp.analysis)
    }
}
