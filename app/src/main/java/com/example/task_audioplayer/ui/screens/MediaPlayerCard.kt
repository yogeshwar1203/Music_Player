import android.text.style.AlignmentSpan
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.task_audioplayer.R
import com.example.task_audioplayer.data.Song
import com.example.task_audioplayer.data.songsList
import com.example.task_audioplayer.ui.helper.SongHelper
import kotlinx.coroutines.launch

@Composable
fun MediaPlayerCard(
    modifier: Modifier = Modifier,
    song: Song,
    onDownloadClick: (Song) -> Unit,
    navController: NavController
) {
    var songState by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0f) }
    var songDuration by remember { mutableStateOf(1f) }

    if (songState) {
        SongHelper.playStream(song.media) { position, duration ->
            currentPosition = position
            songDuration = duration
        }
    } else {
        SongHelper.pauseStream()
    }

    Card(modifier = modifier, elevation = 4.dp) {
        // Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back",
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        songState = false
                        SongHelper.releasePlayer()
                        navController.popBackStack()
                    }
            )
        }

        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Song Thumbnail
            AsyncImage(
                model = R.drawable.music,
                contentDescription = "Song thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Song Info
            Text(
                text = song.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = song.artist,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Progress Slider
            Slider(
                value = currentPosition,
                onValueChange = { newValue ->
                    currentPosition = newValue
                    SongHelper.seekTo(newValue)
                },
                valueRange = 0f..songDuration,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            // Duration Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentPosition),
                    fontSize = 12.sp
                )
                Text(
                    text = formatDuration(songDuration),
                    fontSize = 12.sp
                )
            }

            // Play/Pause Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (songState) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                    contentDescription = "Play/Pause",
                    modifier = Modifier
                        .clickable { songState = !songState }
                        .padding(10.dp)
                        .size(70.dp)
                )

                // Download Button
                IconButton(onClick = { onDownloadClick(song) }) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp)
                    )
                }
            }
        }
    }

    DisposableEffect(song.media) {
        onDispose {
            songState = false
            SongHelper.releasePlayer()
        }
    }
}

// Format duration (seconds to mm:ss)
fun formatDuration(milliseconds: Float): String {
    val seconds = (milliseconds / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
