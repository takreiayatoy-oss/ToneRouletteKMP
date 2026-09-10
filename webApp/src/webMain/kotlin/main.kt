import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import toneroulettekmp.webapp.generated.resources.Res
import androidx.compose.runtime.LaunchedEffect
import kotlin.random.Random
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}

private fun initWebAudio(
    cUrl: String,
    csUrl: String,
    dUrl: String,
    dsUrl: String,
    eUrl: String,
    fUrl: String,
    fsUrl: String,
    gUrl: String,
    gsUrl: String,
    aUrl: String,
    asUrl: String,
    bUrl: String
): Unit = js("""
    (() => {
        if (!window.__toneAudio) {
            const context = new AudioContext();

            window.__toneAudio = {
                context: context,
                buffers: {}
            };

            const load = async (key, url) => {
                const response = await fetch(url);
                const data = await response.arrayBuffer();

                window.__toneAudio.buffers[key] =
                    await context.decodeAudioData(data);
            };

            load("C", cUrl);
            load("Cs", csUrl);
            load("D", dUrl);
            load("Ds", dsUrl);
            load("E", eUrl);
            load("F", fUrl);
            load("Fs", fsUrl);
            load("G", gUrl);
            load("Gs", gsUrl);
            load("A", aUrl);
            load("As", asUrl);
            load("B", bUrl);
        }
    })()
""")

private fun playWebAudio(key: String): Unit = js("""
    (() => {
        const audio = window.__toneAudio;

        if (!audio) return;

        const buffer = audio.buffers[key];

        if (!buffer) return;

        const start = () => {
            const source = audio.context.createBufferSource();

            source.buffer = buffer;
            source.connect(audio.context.destination);
            source.start();
        };

        if (audio.context.state === "suspended") {
            audio.context.resume().then(start);
        } else {
            start();
        }
    })()
""")

