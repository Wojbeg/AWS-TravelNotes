package com.wojbeg.aws_travelnotes.domain.remote

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.storage.StorageAccessLevel
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.*

interface AmplifyService {

    fun init()

    suspend fun fetchAuthSession(): Resource<AuthSession>
    suspend fun signUp(signUpData: SignUpData): Resource<String?>
    suspend fun verifyCode(verification: VerificationData): Resource<AuthException?>
    suspend fun login(loginData: LoginData): Resource<AuthException?>
    suspend fun logOut(): Resource<String?>
    suspend fun resendVerificationCode(username: String): Resource<AuthException?>

    suspend fun getUserInfo(listOfAttributes: List<UserAttribute>): Resource<Map<UserAttribute, String>>
    suspend fun updateUserAttributes(attributesAndValues: Map<UserAttribute, String>): Resource<String?>
    fun getUsername(): String
    suspend fun storeProfilePicture(filePath: String): Resource<String?>

    suspend fun queryNotes(): Resource<List<Note>>
    suspend fun createNote(note: Note, filePath: String?): Resource<String?>
    suspend fun updateNote(note: Note, filePath: String?): Resource<String?>
    suspend fun deleteNote(note: Note): Resource<String?>

    suspend fun storeImage(filePath: String, key: String, accessLevel: StorageAccessLevel = StorageAccessLevel.PRIVATE): Resource<String?>
    suspend fun deleteImage(key: String, accessLevel: StorageAccessLevel = StorageAccessLevel.PRIVATE): Resource<String?>
    suspend fun retrieveImage(key: String, accessLevel: StorageAccessLevel = StorageAccessLevel.PRIVATE): Resource<ImageData>

}