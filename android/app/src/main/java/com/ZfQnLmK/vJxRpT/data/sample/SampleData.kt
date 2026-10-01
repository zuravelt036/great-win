package com.ZfQnLmK.vJxRpT.data.sample

import com.ZfQnLmK.vJxRpT.domain.model.RingSpec
import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.model.TutorialStep

object SampleData {

    val schemes: List<Scheme> = listOf(
        Scheme(
            id = "azure_loop",
            name = "AZURE LOOP",
            segments = 8,
            moveLimit = 14,
            difficulty = 1,
            previewIndex = 0,
            rings = listOf(
                RingSpec(listOf(0, 2, 4, 6), 1),
                RingSpec(listOf(0, 1, 2, 4, 6), 2),
                RingSpec(listOf(0, 2, 3, 4, 6), 0)
            )
        ),
        Scheme(
            id = "violet_core",
            name = "VIOLET CORE",
            segments = 8,
            moveLimit = 14,
            difficulty = 1,
            previewIndex = 1,
            rings = listOf(
                RingSpec(listOf(0, 2, 4, 5, 6), 2),
                RingSpec(listOf(0, 2, 4, 6, 7), 3),
                RingSpec(listOf(0, 1, 2, 4, 6), 1)
            )
        ),
        Scheme(
            id = "amber_relay",
            name = "AMBER RELAY",
            segments = 8,
            moveLimit = 12,
            difficulty = 2,
            previewIndex = 2,
            rings = listOf(
                RingSpec(listOf(0, 2, 3, 4, 6), 3),
                RingSpec(listOf(0, 2, 4, 6), 5),
                RingSpec(listOf(0, 2, 4, 6, 7), 2)
            )
        ),
        Scheme(
            id = "pulse_net",
            name = "PULSE NET",
            segments = 12,
            moveLimit = 12,
            difficulty = 2,
            previewIndex = 0,
            rings = listOf(
                RingSpec(listOf(0, 3, 6, 9, 10), 4),
                RingSpec(listOf(0, 1, 3, 6, 9), 7),
                RingSpec(listOf(0, 3, 4, 6, 9), 2)
            )
        ),
        Scheme(
            id = "nova_grid",
            name = "NOVA GRID",
            segments = 12,
            moveLimit = 10,
            difficulty = 3,
            previewIndex = 1,
            rings = listOf(
                RingSpec(listOf(0, 3, 6, 9), 5),
                RingSpec(listOf(0, 2, 3, 6, 9), 9),
                RingSpec(listOf(0, 3, 6, 8, 9), 7)
            )
        ),
        Scheme(
            id = "prism_vault",
            name = "PRISM VAULT",
            segments = 12,
            moveLimit = 10,
            difficulty = 3,
            previewIndex = 2,
            rings = listOf(
                RingSpec(listOf(0, 3, 5, 6, 9), 8),
                RingSpec(listOf(0, 3, 6, 9, 11), 3),
                RingSpec(listOf(0, 3, 6, 7, 9), 11)
            )
        )
    )

    val tutorialScheme: Scheme = Scheme(
        id = "primer_coil",
        name = "PRIMER COIL",
        segments = 8,
        moveLimit = 20,
        difficulty = 1,
        previewIndex = 0,
        rings = listOf(
            RingSpec(listOf(0, 2, 4, 6), 0),
            RingSpec(listOf(0, 2, 4, 6), 0),
            RingSpec(listOf(0, 2, 4, 6), 1)
        )
    )

    val tutorialSteps: List<TutorialStep> = listOf(
        TutorialStep(index = 0, usesNodeSprite = false),
        TutorialStep(index = 1, usesNodeSprite = false),
        TutorialStep(index = 2, usesNodeSprite = true)
    )
}
