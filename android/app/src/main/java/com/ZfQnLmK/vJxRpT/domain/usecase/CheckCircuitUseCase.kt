package com.ZfQnLmK.vJxRpT.domain.usecase

import com.ZfQnLmK.vJxRpT.domain.model.CircuitResult
import com.ZfQnLmK.vJxRpT.domain.model.RingState
import com.ZfQnLmK.vJxRpT.domain.model.Scheme

class CheckCircuitUseCase {

    operator fun invoke(scheme: Scheme, state: RingState): CircuitResult {
        val segments = scheme.segments
        if (segments <= 0 || scheme.rings.isEmpty()) {
            return CircuitResult(emptyList(), emptySet(), false)
        }
        val conductive = conductiveSets(scheme, state)
        val energized = ArrayList<Set<Int>>(conductive.size)
        var carried: Set<Int> = conductive[0]
        for (ring in conductive.indices) {
            carried = if (ring == 0) conductive[0] else conductive[ring].intersect(carried)
            energized.add(carried)
        }
        val outer = energized.lastOrNull() ?: emptySet()
        val reached = scheme.targetIndices.filter { outer.contains(it) }.toSet()
        return CircuitResult(energized, reached, reached.size == scheme.targetIndices.size)
    }

    fun conductiveSets(scheme: Scheme, state: RingState): List<Set<Int>> {
        val segments = scheme.segments
        return scheme.rings.mapIndexed { index, spec ->
            val offset = state.offsets.getOrElse(index) { 0 }
            spec.conductors.map { normalize(it - spec.solutionStep + offset, segments) }.toSet()
        }
    }

    fun baseConductors(scheme: Scheme): List<Set<Int>> {
        val segments = scheme.segments
        return scheme.rings.map { spec ->
            spec.conductors.map { normalize(it - spec.solutionStep, segments) }.toSet()
        }
    }

    private fun normalize(value: Int, segments: Int): Int = ((value % segments) + segments) % segments
}
