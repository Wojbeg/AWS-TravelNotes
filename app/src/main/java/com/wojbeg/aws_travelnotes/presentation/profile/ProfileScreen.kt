package com.wojbeg.aws_travelnotes.presentation.profile

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.presentation.components.ImagePickerModalBottomSheetContent
import com.wojbeg.aws_travelnotes.presentation.components.RoundImage
import com.wojbeg.aws_travelnotes.presentation.ui.theme.darkerBlue
import com.wojbeg.aws_travelnotes.presentation.util.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val rememberCoroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val state = viewModel.profileState
    val userData = viewModel.userData.collectAsState()

    val roundImageModifier = Modifier
        .fillMaxWidth(0.33f)

    LaunchedEffect(key1 = true) {
        viewModel.validationEvents.collect { event ->
            when(event) {
                ProfileViewModel.ProfileUIEvents.Logout -> {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalBottomSheetLayout(
            sheetContent = {
                ImagePickerModalBottomSheetContent(
                    context = context,
                    rememberCoroutineScope = rememberCoroutineScope,
                    bottomSheetState = bottomSheetState,
                    handleImage = {
                        viewModel.onEvent(ProfileEvents.UpdateProfile(it))
                    },
                    handleUri = {
                        val image = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                        viewModel.onEvent(ProfileEvents.UpdateProfile(image))
                    }
                )
            },
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = scaffoldState,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(darkerBlue)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = Color.White
                            )
                        }
                    }
                },

                bottomBar = {

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Button(
                            onClick = {
                                viewModel.onEvent(ProfileEvents.LogOut)
                            },
                        ) {
                            Text(text = stringResource(id = R.string.log_out))
                        }
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    RoundImage(
                        image = if (userData.value.profileBitmap == null) {
                            painterResource(id = R.drawable.campfire)
                        } else {
                            rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(context)
                                    .data(data = userData.value.profileBitmap)
                                    .placeholder(R.drawable.campfire)
                                    .crossfade(true)
                                    .build()
                            )
                        },
                        modifier = roundImageModifier,
                        borderColor = mainOrange,
                        clickable = true,
                        onClick = {
                            rememberCoroutineScope.launch {
                                if (!bottomSheetState.isVisible) {
                                    bottomSheetState.show()
                                } else {
                                    bottomSheetState.hide()
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = userData.value.username)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = userData.value.email)

                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.Center)
            )
        }
    }
}
