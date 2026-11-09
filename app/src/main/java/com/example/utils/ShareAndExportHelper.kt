package com.example.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object ShareAndExportHelper {

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun shareTextReport(context: Context, reportText: String, title: String = "Share Age Report") {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Smart Age & Zodiac Report")
            putExtra(Intent.EXTRA_TEXT, reportText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(chooser) }
    }

    fun shareAgeSummaryCardImage(
        context: Context,
        result: AgeCalculationResult,
        language: AppLanguage,
        profileName: String = ""
    ) {
        val bitmap = renderSummaryCardBitmap(result, language, profileName)
        val cacheDir = File(context.cacheDir, "shared_cards").apply { mkdirs() }
        val file = File(cacheDir, "smart_age_zodiac_card.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, result.formatFullReportText(language))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Age Summary Card").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(chooser) }
    }

    fun renderSummaryCardBitmap(
        result: AgeCalculationResult,
        language: AppLanguage,
        profileName: String = ""
    ): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    "#0B1120".toColorInt(),
                    "#1E1B4B".toColorInt(),
                    "#0F172A".toColorInt()
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = "#4DFBBF24".toColorInt()
        }
        canvas.drawRoundRect(RectF(48f, 48f, width - 48f, height - 48f), 42f, 42f, borderPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#FBBF24".toColorInt()
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.06f
        }
        val headerTitle = when (language) {
            AppLanguage.ENGLISH -> "SMART AGE & ZODIAC CALCULATOR"
            AppLanguage.BENGALI -> "স্মার্ট বয়স ও রাশিফল ড্যাশবোর্ড"
            AppLanguage.HINDI -> "स्मार्ट आयु और राशिफल डैशबोर्ड"
        }
        canvas.drawText(headerTitle, 96f, 130f, titlePaint)

        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#CBD5E1".toColorInt()
            textSize = 30f
        }
        val subtitleText = if (profileName.isNotBlank()) {
            "$profileName  •  DOB: ${result.formattedBirthDate()}"
        } else {
            "Date of Birth: ${result.formattedBirthDate()}  (${result.birthWeekday(language)})"
        }
        canvas.drawText(subtitleText, 96f, 185f, subtitlePaint)

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#1E293B".toColorInt()
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(88f, 230f, width - 88f, 490f), 32f, 32f, cardPaint)

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#94A3B8".toColorInt()
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val exactLabel = when (language) {
            AppLanguage.ENGLISH -> "EXACT CHRONOLOGICAL AGE"
            AppLanguage.BENGALI -> "সঠিক ক্যালেন্ডার বয়স"
            AppLanguage.HINDI -> "सटीक कैलेंडर आयु"
        }
        canvas.drawText(exactLabel, 128f, 290f, labelPaint)

        val bigAgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 62f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(result.exactAgeSummary(language), 128f, 385f, bigAgePaint)

        val subAgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#A5B4FC".toColorInt()
            textSize = 28f
        }
        canvas.drawText(
            String.format(
                Locale.US,
                "%,d Months  •  %,d Weeks  •  %,d Days Lived",
                result.totalMonths,
                result.totalWeeks,
                result.totalDays
            ),
            128f,
            445f,
            subAgePaint
        )

        val rowBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#151F32".toColorInt()
        }
        val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#94A3B8".toColorInt()
            textSize = 28f
        }
        val valPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#F8FAFC".toColorInt()
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val summaryRows = listOf(
            when (language) {
                AppLanguage.ENGLISH -> "Next Birthday"
                AppLanguage.BENGALI -> "পরবর্তী জন্মদিন"
                AppLanguage.HINDI -> "अगला जन्मदिन"
            } to "${result.formattedNextBirthday()} (${result.nextBirthdayWeekday(language)})",
            when (language) {
                AppLanguage.ENGLISH -> "Days Until Next Birthday"
                AppLanguage.BENGALI -> "জন্মদিনের বাকি দিন"
                AppLanguage.HINDI -> "जन्मदिन में शेष दिन"
            } to "${result.daysUntilNextBirthday} Days",
            when (language) {
                AppLanguage.ENGLISH -> "Completed Birthdays"
                AppLanguage.BENGALI -> "সম্পন্ন জন্মদিন"
                AppLanguage.HINDI -> "पूर्ण जन्मदिन"
            } to "${result.completedBirthdays}",
            when (language) {
                AppLanguage.ENGLISH -> "Western Zodiac Sign"
                AppLanguage.BENGALI -> "পাশ্চাত্য রাশি (Zodiac)"
                AppLanguage.HINDI -> "पश्चिमी राशि (Zodiac)"
            } to "${result.zodiacProfile.symbol} ${result.zodiacProfile.localizedPrimaryName(language)}",
            when (language) {
                AppLanguage.ENGLISH -> "Element & Ruling Planet"
                AppLanguage.BENGALI -> "রাশি উপাদান ও গ্রহ"
                AppLanguage.HINDI -> "तत्व और स्वामी ग्रह"
            } to "${result.zodiacProfile.element(language)} • ${result.zodiacProfile.rulingPlanet(language)}",
            when (language) {
                AppLanguage.ENGLISH -> "Total Hours (Date-Midnight)"
                AppLanguage.BENGALI -> "মোট ঘণ্টা (আনুমানিক)"
                AppLanguage.HINDI -> "कुल घंटे (अनुमानित)"
            } to String.format(Locale.US, "%,d hrs", result.totalHours),
            when (language) {
                AppLanguage.ENGLISH -> "Birth Season & Quarter"
                AppLanguage.BENGALI -> "জন্ম ঋতু ও কোয়ার্টার"
                AppLanguage.HINDI -> "जन्म ऋतु और तिमाही"
            } to "${result.birthSeason(language)} • ${result.birthQuarter}"
        )

        var topY = 535f
        summaryRows.forEach { (k, v) ->
            canvas.drawRoundRect(RectF(88f, topY, width - 88f, topY + 86f), 18f, 18f, rowBgPaint)
            canvas.drawText(k, 124f, topY + 54f, keyPaint)
            canvas.drawText(v, width - 124f, topY + 54f, valPaint)
            topY += 102f
        }

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#64748B".toColorInt()
            textSize = 24f
        }
        canvas.drawText(
            "Generated on ${result.formattedCurrentDate()} • Smart Age & Zodiac Calculator",
            96f,
            height - 80f,
            footerPaint
        )

        return bitmap
    }
}
