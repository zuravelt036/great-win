package com.ZfQnLmK.vJxRpT.domain.model

data class RingState(val offsets: List<Int>) {

    fun rotated(ring: Int, delta: Int, segments: Int): RingState {
        if (ring < 0 || ring >= offsets.size || segments <= 0) return this
        val next = offsets.toMutableList()
        next[ring] = ((next[ring] + delta) % segments + segments) % segments
        return RingState(next)
    }

    companion object {
        fun initial(ringCount: Int): RingState = RingState(List(ringCount) { 0 })
    }
}
