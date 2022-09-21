package com.wojbeg.aws_travelnotes.data.repository

import android.graphics.Bitmap
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.storage.StorageAccessLevel
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.local.UserData
import com.wojbeg.aws_travelnotes.domain.models.*
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import com.wojbeg.aws_travelnotes.presentation.components.logic.UserDataState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    val amplifyService: AmplifyService,
    val userData: UserData
) : Repository {

    override fun init() {
        amplifyService.init()
    }

    override suspend fun fetchAuthSession(): Resource<AuthSession> {
        val result = amplifyService.fetchAuthSession()
        when (result) {
            is Resource.Success -> {
                if (result.data?.isSignedIn == true) {
                   setNewUsername(amplifyService.getUsername())
                }
            }
            else -> {}
        }

        return result
    }

    override suspend fun signUp(signUpData: SignUpData): Resource<String?> {
        return amplifyService.signUp(signUpData)
    }

    override suspend fun verifyCode(verification: VerificationData): Resource<AuthException?> {
        return amplifyService.verifyCode(verification)
    }

    override suspend fun login(loginData: LoginData): Resource<AuthException?> {
        return amplifyService.login(loginData)
    }

    override suspend fun logOut(): Resource<String?> {
        return amplifyService.logOut()
    }

    override suspend fun getUserInfo(listOfAttributes: List<UserAttribute>): Resource<Map<UserAttribute, String>> {
        return amplifyService.getUserInfo(listOfAttributes)
    }

    override suspend fun updateUserAttributes(attributesAndValues: Map<UserAttribute, String>): Resource<String?> {
        return amplifyService.updateUserAttributes(attributesAndValues)
    }

    override fun getUsername(): String {
        return amplifyService.getUsername()
    }

    override suspend fun queryNotes(): Resource<List<Note>> {
        return amplifyService.queryNotes()
    }

    override suspend fun createNote(note: Note, filePath: String?): Resource<String?> {
        return amplifyService.createNote(note, filePath)
    }

    override suspend fun updateNote(note: Note, filePath: String?): Resource<String?> {
        return amplifyService.updateNote(note, filePath)
    }

    override suspend fun deleteNote(note: Note): Resource<String?> {
        return amplifyService.deleteNote(note)
    }

    override suspend fun storeImage(
        filePath: String,
        key: String,
        accessLevel: StorageAccessLevel
    ): Resource<String?> {
        return amplifyService.storeImage(filePath, key, accessLevel)
    }

    override suspend fun deleteImage(
        key: String,
        accessLevel: StorageAccessLevel
    ): Resource<String?> {
        return amplifyService.deleteImage(key, accessLevel)
    }

    override suspend fun retrieveImage(
        key: String,
        accessLevel: StorageAccessLevel
    ): Resource<ImageData> {
        return amplifyService.retrieveImage(key, accessLevel)
    }

    override suspend fun resendVerificationCode(username: String): Resource<AuthException?> {
        return amplifyService.resendVerificationCode(username)
    }

    override suspend fun storeProfilePicture(filePath: String): Resource<String?> {
        return amplifyService.storeProfilePicture(filePath)
    }


    //LOCAL


    override fun clearUserData() {
        return userData.clearUserData()
    }

    override fun getUserData(): StateFlow<UserDataState> = userData.getUserData()


    override fun addNotes(noteList: List<Note>) {
        return userData.addNotes(noteList)
    }

    override fun addNote(note: Note) {
        return userData.addNote(note)
    }

    override fun updateNote(note: Note) {
        return userData.updateNote(note)
    }

    override fun deleteNote(id: String): Boolean {
        return userData.deleteNote(id)
    }

    override fun deleteNoteAt(at: Int): Note? {
        return userData.deleteNoteAt(at)
    }

    override fun resetNotes() {
        return userData.resetNotes()
    }

    override fun addImageToNote(Id: String, image: Bitmap) {
        return userData.addImageToNote(Id, image)
    }

    override fun setNoteList(noteList: List<Note>) {
        return userData.setNoteList(noteList)
    }

    override fun setNewUsername(newUsername: String) {
        userData.setNewUsername(newUsername)
    }

    override fun setNewEmail(newEmail: String) {
        userData.setNewEmail(newEmail)
    }

    override fun setNewImage(profileBitmap: Bitmap?, profilePictureFile: String?) {
        userData.setNewImage(profileBitmap, profilePictureFile)
    }

}
