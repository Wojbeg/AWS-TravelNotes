package com.wojbeg.aws_travelnotes.data.remote

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.NoteData
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.options.StorageRemoveOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.common.TAG
import com.wojbeg.aws_travelnotes.common.mutate
import com.wojbeg.aws_travelnotes.data.mappers.userAttributeToAuthUserAttribute
import com.wojbeg.aws_travelnotes.domain.models.*
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AmplifyServiceImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : AmplifyService {

    override fun init() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.configure(applicationContext)

            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }
    }

    override suspend fun fetchAuthSession(): Resource<AuthSession> {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession(
                {
                    Log.i(TAG, "Fetch auth session successful: $it")
                    continuation.resume(Resource.Success(it))
                },
                {
                    Log.e(TAG, "Failed to fetch auth session $it")
                    continuation.resume(Resource.Error(it.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun signUp(signUpData: SignUpData) : Resource<String?> {
        return suspendCoroutine { continuation ->

            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), signUpData.email)
                .build()

            Amplify.Auth.signUp(
                signUpData.username,
                signUpData.password, options,
                {
                    Log.i(TAG, "Sign Up successful: $it")
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Sign Up Error:", it)
                    continuation.resume(Resource.Error(it.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun verifyCode(verification: VerificationData): Resource<AuthException?> {
        return suspendCoroutine { continuation ->

            Amplify.Auth.confirmSignUp(
                verification.username,
                verification.verificationCode,
                {
                    Log.i(TAG, "Verification successful: $it")
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Verification Failed: ", it)
                    continuation.resume(Resource.Error(data = it, message = it.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun login(loginData: LoginData): Resource<AuthException?> {
        return suspendCoroutine { continuation ->

            Amplify.Auth.signIn(
                loginData.username,
                loginData.password,
                {
                    Log.i(TAG, "Login successful: $it")
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Login error: $it")
                    continuation.resume(Resource.Error(data = it, message = it.message ?: "Unexpected error"))

                }
            )
        }
    }

    override suspend fun resendVerificationCode(username: String): Resource<AuthException?> {
        return suspendCoroutine { continuation ->
            Amplify.Auth.resendSignUpCode(
                username,
                {
                    Log.i(TAG, "Resend verification code: $it")
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Resend verification code error: $it")
                    continuation.resume(Resource.Error(data = it, message = it.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun logOut(): Resource<String?> {
        val signOutResult = signOut()
        val dataStoreClearResult = dataStoreClear()

        if (dataStoreClearResult is Resource.Error) {
            return dataStoreClearResult
        }

        return signOutResult
    }

    private suspend fun signOut(): Resource<String?> {
        return suspendCoroutine { continuation ->
            Amplify.Auth.signOut(
                {
                    Log.i(TAG, "Log out successful")
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Sign Out Failed: ", it)
                    continuation.resume(Resource.Error(it.message ?: "Unexpected error"))
                }
            )
        }
    }

    private suspend fun dataStoreClear(): Resource<String?> {
        return suspendCoroutine { continuation ->
            Amplify.DataStore.clear(
                {
                    continuation.resume(Resource.Success(null))
                },
                {
                    Log.e(TAG, "Sign Out Failed: ", it)
                    continuation.resume(Resource.Error(it.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun queryNotes(): Resource<List<Note>> {
        Log.i(TAG, "Querying notes")

        val response = mutate(ModelQuery.list(NoteData::class.java))

        return if (response.hasErrors()) {

            Log.e(TAG, "${response.errors}")
            Resource.Error(response.errors.first().message)
        } else {

            Log.i(TAG, "${response.data}")
            val notes = response.data?.map { Note.from(it) } ?: listOf()
            Resource.Success(notes)
        }
    }

    private suspend fun noteOperation(mutation: GraphQLRequest<NoteData>, note: Note, filePath: String?, deleteImage: Boolean = false): Resource<String?> {
        val mutate = mutate(mutation)

        val imageUpload = if (note.imageName != null && filePath != null) {
            storeImage(filePath, note.imageName!!)
        } else {
            Resource.Success(null)
        }

        val imageDelete = if (!deleteImage) {
            Resource.Success(null)
        } else {
            deleteImage(note.id)
        }

        if (mutate.hasErrors()) {
            return Resource.Error(mutate.errors.first().message)
        }

        if (imageUpload is Resource.Error) {
            return Resource.Error(imageUpload.message ?: "Unexpected error")
        }

        if (imageDelete is Resource.Error) {
            return Resource.Error(imageDelete.message ?: "Unexpected error")
        }

        return Resource.Success(null)
    }

    override suspend fun createNote(note: Note, filePath: String?): Resource<String?> {
        Log.i(TAG, "Creating note: $note, path: $filePath")

        val create = ModelMutation.create(note.data)

        return noteOperation(create, note, filePath)
    }

    override suspend fun updateNote(note: Note, filePath: String?): Resource<String?> {
        Log.i(TAG, "Updating note $note, path: $filePath")

        val update = ModelMutation.update(note.data)

        return noteOperation(update, note, filePath)
    }

    override suspend fun deleteNote(note: Note): Resource<String?> {
        Log.i(TAG, "Deleting note $note")

        val delete = ModelMutation.delete(note.data)

        return noteOperation(delete, note, null, deleteImage = true)
    }

    override suspend fun storeProfilePicture(filePath: String): Resource<String?> {
        val imageKey = Amplify.Auth.currentUser.userId

        val storeImageResult = storeImage(filePath, imageKey, StorageAccessLevel.PRIVATE)
        val updateAttributesResult = updateUserAttributes(mapOf(UserAttribute.PICTURE to imageKey))

        if (storeImageResult is Resource.Error) {
            Log.e(TAG, "Error image result ${storeImageResult.message}")
            return storeImageResult
        }

        if (updateAttributesResult is Resource.Error) {
            Log.e(TAG, "Error update result ${storeImageResult.message}")
            return updateAttributesResult
        }

        return Resource.Success(imageKey)
    }

    override suspend fun storeImage(filePath: String, key: String, accessLevel: StorageAccessLevel): Resource<String?> {
        return suspendCancellableCoroutine { continuation ->

            val file = File(filePath)
            val options = StorageUploadFileOptions.builder()
                .accessLevel(accessLevel)
                .build()

            val call = Amplify.Storage.uploadFile(
                key,
                file,
                options,
                { progress ->
                    Log.i(TAG, "$key upload: fraction completed=${progress.fractionCompleted}")
                },
                { result ->
                    Log.i(TAG, "Successfully uploaded: " + result.key)
                    continuation.resume(Resource.Success(data = result.key))
                },
                { error ->
                    Log.e(TAG, "Upload failed", error)
                    continuation.resume(Resource.Error(error.message ?: "Unexpected error"))
                }
            )

            continuation.invokeOnCancellation { call.cancel() }
        }
    }

    override suspend fun deleteImage(key: String, accessLevel: StorageAccessLevel): Resource<String?>  {

        return suspendCoroutine { continuation ->
            val options = StorageRemoveOptions.builder()
                .accessLevel(accessLevel)
                .build()

            Amplify.Storage.remove(
                key,
                options,
                { result ->
                    Log.i(TAG, "Successfully removed: " + result.key)
                    continuation.resume(Resource.Success(data = result.key))
                },
                { error ->
                    Log.e(TAG, "Remove failure", error)
                    continuation.resume(Resource.Error(error.message ?: "Unexpected error"))
                }
            )
        }
    }

    override suspend fun retrieveImage(key: String, accessLevel: StorageAccessLevel) : Resource<ImageData> {

        return suspendCancellableCoroutine { continuation ->

            val options = StorageDownloadFileOptions.builder()
                .accessLevel(accessLevel)
                .build()

            val file = File.createTempFile("image", ".image")
            file.deleteOnExit()

            val  call = Amplify.Storage.downloadFile(
                key,
                file,
                options,
                { progress -> Log.i(TAG, "Fraction completed: ${progress.fractionCompleted}") },
                { result ->
                    Log.i(TAG, "Successfully downloaded: ${result.file}")

                    val imageStream = FileInputStream(file)
                    val image = BitmapFactory.decodeStream(imageStream)
                    continuation.resume(Resource.Success(ImageData(image, file.absolutePath)))
                },
                { error ->
                    Log.e(TAG, "Download Failure", error)
                    continuation.resume(Resource.Error(message = error.message ?: "Unexpected error"))
                }
            )

            continuation.invokeOnCancellation { call.cancel() }
        }
    }

    override suspend fun updateUserAttributes(attributesAndValues: Map<UserAttribute, String>): Resource<String?> {

        return suspendCoroutine { continuation ->
            val attributes = attributesAndValues.map {
                AuthUserAttribute(userAttributeToAuthUserAttribute(it.key), it.value)
            }

            Amplify.Auth.updateUserAttributes(
                attributes,
                {
                    Log.i(TAG, "User attributes $it")
                    continuation.resume(Resource.Success(null))
                },
                {   exception ->
                    Log.e(TAG, "Update user attributes error $exception")
                    continuation.resume(Resource.Error(message = exception.message ?: "Unexpected error"))
                }
            )
        }
    }

    override fun getUsername(): String {
        return Amplify.Auth.currentUser.username
    }

    override suspend fun getUserInfo(listOfAttributes: List<UserAttribute>): Resource<Map<UserAttribute, String>> {

        return suspendCoroutine { continuation ->
            val listOfAttributesValues = listOfAttributes.map { it.key }

            Amplify.Auth.fetchUserAttributes(
                { userAttributes ->
                    Log.i(TAG, "User attributes $userAttributes")

                    val mapped = userAttributes.filter {
                        listOfAttributesValues.contains(it.key.keyString)
                    }.associate { attribute ->
                        listOfAttributes.first { it.key == attribute.key.keyString } to attribute.value
                    }

                    Log.i(TAG, "Filtered User attributes $userAttributes")
                    continuation.resume(Resource.Success(data=mapped))
                },
                {   exception ->
                    Log.e(TAG, "User attributes exception $exception")
                    continuation.resume(Resource.Error(exception.message ?: "Unexpected error"))
                }
            )
        }
    }
}
