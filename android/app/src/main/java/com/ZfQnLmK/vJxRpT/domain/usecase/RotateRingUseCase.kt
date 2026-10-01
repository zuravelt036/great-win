package com.ZfQnLmK.vJxRpT.domain.usecase

import com.ZfQnLmK.vJxRpT.domain.model.RingState

class RotateRingUseCase {
    operator fun invoke(state: RingState, ring: Int, delta: Int, segments: Int): RingState =
        state.rotated(ring, delta, segments)
}
