package com.ZfQnLmK.vJxRpT.domain.usecase

import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository

class GetSchemesUseCase(private val repository: SchemeRepository) {
    operator fun invoke(): List<Scheme> = repository.schemes()
}
