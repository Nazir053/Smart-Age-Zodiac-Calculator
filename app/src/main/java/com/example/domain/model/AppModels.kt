package com.example.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BENGALI("bn", "Bangla", "বাংলা"),
    HINDI("hi", "Hindi", "हिन्दी")
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AccentPalette(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val titleHi: String,
    val requiresRewardUnlock: Boolean
) {
    DEFAULT("default", "Royal Indigo & Gold", "রয়্যাল ইন্ডিগো ও গোল্ড", "रॉयल इंडिगो और गोल्ड", false),
    CELESTIAL_GOLD("celestial_gold", "Celestial Gold", "সেলেস্টিয়াল গোল্ড", "सेलेस्टियल गोल्ड", true),
    COSMIC_VIOLET("cosmic_violet", "Cosmic Violet", "কসমিক ভায়োলেট", "कॉस्मिक वायलेट", true);

    fun title(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> titleEn
        AppLanguage.BENGALI -> titleBn
        AppLanguage.HINDI -> titleHi
    }
}

enum class HistorySortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST
}

enum class ZodiacSignId {
    ARIES,
    TAURUS,
    GEMINI,
    CANCER,
    LEO,
    VIRGO,
    LIBRA,
    SCORPIO,
    SAGITTARIUS,
    CAPRICORN,
    AQUARIUS,
    PISCES
}

data class ZodiacProfile(
    val id: ZodiacSignId,
    val englishName: String,
    val bengaliName: String,
    val hindiName: String,
    val symbol: String,
    val elementEn: String,
    val elementBn: String,
    val elementHi: String,
    val rulingPlanetEn: String,
    val rulingPlanetBn: String,
    val rulingPlanetHi: String,
    val dateRangeEn: String,
    val dateRangeBn: String,
    val dateRangeHi: String,
    val modalityEn: String,
    val modalityBn: String,
    val modalityHi: String,
    val luckyColorEn: String,
    val luckyColorBn: String,
    val luckyColorHi: String,
    val luckyDayEn: String,
    val luckyDayBn: String,
    val luckyDayHi: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val descriptionHi: String,
    val strengthsEn: List<String>,
    val strengthsBn: List<String>,
    val strengthsHi: List<String>,
    val weaknessesEn: List<String>,
    val weaknessesBn: List<String>,
    val weaknessesHi: List<String>,
    val associatedTraitsEn: List<String>,
    val associatedTraitsBn: List<String>,
    val associatedTraitsHi: List<String>,
    val isNearCusp: Boolean = false
) {
    fun localizedPrimaryName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> englishName
        AppLanguage.BENGALI -> bengaliName
        AppLanguage.HINDI -> hindiName
    }

    fun localizedSecondaryName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "$bengaliName • $hindiName"
        AppLanguage.BENGALI -> "$englishName • $hindiName"
        AppLanguage.HINDI -> "$englishName • $bengaliName"
    }

    fun name(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "$englishName ($bengaliName / $hindiName)"
        AppLanguage.BENGALI -> "$bengaliName ($englishName)"
        AppLanguage.HINDI -> "$hindiName ($englishName)"
    }

    fun element(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> elementEn
        AppLanguage.BENGALI -> elementBn
        AppLanguage.HINDI -> elementHi
    }

    fun rulingPlanet(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> rulingPlanetEn
        AppLanguage.BENGALI -> rulingPlanetBn
        AppLanguage.HINDI -> rulingPlanetHi
    }

    fun dateRange(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> dateRangeEn
        AppLanguage.BENGALI -> dateRangeBn
        AppLanguage.HINDI -> dateRangeHi
    }

    fun modality(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> modalityEn
        AppLanguage.BENGALI -> modalityBn
        AppLanguage.HINDI -> modalityHi
    }

    fun luckyColor(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> luckyColorEn
        AppLanguage.BENGALI -> luckyColorBn
        AppLanguage.HINDI -> luckyColorHi
    }

    fun luckyDay(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> luckyDayEn
        AppLanguage.BENGALI -> luckyDayBn
        AppLanguage.HINDI -> luckyDayHi
    }

    fun description(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> descriptionEn
        AppLanguage.BENGALI -> descriptionBn
        AppLanguage.HINDI -> descriptionHi
    }

    fun strengths(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.ENGLISH -> strengthsEn
        AppLanguage.BENGALI -> strengthsBn
        AppLanguage.HINDI -> strengthsHi
    }

    fun weaknesses(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.ENGLISH -> weaknessesEn
        AppLanguage.BENGALI -> weaknessesBn
        AppLanguage.HINDI -> weaknessesHi
    }

    fun associatedTraits(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.ENGLISH -> associatedTraitsEn
        AppLanguage.BENGALI -> associatedTraitsBn
        AppLanguage.HINDI -> associatedTraitsHi
    }
}

