package thesis.academic.ally.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.auth0.jwt.JWT
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import thesis.academic.ally.activity.userDataStore
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.AuthenticationResponse
import thesis.academic.ally.api.LoginBody
import thesis.academic.ally.datastore.UserCacheRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.max
import kotlin.math.round
import kotlin.random.Random


object Utils {

    val emailPattern = Regex("^[A-Za-z]{5,}\\.[0-9]{6}@laoag.sti.edu.ph\$")
    val passwordPattern = Regex("\\b(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}\\b")

    fun timeEquivalent(t1: LocalTime, t2: LocalTime): Boolean {
        return t1.hour == t2.hour && t1.minute == t2.minute
    }

    fun timeFullRange(t1: LocalTime, t2: LocalTime): Boolean {
        return t1.hour == LocalTime.MIN.hour && t1.minute == LocalTime.MIN.minute && t2.hour == LocalTime.MAX.hour && t2.minute == LocalTime.MAX.minute
    }

    fun localTimeToString(time: List<TutorAvailabilityData>): String {
        return time.joinToString("|") { "${toMilitaryTime(it.from.hour, it.from.minute)},${toMilitaryTime(it.to.hour, it.to.minute)}" }
    }

    fun stringToLocalTime(timeString: String): List<TutorAvailabilityData> {
        return timeString.split("|").mapIndexed { idx, time ->
            val dates = time.split(",").map { LocalTime.parse(it, DateTimeFormatter.ofPattern("hh:mm a")) }
            TutorAvailabilityData(DayOfWeek.of(idx + 1).name, dates[0], dates[1])
        }
    }

    fun isTutorAvailable(timeString: String): Boolean {
        return stringToLocalTime(timeString)
            .mapIndexed { idx, data -> Pair(idx, data) }
            .any {
                LocalDate.now(Clock.system(ZoneId.of("Asia/Shanghai"))).dayOfWeek.value - 1 == it.first &&
                        LocalTime.now(Clock.system(ZoneId.of("Asia/Shanghai"))).isAfter(it.second.from) &&
                        LocalTime.now(Clock.system(ZoneId.of("Asia/Shanghai"))).isBefore(it.second.to) }
    }

    fun formatTutoringAvailability(freeTutoringTime: String): String {
        return stringToLocalTime(freeTutoringTime).joinToString("\n") {
            if (timeEquivalent(it.from, it.to)) {
                "${it.day}:\nNot Available"
            } else if (timeFullRange(it.from, it.to)) {
                "${it.day}:\nAlways Available"
            } else {
                "${it.day}:\n${toMilitaryTime(it.from.hour, it.from.minute)} - ${toMilitaryTime(it.to.hour, it.to.minute)}"
            }
        }
    }

    fun roundRating(x: Double): Double = round(x * 10.0) / 10.0

    @SuppressLint("SimpleDateFormat")
    fun toMilitaryTime(h: Int, m: Int): String {
        return SimpleDateFormat("hh:mm a").format(SimpleDateFormat("HH:mm").parse("${h}:${m}") ?: "01:00 AM")
    }

    fun formatTime(t1: LocalDateTime, t2: LocalDateTime): String {
        return "${toMilitaryTime(t1.hour, t1.minute)} - ${toMilitaryTime(t2.hour, t2.minute)}"
    }

    fun formatDate(date: LocalDateTime): String {
        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    fun formatDate(date: LocalDate): String {
        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    fun eligibilityComputingAlgorithm(score: Int, items: Int, eval: Double): Double {
        val percentage = score.toDouble() / items
        return if (percentage >= eval) {
            val multiplier = (eval - 0.5) / (1.0 - eval)
            percentage - ((1.0 - percentage) * multiplier)
        } else {
            val multiplier = (0.5 - eval) / eval
            percentage + (percentage * multiplier)
        }
    }

    fun evaluateAnswer(assessmentData: List<List<String>>, assessmentAnswers: List<String>, type: String): Int {
        var totalScore = 0
        val answerIdx = if (type == "Multiple Choice") 6 else 2
        for (idx in assessmentData.indices) {
            if (assessmentData[idx][answerIdx].lowercase() == assessmentAnswers[idx].lowercase()) {
                totalScore += 1
            }
        }
        return totalScore
    }

    fun generateRandomColor(seed: Int): Color {
        val rand = Random(seed)
        return Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 255)
    }

    fun convertToDate(dateStr: String): LocalDateTime {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
    }

    fun showToast(achievements: List<String>, context: Context) {
        achievements.forEach { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    fun validateDate(dateStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
        return LocalDateTime.parse(dateStr, formatter).format(formatter)
    }

    fun validateDateAndNavigate(dateStr: String): String {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")).toString()
    }

    fun convertToImage(encodedImage: String): ImageBitmap {
        val decodedBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size).asImageBitmap()
    }

    fun convertToImage(imageUri: Uri?, context: Context): ImageBitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            updateImage(ImageDecoder.decodeBitmap(source)).asImageBitmap()
        } else {
            updateImage(MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)).asImageBitmap()
        }
    }

    fun bitmapToFile(imageBitmap: ImageBitmap, context: Context): File {
        val bitmap = imageBitmap.asAndroidBitmap()
        val file = File(context.cacheDir, LocalDateTime.now().toString() + ".png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun updateImage(image: Bitmap, imageSize: Float = 200f): Bitmap {
        val ratio = max(imageSize / image.width, imageSize / image.height)
        val newWidth = round(ratio * image.width).toInt()
        val newHeight = round(ratio * image.height).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
        return Bitmap.createBitmap(scaledBitmap, 0, 0, 199, 199)
    }

    fun checkToken(authToken: String): Boolean {
        return try { JWT.decode(authToken).expiresAt.before(Date()) } catch (e: Exception) { true }
    }

    fun getFieldId(field: AssessmentType): Int {
        return when (field) {
            is AssessmentType.MultipleChoiceFields -> field.id
            is AssessmentType.IdentificationFields -> field.id
            is AssessmentType.TrueOrFalseFields -> field.id
        }
    }

    suspend fun checkAuthentication(
        context: Context,
        userCacheRepository: UserCacheRepository,
        academicallyApi: AcademicallyApi
    ) {
        val preferences = userCacheRepository.userDataStore.data.first()

        if (checkToken(preferences.authToken)) {
            val authResponse = academicallyApi.login(
                LoginBody(0, 0.0, 0, preferences.email, preferences.password, preferences.role, "", Firebase.messaging.token.await())
            )

            val refreshedToken = when (authResponse) {
                is AuthenticationResponse.SuccessResponse -> authResponse.token
                is AuthenticationResponse.SuccessNoAssessment -> authResponse.token
                is AuthenticationResponse.ValidationError -> ""
                is AuthenticationResponse.ErrorResponse -> ""
            }

            userCacheRepository.updateAuthToken(refreshedToken)

            Toast.makeText(context, "Your token not valid, refreshing Login.", Toast.LENGTH_LONG).show()
        }
    }
}