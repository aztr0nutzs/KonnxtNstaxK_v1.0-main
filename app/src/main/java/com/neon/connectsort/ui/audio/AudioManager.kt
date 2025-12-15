package com.neon.connectsort.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.neon.connectsort.R
import com.neon.connectsort.core.data.AppPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AudioManager(
    context: Context,
    preferencesRepository: AppPreferencesRepository
) {

    enum class Sample {
        COIN, POWER_UP, VICTORY, SELECT
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val soundPool: SoundPool
    private var mediaPlayer: MediaPlayer? = null
    private val samples = mutableMapOf<Sample, Int>()

    private var musicEnabled = true
    private var soundEnabled = true
    private var volume = 0.8f

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(4)
            .build()

        samples[Sample.COIN] = soundPool.load(context, R.raw.coin_ping, 1)
        samples[Sample.POWER_UP] = soundPool.load(context, R.raw.power_up_ping, 1)
        samples[Sample.VICTORY] = soundPool.load(context, R.raw.victory_ping, 1)
        samples[Sample.SELECT] = soundPool.load(context, R.raw.select_ping, 1)

        mediaPlayer = MediaPlayer.create(context, R.raw.bg_loop).apply {
            isLooping = true
            setVolume(volume, volume)
        }

        scope.launch {
            preferencesRepository.getAudioSettingsFlow().collect { settings ->
                soundEnabled = settings.soundEnabled
                musicEnabled = settings.musicEnabled
                volume = settings.volume
                mediaPlayer?.setVolume(volume, volume)
                if (musicEnabled) {
                    startMusic()
                } else {
                    stopMusic()
                }
            }
        }
    }

    fun playSample(sample: Sample) {
        if (!soundEnabled) return
        val id = samples[sample] ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    private fun startMusic() {
        if (mediaPlayer?.isPlaying == true) return
        mediaPlayer?.start()
    }

    private fun stopMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun release() {
        scope.cancel()
        soundPool.release()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
