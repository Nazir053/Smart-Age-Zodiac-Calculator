package com.example

import com.example.ads.AdManager
import com.example.domain.calculator.AgeCalculationEngine
import com.example.domain.calculator.ZodiacCalculator
import com.example.domain.model.AppLanguage
import com.example.domain.model.CalculationValidationResult
import com.example.domain.model.ZodiacSignId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun `exact age calculation returns accurate years months days and totals`() {
        val dob = LocalDate.of(2000, 5, 15)
        val current = LocalDate.of(2026, 11, 9)

        val validation = AgeCalculationEngine.calculateAge(dob, current)
        assertTrue(validation is CalculationValidationResult.Success)
        val result = (validation as CalculationValidationResult.Success).result

        assertEquals(26, result.exactYears)
        assertEquals(5, result.exactMonths)
        assertEquals(25, result.exactDays)
        assertEquals(26L, result.totalYears)
        assertEquals(317L, result.totalMonths)
        assertEquals(9674L, result.totalDays)
        assertEquals(1382L, result.totalWeeks)
        assertEquals(9674L * 24L, result.totalHours)
        assertEquals(26, result.completedBirthdays)
        assertEquals(27, result.ageOnNextBirthday)
    }

    @Test
    fun `leap year handling and february 29 birthday in non-leap year resolves to february 28`() {
        val leapDob = LocalDate.of(2000, 2, 29)
        val nonLeapBirthdayDate = LocalDate.of(2026, 2, 28)

        val validation = AgeCalculationEngine.calculateAge(leapDob, nonLeapBirthdayDate)
        assertTrue(validation is CalculationValidationResult.Success)
        val result = (validation as CalculationValidationResult.Success).result

        assertTrue(result.isFeb29Birth)
        assertTrue(result.isBirthYearLeap)
        assertFalse(result.isCurrentYearLeap)
        assertEquals(29, result.daysInBirthMonth)
        assertEquals(28, result.daysInCurrentMonth)
        assertEquals(26, result.exactYears)
        assertEquals(0, result.exactMonths)
        assertEquals(0, result.exactDays)
        assertTrue(result.isBirthdayToday)
        assertEquals(0L, result.daysUntilNextBirthday)
        assertEquals(LocalDate.of(2026, 2, 28), result.nextBirthday)
    }

    @Test
    fun `february 29 birthday in leap year resolves to february 29`() {
        val leapDob = LocalDate.of(2004, 2, 29)
        val leapCurrent = LocalDate.of(2028, 2, 20)

        val validation = AgeCalculationEngine.calculateAge(leapDob, leapCurrent)
        val result = (validation as CalculationValidationResult.Success).result

        assertTrue(result.isCurrentYearLeap)
        assertEquals(LocalDate.of(2028, 2, 29), result.nextBirthday)
        assertEquals(9L, result.daysUntilNextBirthday)
        assertEquals(LocalDate.of(2027, 2, 28), result.lastBirthday)
    }

    @Test
    fun `next and previous birthday and trilingual weekdays across year boundaries`() {
        val dob = LocalDate.of(1995, 12, 25)
        val current = LocalDate.of(2026, 11, 9)

        val result = (AgeCalculationEngine.calculateAge(dob, current) as CalculationValidationResult.Success).result

        assertEquals(LocalDate.of(2025, 12, 25), result.lastBirthday)
        assertEquals(LocalDate.of(2026, 12, 25), result.nextBirthday)
        assertEquals(46L, result.daysUntilNextBirthday)
        assertEquals(1, result.nextBirthdayRemainingMonths)
        assertEquals(16, result.nextBirthdayRemainingDays)
        assertEquals("Friday", result.nextBirthdayWeekdayEn)
        assertEquals("শুক্রবার", result.nextBirthdayWeekdayBn)
        assertEquals("शुक्रवार", result.nextBirthdayWeekdayHi)
    }

    @Test
    fun `birth weekday and trilingual zodiac names are accurate`() {
        val dob = LocalDate.of(1998, 3, 21) // Saturday, Aries
        val current = LocalDate.of(2026, 11, 9)

        val result = (AgeCalculationEngine.calculateAge(dob, current) as CalculationValidationResult.Success).result

        assertEquals("Saturday", result.birthWeekday(AppLanguage.ENGLISH))
        assertEquals("শনিবার", result.birthWeekday(AppLanguage.BENGALI))
        assertEquals("शनिवार", result.birthWeekday(AppLanguage.HINDI))
        assertEquals("Aries", result.zodiacProfile.localizedPrimaryName(AppLanguage.ENGLISH))
        assertEquals("মেষ রাশি", result.zodiacProfile.localizedPrimaryName(AppLanguage.BENGALI))
        assertEquals("मेष राशि", result.zodiacProfile.localizedPrimaryName(AppLanguage.HINDI))
    }

    @Test
    fun `all 12 zodiac signs and boundary dates are detected accurately`() {
        val expectations = listOf(
            LocalDate.of(2000, 3, 21) to ZodiacSignId.ARIES,
            LocalDate.of(2000, 4, 20) to ZodiacSignId.TAURUS,
            LocalDate.of(2000, 5, 21) to ZodiacSignId.GEMINI,
            LocalDate.of(2000, 6, 21) to ZodiacSignId.CANCER,
            LocalDate.of(2000, 7, 23) to ZodiacSignId.LEO,
            LocalDate.of(2000, 8, 23) to ZodiacSignId.VIRGO,
            LocalDate.of(2000, 9, 23) to ZodiacSignId.LIBRA,
            LocalDate.of(2000, 10, 23) to ZodiacSignId.SCORPIO,
            LocalDate.of(2000, 11, 22) to ZodiacSignId.SAGITTARIUS,
            LocalDate.of(2000, 12, 22) to ZodiacSignId.CAPRICORN,
            LocalDate.of(2000, 1, 20) to ZodiacSignId.AQUARIUS,
            LocalDate.of(2000, 2, 19) to ZodiacSignId.PISCES
        )

        expectations.forEach { (date, expectedSignId) ->
            val profile = ZodiacCalculator.getZodiacForDate(date)
            assertEquals("Mismatch for $date", expectedSignId, profile.id)
            assertTrue("Boundary date $date should be flagged as near cusp", profile.isNearCusp)
            assertTrue("Hindi name must not be blank", profile.hindiName.isNotBlank())
        }

        val midLeo = ZodiacCalculator.getZodiacForDate(LocalDate.of(2000, 8, 5))
        assertEquals(ZodiacSignId.LEO, midLeo.id)
        assertFalse(midLeo.isNearCusp)
    }

    @Test
    fun `future birth date and null date are strictly rejected in all 3 languages`() {
        val current = LocalDate.of(2026, 11, 9)
        val futureDob = LocalDate.of(2026, 11, 10)

        val futureValidation = AgeCalculationEngine.calculateAge(futureDob, current)
        assertTrue(futureValidation is CalculationValidationResult.FutureDateError)
        val err = futureValidation as CalculationValidationResult.FutureDateError
        assertTrue(err.message(AppLanguage.ENGLISH).isNotBlank())
        assertTrue(err.message(AppLanguage.BENGALI).isNotBlank())
        assertTrue(err.message(AppLanguage.HINDI).isNotBlank())

        val nullValidation = AgeCalculationEngine.calculateAge(null, current)
        assertTrue(nullValidation is CalculationValidationResult.InvalidDateError)
    }

    @Test
    fun `month end transitions and tables contain all required items across English Bangla and Hindi`() {
        val dob = LocalDate.of(2026, 1, 31)
        val current = LocalDate.of(2026, 3, 2)

        val result = (AgeCalculationEngine.calculateAge(dob, current) as CalculationValidationResult.Success).result
        assertEquals(0, result.exactYears)
        assertEquals(1, result.exactMonths)
        assertEquals(2, result.exactDays)

        AppLanguage.entries.forEach { lang ->
            assertEquals(14, result.getBasicAgeTable(lang).size)
            assertEquals(8, result.getBirthdayTable(lang).size)
            assertEquals(8, result.getCalendarTable(lang).size)
            assertEquals(7, result.getTimeStatsTable(lang).size)
        }
    }

    @Test
    fun `adManager enforces frequency cap and cooldown on interstitial transitions`() {
        val adManager = AdManager()
        val startMs = 1_000_000L

        assertFalse(adManager.onNaturalNavigationTransition(startMs))
        assertFalse(adManager.onNaturalNavigationTransition(startMs + 1000))
        assertFalse(adManager.onNaturalNavigationTransition(startMs + 2000))
        assertTrue(adManager.onNaturalNavigationTransition(startMs + 100_000))
        adManager.dismissInterstitial()
        assertFalse(adManager.showInterstitialOverlay.value)
    }
}
