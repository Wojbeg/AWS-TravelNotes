package com.wojbeg.aws_travelnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainOrange
import com.wojbeg.aws_travelnotes.R
import com.wojbeg.aws_travelnotes.presentation.components.RoundImage
import com.wojbeg.aws_travelnotes.presentation.home.HomeViewModel
import com.wojbeg.aws_travelnotes.presentation.ui.theme.backgroundWhite
import com.wojbeg.aws_travelnotes.presentation.ui.theme.darkerBlue
import com.wojbeg.aws_travelnotes.presentation.ui.theme.mainBlue
import com.wojbeg.aws_travelnotes.presentation.util.Screen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val homeState = viewModel.homeState

    val notesState = viewModel.userData.collectAsState()

    val scaffoldState = rememberScaffoldState()

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Note.route)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = darkerBlue)
            }
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkerBlue)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(onClick = {
                    navController.navigate(Screen.ProfileScreen.route)
                }) {
                    RoundImage(
                        image = if (notesState.value.profileBitmap==null) {
                            painterResource(id = R.drawable.campfire)
                        } else {
                            rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(context)
                                    .data(data = notesState.value.profileBitmap)
                                    .placeholder(R.drawable.campfire)
                                    .crossfade(true)
                                    .build()
                            )
                        },
                        modifier = Modifier.size(32.dp),
                        borderColor = mainOrange
                    )
                }

                IconButton(onClick = {
                    navController.navigate(Screen.ProfileScreen.route)
                }) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundWhite),
            ) {

                if (notesState.value.notes.isEmpty()) {

                    Text(
                        text = stringResource(id = R.string.empty_notes),
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        items(notesState.value.notes) { note ->
                            ImageCard(
                                note,
                                modifier = Modifier
                                    .padding(all = 16.dp)
                                    .wrapContentHeight(),
                                onClick = {

                                    navController.navigate(
                                        Screen.Note.route + "?id=${note.id}",
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if (homeState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
