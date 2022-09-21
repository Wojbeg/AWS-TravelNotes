package com.wojbeg.aws_travelnotes.data.mappers

import com.amplifyframework.auth.AuthUserAttributeKey
import com.wojbeg.aws_travelnotes.domain.models.UserAttribute
import com.wojbeg.aws_travelnotes.domain.models.UserAttribute.*

fun userAttributeToAuthUserAttribute(userAttribute: UserAttribute): AuthUserAttributeKey {
    return when(userAttribute) {
        ADDRESS -> {
            AuthUserAttributeKey.address()
        }
        EMAIL_VERIFIED -> {
            throw Error("Illegal to update email verification")
        }
        BIRTHDATE -> {
            AuthUserAttributeKey.birthdate()
        }
        EMAIL -> {
            AuthUserAttributeKey.email()
        }
        FAMILY_NAME -> {
            AuthUserAttributeKey.familyName()
        }
        GENDER -> {
            AuthUserAttributeKey.gender()
        }
        GIVEN_NAME -> {
            AuthUserAttributeKey.givenName()
        }
        LOCALE -> {
            AuthUserAttributeKey.locale()
        }
        MIDDLE_NAME -> {
            AuthUserAttributeKey.middleName()
        }
        NICKNAME -> {
            AuthUserAttributeKey.nickname()
        }
        PHONE_NUMBER -> {
            AuthUserAttributeKey.phoneNumber()
        }
        PICTURE -> {
            AuthUserAttributeKey.picture()
        }
        PREFERRED_USERNAME -> {
            AuthUserAttributeKey.preferredUsername()
        }
        PROFILE -> {
            AuthUserAttributeKey.profile()
        }
        UPDATED_AT -> {
            AuthUserAttributeKey.updatedAt()
        }
        WEBSITE -> {
            AuthUserAttributeKey.website()
        }
        ZONE_INFO -> {
            AuthUserAttributeKey.zoneInfo()
        }
        NAME -> {
            AuthUserAttributeKey.name()
        }
    }
}