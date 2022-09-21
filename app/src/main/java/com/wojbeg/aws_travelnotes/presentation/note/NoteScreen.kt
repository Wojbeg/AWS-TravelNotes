package com.wojbeg.aws_travelnotes.presentation.note

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.presentation.components.ImagePickerModalBottomSheetContent
import com.wojbeg.aws_travelnotes.presentation.components.RoundImage
import com.wojbeg.aws_travelnotes.presentation.ui.theme.darkerBlue
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainBlue
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.presentation.util.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteScreen(
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel(),
    id: String? = null
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val rememberCoroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val imageState = viewModel.imageState
    val noteState = viewModel.noteState

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(NoteEvents.CheckId(id))
    }

    LaunchedEffect(key1 = true) {
        viewModel.noteUIEvents.collect { event ->
            when(event) {
                NoteViewModel.NoteUIEvents.Success, NoteViewModel.NoteUIEvents.NotFound  -> {
                    navController.popBackStack()
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            ImagePickerModalBottomSheetContent(
                context = context,
                rememberCoroutineScope = rememberCoroutineScope,
                bottomSheetState = bottomSheetState,
                handleImage = {
                    viewModel.onEvent(NoteEvents.HandleImage(it))
                },
                handleUri = {
                    val image = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    viewModel.onEvent(NoteEvents.HandleImage(image))
                }
            )
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(darkerBlue)
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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

                    if (noteState.isRemovable) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(NoteEvents.Remove)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete),
                                tint = Color.White
                            )
                        }
                    }

                }
            },
        ) {

            Box(modifier = Modifier
                .padding(it)
                .fillMaxSize()
            ) {

                Box(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                ) {

                    Card(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxSize()
                            .shadow(5.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                        backgroundColor = Color.LightGray,
                        shape = MaterialTheme.shapes.large
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {

                            Image(
                                painter = if (imageState.image == null) {
                                    painterResource(id = R.drawable.campfire)
                                } else {
                                    rememberAsyncImagePainter(model = imageState.image)
                                },
                                contentDescription = stringResource(id = R.string.image_to_pick),
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.large)
                                    .fillMaxWidth()
                                    .aspectRatio(3f / 2f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        rememberCoroutineScope.launch {
                                            if (!bottomSheetState.isVisible) {
                                                bottomSheetState.show()
                                            } else {
                                                bottomSheetState.hide()
                                            }
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (noteState.error != null) {
                                Text(
                                    text = noteState.error!!,
                                    color = MaterialTheme.colors.error
                                )
                            }

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                value = noteState.title,
                                onValueChange = {
                                    if (!noteState.isLoading) {
                                        viewModel.onEvent(NoteEvents.UpdateTitle(it))
                                    }
                                },
                                label = { 
                                    Text(text = stringResource(id = R.string.title)) 
                                },
                                isError = noteState.titleError != null
                            )

                            if (noteState.titleError != null) {
                                Text(
                                    text = noteState.titleError!!,
                                    color = MaterialTheme.colors.error
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = noteState.description,
                                onValueChange = {
                                    if (!noteState.isLoading) {
                                        viewModel.onEvent(NoteEvents.UpdateDescription(it))
                                    }
                                },
                                label = { 
                                    Text(text = stringResource(id = R.string.description)) 
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 8.dp)
                        .background(Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (!noteState.isLoading) {
                                viewModel.onEvent(NoteEvents.SaveNote)
                            }
                        },
                    ) {
                        Text(text= stringResource(id = R.string.save))
                    }
                }

                if (noteState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}
