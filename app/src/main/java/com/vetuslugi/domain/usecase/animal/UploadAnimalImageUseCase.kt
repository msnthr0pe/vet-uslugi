package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.repository.AnimalRepository

class UploadAnimalImageUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(imageBytes: ByteArray, fileName: String): Result<String> =
        repository.uploadImage(imageBytes, fileName)
}
