package com.wojbeg.aws_travelnotes.domain.repository

import com.wojbeg.aws_travelnotes.domain.local.UserData
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService

interface Repository: AmplifyService, UserData {
}
