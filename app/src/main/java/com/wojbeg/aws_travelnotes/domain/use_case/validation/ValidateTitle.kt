package com.wojbeg.aws_travelnotes.domain.use_case.validation


class ValidateTitle {

    fun execute(title: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Title can not be empty"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}