package com.wojbeg.aws_travelnotes.domain.use_case.validation

import android.util.Patterns
import com.wojbeg.aws_travelnotes.R
import java.util.regex.Pattern

class ValidateEmail {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Email can not be empty"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}