@Composable
fun App() {
    var shape by remember { mutableStateOf("○") }
    var message by remember { mutableStateOf("ド") }

    var selectedKey by remember { mutableStateOf("C") }
    var minorPentatonic by remember { mutableStateOf(false) }

    var currentScale by remember { mutableStateOf(0) }
    var scaleNumber by remember { mutableStateOf(0) }
    var previousNumber by remember { mutableStateOf(-1) }

    val keyNumber = mapOf(
        "C" to 0,
        "Cs" to 1,
        "D" to 2,
        "Ds" to 3,
        "E" to 4,
        "F" to 5,
        "Fs" to 6,
        "G" to 7,
        "Gs" to 8,
        "A" to 9,
        "As" to 10,
        "B" to 11
    )

    LaunchedEffect(Unit) {
        initWebAudio(
            Res.getUri("files/note_c.wav"),
            Res.getUri("files/note_cs.wav"),
            Res.getUri("files/note_d.wav"),
            Res.getUri("files/note_ds.wav"),
            Res.getUri("files/note_e.wav"),
            Res.getUri("files/note_f.wav"),
            Res.getUri("files/note_fs.wav"),
            Res.getUri("files/note_g.wav"),
            Res.getUri("files/note_gs.wav"),
            Res.getUri("files/note_a.wav"),
            Res.getUri("files/note_as.wav"),
            Res.getUri("files/note_b.wav")
        )
    }

    fun playCurrentNote() {
        val soundNumber = (scaleNumber + currentScale) % 12

        val soundKey = when (soundNumber) {
            0 -> "C"
            1 -> "Cs"
            2 -> "D"
            3 -> "Ds"
            4 -> "E"
            5 -> "F"
            6 -> "Fs"
            7 -> "G"
            8 -> "Gs"
            9 -> "A"
            10 -> "As"
            11 -> "B"
            else -> "C"
        }

        playWebAudio(soundKey)
    }

    fun selectKey(key: String) {
        selectedKey = key

        currentScale = keyNumber[key] ?: 0

        if (minorPentatonic) {
            currentScale += 3

            if (currentScale >= 12) {
                currentScale -= 12
            }

            scaleNumber = 9
            shape = "⬡"
            message = "ラ"
        } else {
            scaleNumber = 0
            shape = "○"
            message = "ド"
        }

        playCurrentNote()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Tone Roulette",
            fontSize = 50.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (minorPentatonic) {
                "Minor Pentatonic"
            } else {
                "Major Scale"
            },
            fontSize = 20.sp,
            color = if (minorPentatonic) {
                Color.White
            } else {
                Color.Black
            },
            modifier = Modifier
                .background(
                    if (minorPentatonic) {
                        Color.Black
                    } else {
                        Color.Transparent
                    },
                    CircleShape
                )
                .border(
                    3.dp,
                    Color.Black,
                    CircleShape
                )
                .clickable {
                    minorPentatonic = !minorPentatonic

                    if (minorPentatonic) {
                        currentScale += 3

                        if (currentScale >= 12) {
                            currentScale -= 12
                        }
                    } else {
                        currentScale -= 3

                        if (currentScale < 0) {
                            currentScale += 12
                        }
                    }
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                "C" to "C",
                "Cs" to "C♯",
                "D" to "D",
                "Ds" to "D♯",
                "E" to "E",
                "F" to "F"
            ).forEach { (key, label) ->

                val isSelected = selectedKey == key

                Text(
                    text = label,
                    fontSize = 24.sp,
                    color = if (isSelected && minorPentatonic) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    modifier = Modifier
                        .background(
                            if (isSelected && minorPentatonic) {
                                Color.Black
                            } else {
                                Color.Transparent
                            },
                            CircleShape
                        )
                        .border(
                            if (isSelected) 3.dp else 0.dp,
                            Color.Black,
                            CircleShape
                        )
                        .clickable {
                            selectKey(key)
                        }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                "Fs" to "F♯",
                "G" to "G",
                "Gs" to "G♯",
                "A" to "A",
                "As" to "A♯",
                "B" to "B"
            ).forEach { (key, label) ->

                val isSelected = selectedKey == key

                Text(
                    text = label,
                    fontSize = 24.sp,
                    color = if (isSelected && minorPentatonic) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    modifier = Modifier
                        .background(
                            if (isSelected && minorPentatonic) {
                                Color.Black
                            } else {
                                Color.Transparent
                            },
                            CircleShape
                        )
                        .border(
                            if (isSelected) 3.dp else 0.dp,
                            Color.Black,
                            CircleShape
                        )
                        .clickable {
                            selectKey(key)
                        }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = shape,
            fontSize = 120.sp,
            modifier = Modifier.clickable {

                if (minorPentatonic) {

                    if (Random.nextInt(100) < 50) {
                        var number = previousNumber

                        if (Random.nextInt(2) == 0) {
                            number = previousNumber - 1

                            if (number < 0) {
                                number += 5
                            }
                        } else {
                            number = previousNumber + 1

                            if (number > 4) {
                                number -= 5
                            }
                        }

                        previousNumber = number

                    } else {

                        var number = Random.nextInt(5)

                        while (number == previousNumber) {
                            number = Random.nextInt(5)
                        }

                        previousNumber = number
                    }

                    when (previousNumber) {
                        0 -> {
                            scaleNumber = 0
                            shape = "○"
                            message = "ド"
                        }
                        1 -> {
                            scaleNumber = 2
                            shape = "△"
                            message = "レ"
                        }
                        2 -> {
                            scaleNumber = 4
                            shape = "□"
                            message = "ミ"
                        }
                        3 -> {
                            scaleNumber = 7
                            shape = "◇"
                            message = "ソ"
                        }
                        4 -> {
                            scaleNumber = 9
                            shape = "⬡"
                            message = "ラ"
                        }
                    }

                } else {

                    if (Random.nextInt(100) < 50) {
                        var number = previousNumber

                        if (Random.nextInt(2) == 0) {
                            number = previousNumber - 1

                            if (number < 0) {
                                number += 7
                            }
                        } else {
                            number = previousNumber + 1

                            if (number > 6) {
                                number -= 7
                            }
                        }

                        previousNumber = number

                    } else {

                        var number = Random.nextInt(7)

                        while (number == previousNumber) {
                            number = Random.nextInt(7)
                        }

                        previousNumber = number
                    }

                    when (previousNumber) {
                        0 -> {
                            scaleNumber = 0
                            shape = "○"
                            message = "ド"
                        }
                        1 -> {
                            scaleNumber = 2
                            shape = "△"
                            message = "レ"
                        }
                        2 -> {
                            scaleNumber = 4
                            shape = "□"
                            message = "ミ"
                        }
                        3 -> {
                            scaleNumber = 5
                            shape = "⬠"
                            message = "ファ"
                        }
                        4 -> {
                            scaleNumber = 7
                            shape = "◇"
                            message = "ソ"
                        }
                        5 -> {
                            scaleNumber = 9
                            shape = "⬡"
                            message = "ラ"
                        }
                        6 -> {
                            scaleNumber = 11
                            shape = "♡"
                            message = "シ"
                        }
                    }
                }

                playCurrentNote()
            }
        )

        Text(
            text = message,
            fontSize = 40.sp
        )
    }
}