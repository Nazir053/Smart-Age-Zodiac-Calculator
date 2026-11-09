package com.example.domain.calculator

import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.CalculationValidationResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TrilingualLabel(
    val en: String,
    val bn: String,
    val hi: String
) {
    fun get(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> en
        AppLanguage.BENGALI -> bn
        AppLanguage.HINDI -> hi
    }
}

/**
 * High-precision chronological Age Calculation Engine built on java.time.
 *
 * FEBRUARY 29 NON-LEAP YEAR POLICY:
 * When a user's Date of Birth is February 29 (in a leap year), their birthday in any
 * non-leap year is consistently evaluated as February 28 (via `resolveBirthdayForYear`).
 */
object AgeCalculationEngine {

    fun resolveBirthdayForYear(birthDate: LocalDate, targetYear: Int): LocalDate {
        return if (birthDate.month == Month.FEBRUARY && birthDate.dayOfMonth == 29 && !Year.isLeap(targetYear.toLong())) {
            LocalDate.of(targetYear, Month.FEBRUARY, 28)
        } else {
            LocalDate.of(targetYear, birthDate.month, birthDate.dayOfMonth)
        }
    }

    fun calculateAge(
        birthDate: LocalDate?,
        currentDate: LocalDate = LocalDate.now()
    ): CalculationValidationResult {
        if (birthDate == null) {
            return CalculationValidationResult.InvalidDateError(
                messageEn = "Please select a valid date of birth.",
                messageBn = "অনুগ্রহ করে একটি সঠিক জন্ম তারিখ নির্বাচন করুন।",
                messageHi = "कृपया एक मान्य जन्म तिथि चुनें।"
            )
        }

        if (birthDate.isAfter(currentDate)) {
            return CalculationValidationResult.FutureDateError(
                messageEn = "Future birth dates are not allowed. Please select today or an earlier date.",
                messageBn = "ভবিষ্যতের জন্ম তারিখ গ্রহণযোগ্য নয়। আজকের বা তার আগের তারিখ নির্বাচন করুন।",
                messageHi = "भविष्य की जन्म तिथि मान्य नहीं है। कृपया आज या उससे पहले की तिथि चुनें।"
            )
        }

        val isFeb29Birth = birthDate.month == Month.FEBRUARY && birthDate.dayOfMonth == 29

        val effectivePeriod: Period = if (
            isFeb29Birth &&
            !Year.isLeap(currentDate.year.toLong()) &&
            currentDate.month == Month.FEBRUARY &&
            currentDate.dayOfMonth == 28
        ) {
            Period.of(currentDate.year - birthDate.year, 0, 0)
        } else {
            Period.between(birthDate, currentDate)
        }

        val exactYears = effectivePeriod.years.coerceAtLeast(0)
        val exactMonths = effectivePeriod.months.coerceAtLeast(0)
        val exactDays = effectivePeriod.days.coerceAtLeast(0)

        val totalYears = exactYears.toLong()
        val totalMonths = if (
            isFeb29Birth &&
            !Year.isLeap(currentDate.year.toLong()) &&
            currentDate.month == Month.FEBRUARY &&
            currentDate.dayOfMonth == 28
        ) {
            (currentDate.year - birthDate.year) * 12L
        } else {
            ChronoUnit.MONTHS.between(birthDate, currentDate)
        }
        val totalWeeks = ChronoUnit.WEEKS.between(birthDate, currentDate)
        val totalDays = ChronoUnit.DAYS.between(birthDate, currentDate)

        val totalHours = totalDays * 24L
        val totalMinutes = totalHours * 60L
        val totalSeconds = totalMinutes * 60L
        val approximateYearsDecimal = String.format(Locale.US, "%.2f", totalDays / 365.2425)

        val birthdayThisYear = resolveBirthdayForYear(birthDate, currentDate.year)
        val isBirthdayToday = currentDate.isEqual(birthdayThisYear)

        val lastBirthday: LocalDate = when {
            currentDate.isEqual(birthdayThisYear) || currentDate.isAfter(birthdayThisYear) -> birthdayThisYear
            else -> resolveBirthdayForYear(birthDate, currentDate.year - 1)
        }

        val nextBirthday: LocalDate = when {
            currentDate.isEqual(birthdayThisYear) -> birthdayThisYear
            currentDate.isBefore(birthdayThisYear) -> birthdayThisYear
            else -> resolveBirthdayForYear(birthDate, currentDate.year + 1)
        }

        val daysSinceLastBirthday = ChronoUnit.DAYS.between(lastBirthday, currentDate).coerceAtLeast(0L)
        val daysUntilNextBirthday = ChronoUnit.DAYS.between(currentDate, nextBirthday).coerceAtLeast(0L)

        val remainingPeriodToNextBirthday = Period.between(currentDate, nextBirthday)
        val nextBirthdayRemainingMonths = remainingPeriodToNextBirthday.months + (remainingPeriodToNextBirthday.years * 12)
        val nextBirthdayRemainingDays = remainingPeriodToNextBirthday.days

        val completedBirthdays = exactYears
        val ageOnNextBirthday = if (isBirthdayToday) exactYears else exactYears + 1

        val cycleNextAnchor = if (isBirthdayToday) {
            resolveBirthdayForYear(birthDate, currentDate.year + 1)
        } else {
            nextBirthday
        }
        val totalCycleDays = ChronoUnit.DAYS.between(lastBirthday, cycleNextAnchor).coerceAtLeast(1L)
        val birthdayCycleProgress = if (isBirthdayToday) {
            1.0f
        } else {
            (daysSinceLastBirthday.toFloat() / totalCycleDays.toFloat()).coerceIn(0f, 1f)
        }

        val isBirthYearLeap = Year.isLeap(birthDate.year.toLong())
        val isCurrentYearLeap = Year.isLeap(currentDate.year.toLong())
        val daysInBirthMonth = YearMonth.of(birthDate.year, birthDate.month).lengthOfMonth()
        val daysInCurrentMonth = YearMonth.of(currentDate.year, currentDate.month).lengthOfMonth()
        val birthMonthNumber = birthDate.monthValue
        val birthQuarter = when (birthMonthNumber) {
            in 1..3 -> "Q1 (Jan – Mar)"
            in 4..6 -> "Q2 (Apr – Jun)"
            in 7..9 -> "Q3 (Jul – Sep)"
            else -> "Q4 (Oct – Dec)"
        }
        val birthDayOfYear = birthDate.dayOfYear

        val birthWeekday = weekdayLabel(birthDate.dayOfWeek)
        val nextBirthdayWeekday = weekdayLabel(nextBirthday.dayOfWeek)
        val birthMonthName = monthLabel(birthDate.month)
        val birthSeason = seasonLabel(birthDate.month)

        val zodiacProfile = ZodiacCalculator.getZodiacForDate(birthDate)

        return CalculationValidationResult.Success(
            AgeCalculationResult(
                birthDate = birthDate,
                currentDate = currentDate,
                exactYears = exactYears,
                exactMonths = exactMonths,
                exactDays = exactDays,
                totalYears = totalYears,
                totalMonths = totalMonths,
                totalWeeks = totalWeeks,
                totalDays = totalDays,
                totalHours = totalHours,
                totalMinutes = totalMinutes,
                totalSeconds = totalSeconds,
                approximateYearsDecimal = approximateYearsDecimal,
                completedBirthdays = completedBirthdays,
                birthWeekdayEn = birthWeekday.en,
                birthWeekdayBn = birthWeekday.bn,
                birthWeekdayHi = birthWeekday.hi,
                birthYear = birthDate.year,
                birthMonthNameEn = birthMonthName.en,
                birthMonthNameBn = birthMonthName.bn,
                birthMonthNameHi = birthMonthName.hi,
                birthMonthNumber = birthMonthNumber,
                birthQuarter = birthQuarter,
                birthDayOfYear = birthDayOfYear,
                isBirthYearLeap = isBirthYearLeap,
                isCurrentYearLeap = isCurrentYearLeap,
                daysInBirthMonth = daysInBirthMonth,
                daysInCurrentMonth = daysInCurrentMonth,
                lastBirthday = lastBirthday,
                nextBirthday = nextBirthday,
                daysSinceLastBirthday = daysSinceLastBirthday,
                daysUntilNextBirthday = daysUntilNextBirthday,
                nextBirthdayRemainingMonths = nextBirthdayRemainingMonths,
                nextBirthdayRemainingDays = nextBirthdayRemainingDays,
                nextBirthdayWeekdayEn = nextBirthdayWeekday.en,
                nextBirthdayWeekdayBn = nextBirthdayWeekday.bn,
                nextBirthdayWeekdayHi = nextBirthdayWeekday.hi,
                ageOnNextBirthday = ageOnNextBirthday,
                birthSeasonEn = birthSeason.en,
                birthSeasonBn = birthSeason.bn,
                birthSeasonHi = birthSeason.hi,
                birthdayCycleProgress = birthdayCycleProgress,
                isBirthdayToday = isBirthdayToday,
                isFeb29Birth = isFeb29Birth,
                zodiacProfile = zodiacProfile
            )
        )
    }

