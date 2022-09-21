package com.wojbeg.aws_travelnotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.wojbeg.aws_travelnotes.domain.models.Note

@Composable
fun ImageCard(
    note: Note,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick(note.id)
            }
        ,
        backgroundColor = Color.LightGray,
        shape = MaterialTheme.shapes.large
    ) {


        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (note.image != null || note.imageName != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(note.image)
                            .placeholder(R.drawable.campfire)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = stringResource(id = R.string.note_image),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .fillMaxWidth()
                        .aspectRatio(3f / 2f)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = note.name,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (note.description.trim() != "") {
                Text(
                    text = note.description,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}