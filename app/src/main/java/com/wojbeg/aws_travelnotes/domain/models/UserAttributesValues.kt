package com.wojbeg.aws_travelnotes.domain.models

import com.amplifyframework.auth.AuthUserAttributeKey

enum class UserAttribute(val key: String) {
    ADDRESS(AuthUserAttributeKey.address().keyString),
    EMAIL_VERIFIED("email_verified"),
    BIRTHDATE(AuthUserAttributeKey.birthdate().keyString),
    EMAIL(AuthUserAttributeKey.email().keyString),
    FAMILY_NAME(AuthUserAttributeKey.familyName().keyString),
    GENDER(AuthUserAttributeKey.gender().keyString),
    GIVEN_NAME(AuthUserAttributeKey.givenName().keyString),
    LOCALE(AuthUserAttributeKey.locale().keyString),
    MIDDLE_NAME(AuthUserAttributeKey.middleName().keyString),
    NICKNAME(AuthUserAttributeKey.nickname().keyString),
    PHONE_NUMBER(AuthUserAttributeKey.phoneNumber().keyString),
    PICTURE(AuthUserAttributeKey.picture().keyString),
    PREFERRED_USERNAME(AuthUserAttributeKey.preferredUsername().keyString),
    PROFILE(AuthUserAttributeKey.profile().keyString),
    UPDATED_AT(AuthUserAttributeKey.updatedAt().keyString),
    WEBSITE(AuthUserAttributeKey.website().keyString),
    ZONE_INFO(AuthUserAttributeKey.zoneInfo().keyString),
    NAME(AuthUserAttributeKey.name().keyString);

    companion object {

    }
}
