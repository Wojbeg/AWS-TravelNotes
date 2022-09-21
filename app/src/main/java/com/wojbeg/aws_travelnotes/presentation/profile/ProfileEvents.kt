package com.wojbeg.aws_travelnotes.presentation.profile

import android.graphics.Bitmap

sealed class ProfileEvents {
    data class UpdateProfile(val image: Bitmap): ProfileEvents()
    object LogOut: ProfileEvents()
}