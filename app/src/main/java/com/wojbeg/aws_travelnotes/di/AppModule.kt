package com.wojbeg.aws_travelnotes.di

import android.content.Context
import com.wojbeg.aws_travelnotes.common.ImageOperationsImpl
import com.wojbeg.aws_travelnotes.data.remote.AmplifyServiceImpl
import com.wojbeg.aws_travelnotes.data.repository.RepositoryImpl
import com.wojbeg.aws_travelnotes.domain.local.UserData
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.*
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.*
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.UpdateNote_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.*
import com.wojbeg.aws_travelnotes.domain.use_case.validation.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAmplifyService(
        @ApplicationContext context: Context
    ): AmplifyService {
        return AmplifyServiceImpl(context)
    }

    @Singleton
    @Provides
    fun provideImageOperations(
        @ApplicationContext context: Context
    ): ImageOperationsImpl {
        return ImageOperationsImpl(context)
    }

    @Singleton
    @Provides
    fun provideUserData(): UserData {
        return com.wojbeg.aws_travelnotes.data.local.UserData
    }

    @Singleton
    @Provides
    fun provideRepository(
        amplifyService: AmplifyService,
        userData: UserData
    ): Repository {
        return RepositoryImpl(amplifyService, userData)
    }

    @Singleton
    @Provides
    fun provideValidateTitle(): ValidateTitle {
        return ValidateTitle()
    }

    @Singleton
    @Provides
    fun provideValidateEmail(): ValidateEmail {
        return ValidateEmail()
    }

    @Singleton
    @Provides
    fun provideValidatePassword(): ValidatePassword {
        return ValidatePassword()
    }

    @Singleton
    @Provides
    fun provideValidateRepeatPassword(): ValidateRepeatPassword {
        return ValidateRepeatPassword()
    }

    @Singleton
    @Provides
    fun provideValidateUsername(): ValidateUsername {
        return ValidateUsername()
    }

    //USE CASES

    @Singleton
    @Provides
    fun ProvideSignUpUseCase(
        repository: Repository
    ): SignUp_UseCase {
        return SignUp_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideSignInUseCase(
        repository: Repository
    ): SignIn_UseCase {
        return SignIn_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideLogOutUseCase(
        repository: Repository
    ): LogOut_UseCase {
        return LogOut_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideVerifyCodeUseCase(
        repository: Repository
    ): VerifyCode_UseCase {
        return VerifyCode_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideCreateNoteUseCase(
        repository: Repository
    ): CreateNote_UseCase {
        return CreateNote_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideDeleteNoteUseCase(
        repository: Repository
    ): DeleteNote_UseCase {
        return DeleteNote_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideGetNotesUseCase(
        repository: Repository
    ): GetNotes_UseCase {
        return GetNotes_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideUpdateNodeUseCase(
        repository: Repository
    ): UpdateNote_UseCase {
        return UpdateNote_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideRetriveImageUseCase(
        repository: Repository
    ): RetrieveImage_UseCase {
        return RetrieveImage_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideStoreImageUseCase(
        repository: Repository
    ): StoreImage_UseCase {
        return StoreImage_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideDeleteImageUseCase(
        repository: Repository
    ): DeleteImage_UseCase {
        return DeleteImage_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideFetchauthSessionUseCase(
        repository: Repository
    ): FetchAuthSession_UseCase {
        return FetchAuthSession_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideGetUserInfoUseCase(
        repository: Repository
    ): GetUserInfo_UseCase {
        return GetUserInfo_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideGetUsernameUseCase(
        repository: Repository
    ): GetUsername_UseCase {
        return GetUsername_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideResendVerificationUseCase(
        repository: Repository
    ): ResendVerificationCode_UseCase {
        return ResendVerificationCode_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideStoreProfilePictureUseCase(
        repository: Repository
    ): StoreProfilePicture_UseCase {
        return StoreProfilePicture_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideAddImageToNoteUseCase(
        repository: Repository
    ): AddImageToNoteUserData_UseCase {
        return AddImageToNoteUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideAddNoteUseCase(
        repository: Repository
    ): AddNoteUserData_UseCase {
        return AddNoteUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideAddNotesUseCase(
        repository: Repository
    ): AddNotesUserData_UseCase {
        return AddNotesUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideAddClearUserDataUseCase(
        repository: Repository
    ): ClearUserData_UseCase {
        return ClearUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideDeleteNoteLocalUseCase(
        repository: Repository
    ): DelteNoteUserData_UseCase {
        return DelteNoteUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideDeleteNoteAtUseCase(
        repository: Repository
    ): DelteNoteAtUserData_UseCase {
        return DelteNoteAtUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideResetNotesUseCase(
        repository: Repository
    ): ResetNotesUserData_UseCase {
        return ResetNotesUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideSetNewUsernameUseCase(
        repository: Repository
    ): SetUsernameUserData_UseCase {
        return SetUsernameUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideSetNoewListUseCase(
        repository: Repository
    ): SetNoteListUserData_UseCase {
        return SetNoteListUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideUpdateNoteUserDataUseCase(
        repository: Repository
    ): UpdateNoteUserData_UseCase {
        return UpdateNoteUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideSetNewLocalImageUseCase(
        repository: Repository
    ): SetNewProfileImageUserData_UseCase {
        return SetNewProfileImageUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideSetEmailImageUseCase(
        repository: Repository
    ): SetEmailUserData_UseCase {
        return SetEmailUserData_UseCase(repository)
    }

    @Singleton
    @Provides
    fun ProvideGetUserDataImageUseCase(
        repository: Repository
    ): GetUserData_UseCase {
        return GetUserData_UseCase(repository)
    }

}
