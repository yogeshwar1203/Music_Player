package com.example.task_audioplayer.data

data class Song(
    val title: String,
    val artist: String,
    val duration: String,
    val media: String
) {
    constructor() : this("title","me","10", songsList.get(1).media)
}

val songsList: List<Song> = listOf(
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=lofi-study-112191.mp3",
        duration = "2:27",
        title = "1.Lofi Study",
        artist = "FasSSounds"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2023/03/02/audio_501a4aea8a.mp3?filename=genres-hiphop-lofi-141320.mp3",
        duration = "12:45",
        title = "2.genres hiphop lofi",
        artist = "teodholina"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/09/02/audio_72502a492a.mp3?filename=empty-mind-118973.mp3",
        duration = "2:55",
        title = "3.Empty mind",
        artist = "Lofi hour"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=lofi-study-112191.mp3",
        duration = "2:27",
        title = "4.Lofi Study",
        artist = "FasSSounds"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2023/03/02/audio_501a4aea8a.mp3?filename=genres-hiphop-lofi-141320.mp3",
        duration = "12:45",
        title = "5.genres hiphop lofi",
        artist = "teodholina"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/09/02/audio_72502a492a.mp3?filename=empty-mind-118973.mp3",
        duration = "2:55",
        title = "6.Empty mind",
        artist = "Lofi hour"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=lofi-study-112191.mp3",
        duration = "2:27",
        title = "7.Lofi Study",
        artist = "FasSSounds"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2023/03/02/audio_501a4aea8a.mp3?filename=genres-hiphop-lofi-141320.mp3",
        duration = "12:45",
        title = "8.genres hiphop lofi",
        artist = "teodholina"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/09/02/audio_72502a492a.mp3?filename=empty-mind-118973.mp3",
        duration = "2:55",
        title = "9.Empty mind",
        artist = "Lofi hour"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=lofi-study-112191.mp3",
        duration = "2:27",
        title = "10.Lofi Study",
        artist = "FasSSounds"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2023/03/02/audio_501a4aea8a.mp3?filename=genres-hiphop-lofi-141320.mp3",
        duration = "12:45",
        title = "11.genres hiphop lofi",
        artist = "teodholina"
    ),
    Song(
        media = "https://cdn.pixabay.com/download/audio/2022/09/02/audio_72502a492a.mp3?filename=empty-mind-118973.mp3",
        duration = "2:55",
        title = "12.Empty mind",
        artist = "Lofi hour"
    ),
)


