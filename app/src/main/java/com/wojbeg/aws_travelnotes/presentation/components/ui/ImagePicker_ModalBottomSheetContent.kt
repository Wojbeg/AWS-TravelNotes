package com.wojbeg.aws_travelnotes.presentation.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.wojbeg.aws_travelnotes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImagePickerModalBottomSheetContent(
    context: Context,
    rememberCoroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    handleImage: (Bitmap) -> Unit,
    handleUri: (Uri) -> Unit,

) {

    //launcher picking image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleUri(it)
        }
    }

    //launcher for picking image from camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            handleImage(it)
        }
    }

    val toastText = stringResource(R.string.permission_denied)

    //launcher for runtime permission - camera
    val CameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->

        if (isPermissionGranted) {
            cameraLauncher.launch()

            rememberCoroutineScope.launch {
                bottomSheetState.hide()
            }
        } else {
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    //launcher for runtime permission - gallery
    val GalleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->

        if (isPermissionGranted) {
            galleryLauncher.launch("image/*")

            rememberCoroutineScope.launch {
                bottomSheetState.hide()
            }
        } else {
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.add_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                color = MaterialTheme.colors.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .background(MaterialTheme.colors.primary)
            )

            Text(
                text = stringResource(R.string.take_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) -> {
                                cameraLauncher.launch()
                                rememberCoroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                            else -> {
                                CameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }

                    }
                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
            Divider(
                modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            Text(
                text = stringResource(R.string.choose_file_gallery),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (PackageManager.PERMISSION_GRANTED) {

                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.READ_EXTERNAL_STORAGE
                            ) -> {
                                galleryLauncher.launch("image/*")
                                rememberCoroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                            else -> {
                                GalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        }
                    }
                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )

            Divider(
                modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            Text(
                text = stringResource(R.string.cancel),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        rememberCoroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                    .padding(15.dp),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