data class TableRowItem(
    val key: String,
    val label: String,
    val value: String,
    val badgeText: String? = null,
    val isApproximate: Boolean = false
)

sealed class CalculationValidationResult {
    data class Success(val result: AgeCalculationResult) : CalculationValidationResult()
    data class FutureDateError(
        val messageEn: String,
        val messageBn: String,
        val messageHi: String
    ) : CalculationValidationResult() {
        fun message(lang: AppLanguage): String = when (lang) {
            AppLanguage.ENGLISH -> messageEn
            AppLanguage.BENGALI -> messageBn
            AppLanguage.HINDI -> messageHi
        }
    }

    data class InvalidDateError(
        val messageEn: String,
        val messageBn: String,
        val messageHi: String
    ) : CalculationValidationResult() {
        fun message(lang: AppLanguage): String = when (lang) {
            AppLanguage.ENGLISH -> messageEn
            AppLanguage.BENGALI -> messageBn
            AppLanguage.HINDI -> messageHi
        }
    }
}

data class AgeCalculationResult(
    val birthDate: LocalDate,
    val currentDate: LocalDate,
    val exactYears: Int,
    val exactMonths: Int,
    val exactDays: Int,
    val totalYears: Long,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val totalSeconds: Long,
    val approximateYearsDecimal: String,
    val completedBirthdays: Int,
    val birthWeekdayEn: String,
    val birthWeekdayBn: String,
    val birthWeekdayHi: String,
    val birthYear: Int,
    val birthMonthNameEn: String,
    val birthMonthNameBn: String,
    val birthMonthNameHi: String,
    val birthMonthNumber: Int,
    val birthQuarter: String,
    val birthDayOfYear: Int,
    val isBirthYearLeap: Boolean,
    val isCurrentYearLeap: Boolean,
    val daysInBirthMonth: Int,
    val daysInCurrentMonth: Int,
    val lastBirthday: LocalDate,
    val nextBirthday: LocalDate,
    val daysSinceLastBirthday: Long,
    val daysUntilNextBirthday: Long,
    val nextBirthdayRemainingMonths: Int,
    val nextBirthdayRemainingDays: Int,
    val nextBirthdayWeekdayEn: String,
    val nextBirthdayWeekdayBn: String,
    val nextBirthdayWeekdayHi: String,
    val ageOnNextBirthday: Int,
    val birthSeasonEn: String,
    val birthSeasonBn: String,
    val birthSeasonHi: String,
    val birthdayCycleProgress: Float,
    val isBirthdayToday: Boolean,
    val isFeb29Birth: Boolean,
    val zodiacProfile: ZodiacProfile
) {
    companion object {
        private val SHARED_DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    }

    fun formattedBirthDate(): String = birthDate.format(SHARED_DATE_FORMATTER)
    fun formattedCurrentDate(): String = currentDate.format(SHARED_DATE_FORMATTER)
    fun formattedLastBirthday(): String = lastBirthday.format(SHARED_DATE_FORMATTER)
    fun formattedNextBirthday(): String = nextBirthday.format(SHARED_DATE_FORMATTER)

    fun exactAgeSummary(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "$exactYears Years, $exactMonths Months, $exactDays Days"
        AppLanguage.BENGALI -> "$exactYears বছর, $exactMonths মাস, $exactDays দিন"
        AppLanguage.HINDI -> "$exactYears वर्ष, $exactMonths महीने, $exactDays दिन"
    }

    fun birthWeekday(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> birthWeekdayEn
        AppLanguage.BENGALI -> birthWeekdayBn
        AppLanguage.HINDI -> birthWeekdayHi
    }

    fun nextBirthdayWeekday(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> nextBirthdayWeekdayEn
        AppLanguage.BENGALI -> nextBirthdayWeekdayBn
        AppLanguage.HINDI -> nextBirthdayWeekdayHi
    }

    fun birthMonthName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> birthMonthNameEn
        AppLanguage.BENGALI -> birthMonthNameBn
        AppLanguage.HINDI -> birthMonthNameHi
    }

    fun birthSeason(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> birthSeasonEn
        AppLanguage.BENGALI -> birthSeasonBn
        AppLanguage.HINDI -> birthSeasonHi
    }

    fun feb29PolicyNote(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH ->
            "Leap-Year Policy: February 29 birthdays are evaluated on February 28 in non-leap years."
        AppLanguage.BENGALI ->
            "লিপ ইয়ার নোট: ২৯ ফেব্রুয়ারি জন্মদিন অধিবর্ষ ছাড়া অন্যান্য বছরে ২৮ ফেব্রুয়ারি হিসেবে গণনা করা হয়।"
        AppLanguage.HINDI ->
            "लीप वर्ष नियम: 29 फरवरी का जन्मदिन सामान्य (गैर-लीप) वर्षों में 28 फरवरी को मनाया और गिना जाता है।"
    }

    fun getBasicAgeTable(lang: AppLanguage): List<TableRowItem> {
        val exactBadge = when (lang) {
            AppLanguage.ENGLISH -> "Exact Calendar"
            AppLanguage.BENGALI -> "সঠিক ক্যালেন্ডার"
            AppLanguage.HINDI -> "सटीक कैलेंडर"
        }
        val approxBadge = when (lang) {
            AppLanguage.ENGLISH -> "Date-Midnight"
            AppLanguage.BENGALI -> "মধ্যরাত ভিত্তিক"
            AppLanguage.HINDI -> "मध्यरात्रि आधारित"
        }
        val yearsUnit = when (lang) {
            AppLanguage.ENGLISH -> "Years"
            AppLanguage.BENGALI -> "বছর"
            AppLanguage.HINDI -> "वर्ष"
        }
        val monthsUnit = when (lang) {
            AppLanguage.ENGLISH -> "Months"
            AppLanguage.BENGALI -> "মাস"
            AppLanguage.HINDI -> "महीने"
        }
        val weeksUnit = when (lang) {
            AppLanguage.ENGLISH -> "Weeks"
            AppLanguage.BENGALI -> "সপ্তাহ"
            AppLanguage.HINDI -> "सप्ताह"
        }
        val daysUnit = when (lang) {
            AppLanguage.ENGLISH -> "Days"
            AppLanguage.BENGALI -> "দিন"
            AppLanguage.HINDI -> "दिन"
        }
        val hoursUnit = when (lang) {
            AppLanguage.ENGLISH -> "Hours"
            AppLanguage.BENGALI -> "ঘণ্টা"
            AppLanguage.HINDI -> "घंटे"
        }
        val minutesUnit = when (lang) {
            AppLanguage.ENGLISH -> "Minutes"
            AppLanguage.BENGALI -> "মিনিট"
            AppLanguage.HINDI -> "मिनट"
        }
        val secondsUnit = when (lang) {
            AppLanguage.ENGLISH -> "Seconds"
            AppLanguage.BENGALI -> "সেকেন্ড"
            AppLanguage.HINDI -> "सेकंड"
        }

        return listOf(
            TableRowItem(
                key = "dob",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Date of Birth"
                    AppLanguage.BENGALI -> "জন্ম তারিখ"
                    AppLanguage.HINDI -> "जन्म तिथि"
                },
                value = formattedBirthDate()
            ),
            TableRowItem(
                key = "current_date",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Current Date"
                    AppLanguage.BENGALI -> "বর্তমান তারিখ"
                    AppLanguage.HINDI -> "वर्तमान तिथि"
                },
                value = formattedCurrentDate()
            ),
            TableRowItem(
                key = "exact_age",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Exact Age"
                    AppLanguage.BENGALI -> "সঠিক বয়স"
                    AppLanguage.HINDI -> "सटीक आयु"
                },
                value = exactAgeSummary(lang),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "total_years",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Years"
                    AppLanguage.BENGALI -> "মোট পূর্ণ বছর"
                    AppLanguage.HINDI -> "कुल वर्ष"
                },
                value = String.format(Locale.US, "%,d %s", totalYears, yearsUnit),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "total_months",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Months"
                    AppLanguage.BENGALI -> "মোট পূর্ণ মাস"
                    AppLanguage.HINDI -> "कुल महीने"
                },
                value = String.format(Locale.US, "%,d %s", totalMonths, monthsUnit),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "total_weeks",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Weeks"
                    AppLanguage.BENGALI -> "মোট পূর্ণ সপ্তাহ"
                    AppLanguage.HINDI -> "कुल सप्ताह"
                },
                value = String.format(Locale.US, "%,d %s", totalWeeks, weeksUnit),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "total_days",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Days"
                    AppLanguage.BENGALI -> "মোট দিন"
                    AppLanguage.HINDI -> "कुल दिन"
                },
                value = String.format(Locale.US, "%,d %s", totalDays, daysUnit),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "total_hours",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Hours"
                    AppLanguage.BENGALI -> "মোট ঘণ্টা"
                    AppLanguage.HINDI -> "कुल घंटे"
                },
                value = String.format(Locale.US, "%,d %s", totalHours, hoursUnit),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "total_minutes",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Minutes"
                    AppLanguage.BENGALI -> "মোট মিনিট"
                    AppLanguage.HINDI -> "कुल मिनट"
                },
                value = String.format(Locale.US, "%,d %s", totalMinutes, minutesUnit),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "total_seconds",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Seconds"
                    AppLanguage.BENGALI -> "মোট সেকেন্ড"
                    AppLanguage.HINDI -> "कुल सेकंड"
                },
                value = String.format(Locale.US, "%,d %s", totalSeconds, secondsUnit),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "birth_day",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Day"
                    AppLanguage.BENGALI -> "জন্মবার"
                    AppLanguage.HINDI -> "जन्म का दिन (वार)"
                },
                value = birthWeekday(lang)
            ),
            TableRowItem(
                key = "birth_year",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Year"
                    AppLanguage.BENGALI -> "জন্ম সাল"
                    AppLanguage.HINDI -> "जन्म वर्ष"
                },
                value = birthYear.toString()
            ),
            TableRowItem(
                key = "birth_month",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Month"
                    AppLanguage.BENGALI -> "জন্ম মাস"
                    AppLanguage.HINDI -> "जन्म महीना"
                },
                value = birthMonthName(lang)
            ),
            TableRowItem(
                key = "birth_quarter",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Quarter"
                    AppLanguage.BENGALI -> "জন্ম কোয়ার্টার"
                    AppLanguage.HINDI -> "जन्म तिमाही (Quarter)"
                },
                value = birthQuarter
            )
        )
    }

    fun getBirthdayTable(lang: AppLanguage): List<TableRowItem> {
        return listOf(
            TableRowItem(
                key = "last_birthday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Last Birthday"
                    AppLanguage.BENGALI -> "গত জন্মদিন"
                    AppLanguage.HINDI -> "पिछला जन्मदिन"
                },
                value = when (lang) {
                    AppLanguage.ENGLISH -> "${formattedLastBirthday()} ($daysSinceLastBirthday days ago)"
                    AppLanguage.BENGALI -> "${formattedLastBirthday()} ($daysSinceLastBirthday দিন আগে)"
                    AppLanguage.HINDI -> "${formattedLastBirthday()} ($daysSinceLastBirthday दिन पहले)"
                }
            ),
            TableRowItem(
                key = "next_birthday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Next Birthday"
                    AppLanguage.BENGALI -> "পরবর্তী জন্মদিন"
                    AppLanguage.HINDI -> "अगला जन्मदिन"
                },
                value = formattedNextBirthday()
            ),
            TableRowItem(
                key = "days_until_birthday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Days Until Birthday"
                    AppLanguage.BENGALI -> "জন্মদিনের বাকি দিন"
                    AppLanguage.HINDI -> "जन्मदिन में शेष दिन"
                },
                value = if (isBirthdayToday) {
                    when (lang) {
                        AppLanguage.ENGLISH -> "Today is your Birthday! (0 Days)"
                        AppLanguage.BENGALI -> "আজ শুভ জন্মদিন! (০ দিন)"
                        AppLanguage.HINDI -> "आज आपका जन्मदिन है! (0 दिन)"
                    }
                } else {
                    when (lang) {
                        AppLanguage.ENGLISH -> "$daysUntilNextBirthday Days ($nextBirthdayRemainingMonths Months, $nextBirthdayRemainingDays Days)"
                        AppLanguage.BENGALI -> "$daysUntilNextBirthday দিন ($nextBirthdayRemainingMonths মাস, $nextBirthdayRemainingDays দিন)"
                        AppLanguage.HINDI -> "$daysUntilNextBirthday दिन ($nextBirthdayRemainingMonths महीने, $nextBirthdayRemainingDays दिन)"
                    }
                }
            ),
            TableRowItem(
                key = "day_of_next_birthday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Day of Next Birthday"
                    AppLanguage.BENGALI -> "পরবর্তী জন্মদিনের বার"
                    AppLanguage.HINDI -> "अगले जन्मदिन का वार"
                },
                value = nextBirthdayWeekday(lang)
            ),
            TableRowItem(
                key = "birthdays_completed",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birthdays Completed"
                    AppLanguage.BENGALI -> "সম্পন্ন জন্মদিন সংখ্যা"
                    AppLanguage.HINDI -> "पूर्ण हुए जन्मदिन"
                },
                value = completedBirthdays.toString()
            ),
            TableRowItem(
                key = "age_on_next_birthday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Age on Next Birthday"
                    AppLanguage.BENGALI -> "পরবর্তী জন্মদিনে বয়স"
                    AppLanguage.HINDI -> "अगले जन्मदिन पर आयु"
                },
                value = when (lang) {
                    AppLanguage.ENGLISH -> "$ageOnNextBirthday Years"
                    AppLanguage.BENGALI -> "$ageOnNextBirthday বছর"
                    AppLanguage.HINDI -> "$ageOnNextBirthday वर्ष"
                }
            ),
            TableRowItem(
                key = "birthday_month",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birthday Month"
                    AppLanguage.BENGALI -> "জন্মদিনের মাস"
                    AppLanguage.HINDI -> "जन्मदिन का महीना"
                },
                value = birthMonthName(lang)
            ),
            TableRowItem(
                key = "birthday_season",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birthday Season"
                    AppLanguage.BENGALI -> "জন্ম ঋতু (উত্তর গোলার্ধ)"
                    AppLanguage.HINDI -> "जन्म ऋतु (उत्तरी गोलार्ध)"
                },
                value = birthSeason(lang)
            )
        )
    }

    fun getCalendarTable(lang: AppLanguage): List<TableRowItem> {
        val yesText = when (lang) {
            AppLanguage.ENGLISH -> "Yes (Leap Year)"
            AppLanguage.BENGALI -> "হ্যাঁ (লিপ ইয়ার)"
            AppLanguage.HINDI -> "हाँ (लीप वर्ष)"
        }
        val noText = when (lang) {
            AppLanguage.ENGLISH -> "No"
            AppLanguage.BENGALI -> "না"
            AppLanguage.HINDI -> "नहीं"
        }
        return listOf(
            TableRowItem(
                key = "birth_year_leap",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Year Leap Year"
                    AppLanguage.BENGALI -> "জন্ম সাল লিপ ইয়ার"
                    AppLanguage.HINDI -> "जन्म वर्ष लीप वर्ष है?"
                },
                value = if (isBirthYearLeap) yesText else noText
            ),
            TableRowItem(
                key = "current_year_leap",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Current Year Leap Year"
                    AppLanguage.BENGALI -> "বর্তমান সাল লিপ ইয়ার"
                    AppLanguage.HINDI -> "वर्तमान वर्ष लीप वर्ष है?"
                },
                value = if (isCurrentYearLeap) yesText else noText
            ),
            TableRowItem(
                key = "days_in_birth_month",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Days in Birth Month"
                    AppLanguage.BENGALI -> "জন্ম মাসের মোট দিন"
                    AppLanguage.HINDI -> "जन्म महीने में कुल दिन"
                },
                value = when (lang) {
                    AppLanguage.ENGLISH -> "$daysInBirthMonth Days"
                    AppLanguage.BENGALI -> "$daysInBirthMonth দিন"
                    AppLanguage.HINDI -> "$daysInBirthMonth दिन"
                }
            ),
            TableRowItem(
                key = "days_in_current_month",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Days in Current Month"
                    AppLanguage.BENGALI -> "বর্তমান মাসের মোট দিন"
                    AppLanguage.HINDI -> "वर्तमान महीने में कुल दिन"
                },
                value = when (lang) {
                    AppLanguage.ENGLISH -> "$daysInCurrentMonth Days"
                    AppLanguage.BENGALI -> "$daysInCurrentMonth দিন"
                    AppLanguage.HINDI -> "$daysInCurrentMonth दिन"
                }
            ),
            TableRowItem(
                key = "birth_month_number",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Month Number"
                    AppLanguage.BENGALI -> "জন্ম মাসের ক্রমিক নম্বর"
                    AppLanguage.HINDI -> "जन्म महीने की संख्या"
                },
                value = birthMonthNumber.toString()
            ),
            TableRowItem(
                key = "cal_birth_quarter",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Quarter"
                    AppLanguage.BENGALI -> "জন্ম কোয়ার্টার"
                    AppLanguage.HINDI -> "जन्म तिमाही"
                },
                value = birthQuarter
            ),
            TableRowItem(
                key = "cal_birth_weekday",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Weekday"
                    AppLanguage.BENGALI -> "জন্মবার"
                    AppLanguage.HINDI -> "जन्म सप्ताह का दिन"
                },
                value = birthWeekday(lang)
            ),
            TableRowItem(
                key = "birth_day_of_year",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Birth Day of Year"
                    AppLanguage.BENGALI -> "বছরের তম দিন"
                    AppLanguage.HINDI -> "वर्ष का दिन क्रमांक"
                },
                value = when (lang) {
                    AppLanguage.ENGLISH -> "Day $birthDayOfYear of ${if (isBirthYearLeap) 366 else 365}"
                    AppLanguage.BENGALI -> "$birthDayOfYear তম দিন"
                    AppLanguage.HINDI -> "दिन $birthDayOfYear / ${if (isBirthYearLeap) 366 else 365}"
                }
            )
        )
    }

    fun getTimeStatsTable(lang: AppLanguage): List<TableRowItem> {
        val exactBadge = when (lang) {
            AppLanguage.ENGLISH -> "Exact Calendar"
            AppLanguage.BENGALI -> "সঠিক ক্যালেন্ডার"
            AppLanguage.HINDI -> "सटीक कैलेंडर"
        }
        val approxBadge = when (lang) {
            AppLanguage.ENGLISH -> "Approximate Conversion"
            AppLanguage.BENGALI -> "আনুমানিক রূপান্তর"
            AppLanguage.HINDI -> "अनुमानित रूपांतरण"
        }
        return listOf(
            TableRowItem(
                key = "stat_days",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Days Lived"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত দিন"
                    AppLanguage.HINDI -> "कुल बीते हुए दिन"
                },
                value = String.format(Locale.US, "%,d", totalDays),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "stat_weeks",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Weeks Lived"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত সপ্তাহ"
                    AppLanguage.HINDI -> "कुल बीते हुए सप्ताह"
                },
                value = String.format(Locale.US, "%,d", totalWeeks),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "stat_months",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Months Lived"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত মাস"
                    AppLanguage.HINDI -> "कुल बीते हुए महीने"
                },
                value = String.format(Locale.US, "%,d", totalMonths),
                badgeText = exactBadge
            ),
            TableRowItem(
                key = "stat_hours",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Hours Lived (Days × 24)"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত ঘণ্টা (দিন × ২৪)"
                    AppLanguage.HINDI -> "कुल बीते हुए घंटे (दिन × 24)"
                },
                value = String.format(Locale.US, "%,d", totalHours),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "stat_minutes",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Minutes Lived (Days × 1,440)"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত মিনিট (দিন × ১,৪৪০)"
                    AppLanguage.HINDI -> "कुल बीते हुए मिनट (दिन × 1,440)"
                },
                value = String.format(Locale.US, "%,d", totalMinutes),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "stat_seconds",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Total Seconds Lived (Days × 86,400)"
                    AppLanguage.BENGALI -> "মোট অতিবাহিত সেকেন্ড (দিন × ৮৬,৪০০)"
                    AppLanguage.HINDI -> "कुल बीते हुए सेकंड (दिन × 86,400)"
                },
                value = String.format(Locale.US, "%,d", totalSeconds),
                badgeText = approxBadge,
                isApproximate = true
            ),
            TableRowItem(
                key = "stat_approx_years",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Approximate Years Lived (Days ÷ 365.2425)"
                    AppLanguage.BENGALI -> "আনুমানিক বছর (দিন ÷ ৩৬৫.২৪২৫)"
                    AppLanguage.HINDI -> "अनुमानित वर्ष (दिन ÷ 365.2425)"
                },
                value = approximateYearsDecimal,
                badgeText = approxBadge,
                isApproximate = true
            )
        )
    }

    fun formatFullReportText(lang: AppLanguage): String {
        val sb = StringBuilder()
        sb.appendLine("========================================")
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "SMART AGE & ZODIAC CALCULATOR REPORT"
                AppLanguage.BENGALI -> "স্মার্ট বয়স ও রাশিফল ক্যালকুলেটর রিপোর্ট"
                AppLanguage.HINDI -> "स्मार्ट आयु और राशिफल कैलकुलेटर रिपोर्ट"
            }
        )
        sb.appendLine("========================================")
        sb.appendLine()
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "[1] BASIC AGE INFORMATION"
                AppLanguage.BENGALI -> "[১] মৌলিক বয়স তথ্য"
                AppLanguage.HINDI -> "[1] मूल आयु जानकारी"
            }
        )
        getBasicAgeTable(lang).forEach { sb.appendLine("• ${it.label}: ${it.value}") }
        sb.appendLine()
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "[2] BIRTHDAY INFORMATION"
                AppLanguage.BENGALI -> "[২] জন্মদিনের তথ্য"
                AppLanguage.HINDI -> "[2] जन्मदिन की जानकारी"
            }
        )
        getBirthdayTable(lang).forEach { sb.appendLine("• ${it.label}: ${it.value}") }
        sb.appendLine()
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "[3] CALENDAR INFORMATION"
                AppLanguage.BENGALI -> "[৩] ক্যালেন্ডার তথ্য"
                AppLanguage.HINDI -> "[3] कैलेंडर जानकारी"
            }
        )
        getCalendarTable(lang).forEach { sb.appendLine("• ${it.label}: ${it.value}") }
        sb.appendLine()
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "[4] TIME STATISTICS"
                AppLanguage.BENGALI -> "[৪] সময় পরিসংখ্যান"
                AppLanguage.HINDI -> "[4] समय सांख्यिकी"
            }
        )
        getTimeStatsTable(lang).forEach { sb.appendLine("• ${it.label}: ${it.value}") }
        sb.appendLine()
        sb.appendLine(
            when (lang) {
                AppLanguage.ENGLISH -> "[5] WESTERN TROPICAL ZODIAC"
                AppLanguage.BENGALI -> "[৫] পাশ্চাত্য রাশিচক্র (Western Zodiac)"
                AppLanguage.HINDI -> "[5] पश्चिमी राशि चक्र (Western Zodiac)"
            }
        )
        sb.appendLine("• Zodiac: ${zodiacProfile.name(lang)} ${zodiacProfile.symbol}")
        sb.appendLine("• Element: ${zodiacProfile.element(lang)}")
        sb.appendLine("• Ruling Planet: ${zodiacProfile.rulingPlanet(lang)}")
        sb.appendLine("• Date Range: ${zodiacProfile.dateRange(lang)}")
        if (isFeb29Birth) {
            sb.appendLine()
            sb.appendLine("Note: ${feb29PolicyNote(lang)}")
        }
        return sb.toString().trim()
    }
}

data class SavedCalculationEntry(
    val id: String,
    val profileName: String,
    val relationshipTag: String,
    val birthDateIso: String,
    val calculatedAtMillis: Long,
    val isPinnedProfile: Boolean,
    val summaryAgeText: String,
    val zodiacNameEn: String,
    val zodiacSymbol: String
)

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val accentPalette: AccentPalette = AccentPalette.DEFAULT,
    val unlockedPalettes: Set<AccentPalette> = setOf(AccentPalette.DEFAULT),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val hapticFeedbackEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)
