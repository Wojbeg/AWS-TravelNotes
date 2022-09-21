package com.wojbeg.aws_travelnotes.domain.use_case.validation


class ValidateUsername {

    fun execute(username: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Username can not be empty"
            )
        }

        if (username.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "Username has to be longer than 4 characters"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}