    fun weekdayLabel(dayOfWeek: DayOfWeek): TrilingualLabel = when (dayOfWeek) {
        DayOfWeek.MONDAY -> TrilingualLabel("Monday", "সোমবার", "सोमवार")
        DayOfWeek.TUESDAY -> TrilingualLabel("Tuesday", "মঙ্গলবার", "मंगलवार")
        DayOfWeek.WEDNESDAY -> TrilingualLabel("Wednesday", "বুধবার", "बुधवार")
        DayOfWeek.THURSDAY -> TrilingualLabel("Thursday", "বৃহস্পতিবার", "गुरुवार")
        DayOfWeek.FRIDAY -> TrilingualLabel("Friday", "শুক্রবার", "शुक्रवार")
        DayOfWeek.SATURDAY -> TrilingualLabel("Saturday", "শনিবার", "शनिवार")
        DayOfWeek.SUNDAY -> TrilingualLabel("Sunday", "রবিবার", "रविवार")
    }

    fun monthLabel(month: Month): TrilingualLabel = when (month) {
        Month.JANUARY -> TrilingualLabel("January", "জানুয়ারি", "जनवरी")
        Month.FEBRUARY -> TrilingualLabel("February", "ফেব্রুয়ারি", "फ़रवरी")
        Month.MARCH -> TrilingualLabel("March", "মার্চ", "मार्च")
        Month.APRIL -> TrilingualLabel("April", "এপ্রিল", "अप्रैल")
        Month.MAY -> TrilingualLabel("May", "মে", "मई")
        Month.JUNE -> TrilingualLabel("June", "জুন", "जून")
        Month.JULY -> TrilingualLabel("July", "জুলাই", "जुलाई")
        Month.AUGUST -> TrilingualLabel("August", "আগস্ট", "अगस्त")
        Month.SEPTEMBER -> TrilingualLabel("September", "সেপ্টেম্বর", "सितंबर")
        Month.OCTOBER -> TrilingualLabel("October", "অক্টোবর", "अक्टूबर")
        Month.NOVEMBER -> TrilingualLabel("November", "নভেম্বর", "नवंबर")
        Month.DECEMBER -> TrilingualLabel("December", "ডিসেম্বর", "दिसंबर")
    }

    fun seasonLabel(month: Month): TrilingualLabel = when (month) {
        Month.MARCH, Month.APRIL, Month.MAY -> TrilingualLabel("Spring", "বসন্ত (Spring)", "वसंत (Spring)")
        Month.JUNE, Month.JULY, Month.AUGUST -> TrilingualLabel("Summer", "গ্রীষ্ম (Summer)", "ग्रीष्म (Summer)")
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> TrilingualLabel("Autumn", "শরৎ/হেমন্ত (Autumn)", "शरद (Autumn)")
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> TrilingualLabel("Winter", "শীত (Winter)", "शीत (Winter)")
    }
}
