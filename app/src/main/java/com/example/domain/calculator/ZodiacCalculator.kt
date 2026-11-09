package com.example.domain.calculator

import com.example.domain.model.AppLanguage
import com.example.domain.model.TableRowItem
import com.example.domain.model.ZodiacProfile
import com.example.domain.model.ZodiacSignId
import java.time.LocalDate
import java.time.Month
import kotlin.math.abs

/**
 * Western Tropical Zodiac Calculator using standard conventional date ranges.
 * Includes cusp/boundary detection and trilingual (English, Bangla, Hindi) astrological metadata.
 */
object ZodiacCalculator {

    val allSigns: List<ZodiacProfile> = listOf(
        ZodiacProfile(
            id = ZodiacSignId.ARIES,
            englishName = "Aries",
            bengaliName = "মেষ রাশি",
            hindiName = "मेष राशि",
            symbol = "♈",
            elementEn = "Fire",
            elementBn = "অগ্নি (Fire)",
            elementHi = "अग्नि (Fire)",
            rulingPlanetEn = "Mars",
            rulingPlanetBn = "মঙ্গল (Mars)",
            rulingPlanetHi = "मंगल (Mars)",
            dateRangeEn = "March 21 – April 19",
            dateRangeBn = "২১ মার্চ – ১৯ এপ্রিল",
            dateRangeHi = "21 मार्च – 19 अप्रैल",
            modalityEn = "Cardinal",
            modalityBn = "চর (Cardinal)",
            modalityHi = "चर (Cardinal)",
            luckyColorEn = "Crimson Red & Scarlet",
            luckyColorBn = "গাঢ় লাল ও সিঁদুর লাল",
            luckyColorHi = "गहरा लाल और सिंदूरी",
            luckyDayEn = "Tuesday",
            luckyDayBn = "মঙ্গলবার",
            luckyDayHi = "मंगलवार",
            descriptionEn = "Traditionally associated with pioneering initiative, bold enthusiasm, and direct action. Aries opens the tropical zodiac wheel with dynamic, forward-looking energy.",
            descriptionBn = "ঐতিহ্যগতভাবে সাহসিকতা, নেতৃত্বের উদ্যোগ এবং প্রাণবন্ত উদ্দীপনার সাথে সম্পর্কিত। মেষ রাশি পাশ্চাত্য রাশিচক্রের প্রথম রাশি যা গতিশীল শক্তির প্রতীক।",
            descriptionHi = "पारंपरिक रूप से साहस, नेतृत्व की पहल और ऊर्जावान उत्साह से जुड़ा हुआ है। मेष राशि पश्चिमी राशि चक्र की पहली राशि है जो गतिशील ऊर्जा का प्रतीक है।",
            strengthsEn = listOf("Courageous", "Determined", "Confident", "Enthusiastic", "Optimistic"),
            strengthsBn = listOf("সাহসী", "দৃঢ়প্রতিজ্ঞ", "আত্মবিশ্বাসী", "উদ্যমী", "আশাবাদী"),
            strengthsHi = listOf("साहसी", "दृढ़निश्चयी", "आत्मविश्वासी", "उत्साही", "आशावादी"),
            weaknessesEn = listOf("Impatient", "Impulsive", "Short-tempered"),
            weaknessesBn = listOf("অধৈর্য", "আবেগপ্রবণ সিদ্ধান্ত", "দ্রুত রাগান্বিত"),
            weaknessesHi = listOf("अधीर", "आवेगी निर्णय", "जल्दी क्रोधित होना"),
            associatedTraitsEn = listOf("Action-oriented", "Enterprising", "Direct communicator", "Competitive spirit"),
            associatedTraitsBn = listOf("কর্মমুখী", "উদ্যোগী", "স্পষ্টভাষী", "প্রতিযোগিতাপূর্ণ মনোভাব"),
            associatedTraitsHi = listOf("कर्मप्रधान", "उद्यमी", "स्पष्ट वक्ता", "प्रतिस्पर्धी भावना")
        ),
        ZodiacProfile(
            id = ZodiacSignId.TAURUS,
            englishName = "Taurus",
            bengaliName = "বৃষ রাশি",
            hindiName = "वृषभ राशि",
            symbol = "♉",
            elementEn = "Earth",
            elementBn = "পৃথিবী/মাটি (Earth)",
            elementHi = "पृथ्वी (Earth)",
            rulingPlanetEn = "Venus",
            rulingPlanetBn = "শুক্র (Venus)",
            rulingPlanetHi = "शुक्र (Venus)",
            dateRangeEn = "April 20 – May 20",
            dateRangeBn = "২০ এপ্রিল – ২০ মে",
            dateRangeHi = "20 अप्रैल – 20 मई",
            modalityEn = "Fixed",
            modalityBn = "স্থির (Fixed)",
            modalityHi = "स्थिर (Fixed)",
            luckyColorEn = "Emerald Green & Soft Pink",
            luckyColorBn = "পান্না সবুজ ও হালকা গোলাপী",
            luckyColorHi = "पन्ना हरा और हल्का गुलाबी",
            luckyDayEn = "Friday",
            luckyDayBn = "শুক্রবার",
            luckyDayHi = "शुक्रवार",
            descriptionEn = "Traditionally associated with grounded stability, perseverance, and an appreciation for beauty, comfort, and long-term craftsmanship.",
            descriptionBn = "ঐতিহ্যগতভাবে স্থিরতা, ধৈর্য, নির্ভরযোগ্যতা এবং সৌন্দর্যবোধের সাথে সম্পর্কিত। বৃষ রাশির মানুষেরা দীর্ঘমেয়াদী লক্ষ্যে অবিচল থাকতে পছন্দ করেন।",
            descriptionHi = "पारंपरिक रूप से स्थिरता, धैर्य, विश्वसनीयता और सौंदर्यबोध से जुड़ा हुआ है। वृषभ राशि के लोग दीर्घकालिक लक्ष्यों के प्रति समर्पित रहते हैं।",
            strengthsEn = listOf("Reliable", "Patient", "Practical", "Devoted", "Responsible"),
            strengthsBn = listOf("নির্ভরযোগ্য", "ধৈর্যশীল", "বাস্তববাদী", "নিষ্ঠাবান", "দায়িত্বশীল"),
            strengthsHi = listOf("विश्वसनीय", "धैर्यवान", "व्यावहारिक", "समर्पित", "ज़िम्मेदार"),
            weaknessesEn = listOf("Stubborn", "Possessive", "Resistant to sudden change"),
            weaknessesBn = listOf("একগুঁয়ে", "অধিকারপ্রবণ", "হঠাৎ পরিবর্তনে অনীহা"),
            weaknessesHi = listOf("ज़िद्दी", "अधिकार जताने वाला", "अचानक बदलाव से हिचक"),
            associatedTraitsEn = listOf("Steady builder", "Aesthetic appreciation", "Loyal companion", "Methodical planner"),
            associatedTraitsBn = listOf("স্থিতিশীল নির্মাতা", "নন্দনতত্ত্বের কদর", "বিশ্বস্ত সঙ্গী", "সুশৃঙ্খল পরিকল্পনাকারী"),
            associatedTraitsHi = listOf("स्थिर निर्माता", "कला प्रेमी", "वफादार साथी", "व्यवस्थित योजनाकार")
        ),
        ZodiacProfile(
            id = ZodiacSignId.GEMINI,
            englishName = "Gemini",
            bengaliName = "মিথুন রাশি",
            hindiName = "मिथुन राशि",
            symbol = "♊",
            elementEn = "Air",
            elementBn = "বায়ু (Air)",
            elementHi = "वायु (Air)",
            rulingPlanetEn = "Mercury",
            rulingPlanetBn = "বুধ (Mercury)",
            rulingPlanetHi = "बुध (Mercury)",
            dateRangeEn = "May 21 – June 20",
            dateRangeBn = "২১ মে – ২০ জুন",
            dateRangeHi = "21 मई – 20 जून",
            modalityEn = "Mutable",
            modalityBn = "দ্ব্যাত্মক (Mutable)",
            modalityHi = "द्विस्वभाव (Mutable)",
            luckyColorEn = "Vibrant Yellow & Sky Blue",
            luckyColorBn = "উজ্জ্বল হলুদ ও আকাশী নীল",
            luckyColorHi = "चमकीला पीला और आसमानी नीला",
            luckyDayEn = "Wednesday",
            luckyDayBn = "বুধবার",
            luckyDayHi = "बुधवार",
            descriptionEn = "Traditionally associated with intellectual curiosity, adaptability, expressive communication, and rapid synthesis of ideas.",
            descriptionBn = "ঐতিহ্যগতভাবে বুদ্ধিবৃত্তিক কৌতূহল, মানিয়ে নেওয়ার ক্ষমতা এবং চমৎকার যোগাযোগ দক্ষতার সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से बौद्धिक जिज्ञासा, अनुकूलनशीलता और प्रभावशाली संवाद कौशल से जुड़ा हुआ है।",
            strengthsEn = listOf("Adaptable", "Outgoing", "Curious", "Quick-witted", "Expressive"),
            strengthsBn = listOf("অভিযোজনযোগ্য", "মিশুক", "কৌতূহলী", "প্রত্যুৎপন্নমতি", "প্রকাশক্ষম"),
            strengthsHi = listOf("अनुकूलनशील", "मिलनसार", "जिज्ञासु", "हाज़िरजवाब", "अभिव्यक्तिपूर्ण"),
            weaknessesEn = listOf("Indecisive", "Restless", "Inconsistent focus"),
            weaknessesBn = listOf("দ্বিধাগ্রস্ত", "অস্থিরতা", "মনোযোগের পরিবর্তন"),
            weaknessesHi = listOf("अनिर्णय", "चंचलता", "ध्यान में अस्थिरता"),
            associatedTraitsEn = listOf("Versatile thinker", "Social connector", "Avid learner", "Articulate speaker"),
            associatedTraitsBn = listOf("বহুমুখী চিন্তাবিদ", "সামাজিক সংযোগকারী", "জ্ঞানপিপাসু", "বাগ্মী"),
            associatedTraitsHi = listOf("बहुमुखी विचारक", "सामाजिक संयोजक", "जिज्ञासु शिक्षार्थी", "कुशल वक्ता")
        ),
        ZodiacProfile(
            id = ZodiacSignId.CANCER,
            englishName = "Cancer",
            bengaliName = "কর্কট রাশি",
            hindiName = "कर्क राशि",
            symbol = "♋",
            elementEn = "Water",
            elementBn = "জল (Water)",
            elementHi = "जल (Water)",
            rulingPlanetEn = "Moon",
            rulingPlanetBn = "চন্দ্র (Moon)",
            rulingPlanetHi = "चंद्रमा (Moon)",
            dateRangeEn = "June 21 – July 22",
            dateRangeBn = "২১ জুন – ২২ জুলাই",
            dateRangeHi = "21 जून – 22 जुलाई",
            modalityEn = "Cardinal",
            modalityBn = "চর (Cardinal)",
            modalityHi = "चर (Cardinal)",
            luckyColorEn = "Silver White & Pearl",
            luckyColorBn = "রূপালী সাদা ও মুক্তা রং",
            luckyColorHi = "चांदी जैसा सफेद और मोती",
            luckyDayEn = "Monday",
            luckyDayBn = "সোমবার",
            luckyDayHi = "सोमवार",
            descriptionEn = "Traditionally associated with emotional depth, intuitive empathy, protective care for family and community, and strong memory.",
            descriptionBn = "ঐতিহ্যগতভাবে গভীর সংবেদনশীলতা, স্বজ্ঞাত সহমর্মিতা এবং পরিবার ও প্রিয়জনদের প্রতি যত্নশীল মনোভাবের সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से भावनात्मक गहराई, सहानुभूति और परिवार व प्रियजनों के प्रति सुरक्षात्मक देखभाल से जुड़ा हुआ है।",
            strengthsEn = listOf("Tenacious", "Empathetic", "Loyal", "Protective", "Intuitive"),
            strengthsBn = listOf("দৃঢ়চেতা", "সহমর্মী", "বিশ্বস্ত", "রক্ষণাবেক্ষণকারী", "স্বজ্ঞাত"),
            strengthsHi = listOf("दृढ़", "सहानुभूतिपूर्ण", "वफादार", "सुरक्षात्मक", "अंतर्ज्ञानी"),
            weaknessesEn = listOf("Moody", "Guarded", "Overly sensitive"),
            weaknessesBn = listOf("মেজাজের ওঠানামা", "রক্ষণশীল", "অতিরিক্ত সংবেদনশীল"),
            weaknessesHi = listOf("मूड में उतार-चढ़ाव", "संकोची", "अति संवेदनशील"),
            associatedTraitsEn = listOf("Nurturing presence", "Strong intuition", "Home-centered", "Deeply observant"),
            associatedTraitsBn = listOf("যত্নশীল ব্যক্তিত্ব", "প্রখর অন্তর্দৃষ্টি", "পরিবারকেন্দ্রিক", "গভীর পর্যবেক্ষক"),
            associatedTraitsHi = listOf("देखभाल करने वाला", "तीव्र अंतर्ज्ञान", "परिवार-केंद्रित", "गहरा पर्यवेक्षक")
        ),
        ZodiacProfile(
            id = ZodiacSignId.LEO,
            englishName = "Leo",
            bengaliName = "সিংহ রাশি",
            hindiName = "सिंह राशि",
            symbol = "♌",
            elementEn = "Fire",
            elementBn = "অগ্নি (Fire)",
            elementHi = "अग्नि (Fire)",
            rulingPlanetEn = "Sun",
            rulingPlanetBn = "সূর্য (Sun)",
            rulingPlanetHi = "सूर्य (Sun)",
            dateRangeEn = "July 23 – August 22",
            dateRangeBn = "২৩ জুলাই – ২২ আগস্ট",
            dateRangeHi = "23 जुलाई – 22 अगस्त",
            modalityEn = "Fixed",
            modalityBn = "স্থির (Fixed)",
            modalityHi = "स्थिर (Fixed)",
            luckyColorEn = "Radiant Gold & Amber",
            luckyColorBn = "উজ্জ্বল সোনালী ও কমলা",
            luckyColorHi = "चमकीला सुनहरा और केसरिया",
            luckyDayEn = "Sunday",
            luckyDayBn = "রবিবার",
            luckyDayHi = "रविवार",
            descriptionEn = "Traditionally associated with warmth, creative self-expression, generosity, and natural stage presence.",
            descriptionBn = "ঐতিহ্যগতভাবে উষ্ণ হৃদয়, সৃজনশীল আত্মপ্রকাশ, উদারতা এবং স্বাভাবিক নেতৃত্বগুণের সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से गर्मजोशी, रचनात्मक आत्म-अभिव्यक्ति, उदारता और स्वाभाविक नेतृत्व क्षमता से जुड़ा हुआ है।",
            strengthsEn = listOf("Creative", "Generous", "Warm-hearted", "Charismatic", "Dedicated"),
            strengthsBn = listOf("সৃজনশীল", "উদার", "উষ্ণ হৃদয়ের", "আকর্ষণীয় ব্যক্তিত্ব", "নিবেদিতপ্রাণ"),
            strengthsHi = listOf("रचनात्मक", "उदार", "सहृदय", "आकर्षक व्यक्तित्व", "समर्पित"),
            weaknessesEn = listOf("Prideful", "Inflexible", "Dramatic"),
            weaknessesBn = listOf("অহংবোধ", "অনমনীয়", "নাটকীয়তা প্রবণ"),
            weaknessesHi = listOf("स्वाभिमानी", "अड़िग", "नाटकीयता"),
            associatedTraitsEn = listOf("Natural leader", "Big-hearted host", "Creative visionary", "Protective ally"),
            associatedTraitsBn = listOf("স্বাভাবিক নেতা", "উদার আয়োজক", "সৃজনশীল স্বপ্নদ্রষ্টা", "বিশ্বস্ত মিত্র"),
            associatedTraitsHi = listOf("स्वाभाविक नेता", "उदार मेज़बान", "रचनात्मक दूरदर्शी", "विश्वस्त मित्र")
        ),
        ZodiacProfile(
            id = ZodiacSignId.VIRGO,
            englishName = "Virgo",
            bengaliName = "কন্যা রাশি",
            hindiName = "कन्या राशि",
            symbol = "♍",
            elementEn = "Earth",
            elementBn = "পৃথিবী/মাটি (Earth)",
            elementHi = "पृथ्वी (Earth)",
            rulingPlanetEn = "Mercury",
            rulingPlanetBn = "বুধ (Mercury)",
            rulingPlanetHi = "बुध (Mercury)",
            dateRangeEn = "August 23 – September 22",
            dateRangeBn = "২৩ আগস্ট – ২২ সেপ্টেম্বর",
            dateRangeHi = "23 अगस्त – 22 सितंबर",
            modalityEn = "Mutable",
            modalityBn = "দ্ব্যাত্মক (Mutable)",
            modalityHi = "द्विस्वभाव (Mutable)",
            luckyColorEn = "Forest Green & Warm Beige",
            luckyColorBn = "সবুজ ও হালকা বাদামী",
            luckyColorHi = "हरा और हल्का भूरा",
            luckyDayEn = "Wednesday",
            luckyDayBn = "বুধবার",
            luckyDayHi = "बुधवार",
            descriptionEn = "Traditionally associated with analytical precision, methodical organization, practical service, and attention to detail.",
            descriptionBn = "ঐতিহ্যগতভাবে বিশ্লেষণাত্মক দৃষ্টিভঙ্গি, সুশৃঙ্খল পরিকল্পনা, সূক্ষ্ম পর্যবেক্ষণ এবং বাস্তবসম্মত কর্মদক্ষতার সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से विश्लेषणात्मक सटीकता, व्यवस्थित संगठन और सूक्ष्म विवरणों पर ध्यान देने से जुड़ा हुआ है।",
            strengthsEn = listOf("Analytical", "Observant", "Diligent", "Practical", "Reliable"),
            strengthsBn = listOf("বিশ্লেষণধর্মী", "পর্যবেক্ষণশীল", "পরিশ্রমী", "বাস্তববাদী", "নির্ভরযোগ্য"),
            strengthsHi = listOf("विश्लेषणात्मक", "सूक्ष्मदर्शी", "परिश्रमी", "व्यावहारिक", "विश्वसनीय"),
            weaknessesEn = listOf("Overly critical", "Perfectionistic", "Prone to worry"),
            weaknessesBn = listOf("অতিরিক্ত সমালোচনাকারী", "নিখুঁত করার প্রবণতা", "উদ্বেগপ্রবণ"),
            weaknessesHi = listOf("अत्यधिक आलोचक", "पूर्णतावादी", "चिंता करने की प्रवृत्ति"),
            associatedTraitsEn = listOf("Detail-oriented", "Systematic thinker", "Helpful problem-solver", "Refined taste"),
            associatedTraitsBn = listOf("সূক্ষ্মদর্শী", "পদ্ধতিগত চিন্তাবিদ", "সমস্যা সমাধানকারী", "মার্জিত রুচি"),
            associatedTraitsHi = listOf("बारीकी पर ध्यान", "व्यवस्थित विचारक", "समस्या समाधानकर्ता", "परिष्कृत रुचि")
        ),
        ZodiacProfile(
            id = ZodiacSignId.LIBRA,
            englishName = "Libra",
            bengaliName = "তুলা রাশি",
            hindiName = "तुला राशि",
            symbol = "♎",
            elementEn = "Air",
            elementBn = "বায়ু (Air)",
            elementHi = "वायु (Air)",
            rulingPlanetEn = "Venus",
            rulingPlanetBn = "শুক্র (Venus)",
            rulingPlanetHi = "शुक्र (Venus)",
            dateRangeEn = "September 23 – October 22",
            dateRangeBn = "২৩ সেপ্টেম্বর – ২২ অক্টোবর",
            dateRangeHi = "23 सितंबर – 22 अक्टूबर",
            modalityEn = "Cardinal",
            modalityBn = "চর (Cardinal)",
            modalityHi = "चर (Cardinal)",
            luckyColorEn = "Pastel Blue & Rose Quartz",
            luckyColorBn = "হালকা নীল ও গোলাপী",
            luckyColorHi = "हल्का नीला और गुलाबी",
            luckyDayEn = "Friday",
            luckyDayBn = "শুক্রবার",
            luckyDayHi = "शुक्रवार",
            descriptionEn = "Traditionally associated with balance, diplomacy, fairness, aesthetic harmony, and cooperative partnerships.",
            descriptionBn = "ঐতিহ্যগতভাবে ভারসাম্য, কূটনীতি, ন্যায়পরায়ণতা, নান্দনিক সম্প্রীতি এবং অংশীদারিত্বের সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से संतुलन, कूटनीति, निष्पक्षता, सौंदर्य सामंजस्य और सहयोगात्मक भागीदारी से जुड़ा हुआ है।",
            strengthsEn = listOf("Diplomatic", "Fair-minded", "Social", "Cooperative", "Gracious"),
            strengthsBn = listOf("কূটনৈতিক", "ন্যায়পরায়ণ", "সামাজিক", "সহযোগিতাপূর্ণ", "মার্জিত"),
            strengthsHi = listOf("कूटनीतिक", "निष्पक्ष", "सामाजिक", "सहयोगी", "विनम्र"),
            weaknessesEn = listOf("Indecisive", "Avoids confrontation", "People-pleasing"),
            weaknessesBn = listOf("সিদ্ধান্তহীনতা", "বিরোধ এড়িয়ে চলা", "সবাইকে খুশি রাখার চেষ্টা"),
            weaknessesHi = listOf("अनिर्णय", "टकराव से बचना", "सबको खुश रखने की कोशिश"),
            associatedTraitsEn = listOf("Harmony seeker", "Objective mediator", "Artistic eye", "Tactful communicator"),
            associatedTraitsBn = listOf("সম্প্রীতিপ্রিয়", "নিরপেক্ষ মধ্যস্থতাকারী", "শিল্পমনা", "কৌশলী বক্তা"),
            associatedTraitsHi = listOf("सामंजस्य प्रेमी", "निष्पक्ष मध्यस्थ", "कलात्मक दृष्टि", "व्यवहारकुशल वक्ता")
        ),
        ZodiacProfile(
            id = ZodiacSignId.SCORPIO,
            englishName = "Scorpio",
            bengaliName = "বৃশ্চিক রাশি",
            hindiName = "वृश्चिक राशि",
            symbol = "♏",
            elementEn = "Water",
            elementBn = "জল (Water)",
            elementHi = "जल (Water)",
            rulingPlanetEn = "Mars (Traditional) / Pluto (Modern)",
            rulingPlanetBn = "মঙ্গল (প্রথাগত) / প্লুটো (আধুনিক)",
            rulingPlanetHi = "मंगल (पारंपरिक) / प्लूटो (आधुनिक)",
            dateRangeEn = "October 23 – November 21",
            dateRangeBn = "২৩ অক্টোবর – ২১ নভেম্বর",
            dateRangeHi = "23 अक्टूबर – 21 नवंबर",
            modalityEn = "Fixed",
            modalityBn = "স্থির (Fixed)",
            modalityHi = "स्थिर (Fixed)",
            luckyColorEn = "Deep Burgundy & Obsidian",
            luckyColorBn = "গাঢ় মেরুন ও কালো",
            luckyColorHi = "गहरा मैरून और काला",
            luckyDayEn = "Tuesday",
            luckyDayBn = "মঙ্গলবার",
            luckyDayHi = "मंगलवार",
            descriptionEn = "Traditionally associated with focused intensity, resourcefulness, investigative depth, and unwavering loyalty.",
            descriptionBn = "ঐতিহ্যগতভাবে গভীর একাগ্রতা, মানসিক দৃঢ়তা, অনুসন্ধিৎসু মনোভাব এবং অটুট বিশ্বস্ততার সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से गहरी एकाग्रता, मानसिक दृढ़ता, खोजी प्रवृत्ति और अटूट निष्ठा से जुड़ा हुआ है।",
            strengthsEn = listOf("Resourceful", "Brave", "Passionate", "Focused", "Loyal"),
            strengthsBn = listOf("উদ্ভাবনী", "সাহসী", "একনিষ্ঠ", "মনোযোগী", "বিশ্বস্ত"),
            strengthsHi = listOf("साधन-संपन्न", "साहसी", "समर्पित", "एकाग्र", "वफादार"),
            weaknessesEn = listOf("Secretive", "Suspicious", "Uncompromising"),
            weaknessesBn = listOf("গোপনীয়তাপ্রিয়", "সন্দেহপ্রবণ", "আপসহীন"),
            weaknessesHi = listOf("गोपनीयता प्रिय", "संदेहशील", "समझौता न करने वाला"),
            associatedTraitsEn = listOf("Deep researcher", "Resilient spirit", "Strategic mind", "Intense focus"),
            associatedTraitsBn = listOf("গভীর অনুসন্ধানকারী", "ঘুরে দাঁড়ানোর ক্ষমতা", "কৌশলী মন", "তীব্র একাগ্রতা"),
            associatedTraitsHi = listOf("गहरा शोधकर्ता", "जूझारू व्यक्तित्व", "रणनीतिक दिमाग", "तीव्र ध्यान")
        ),
        ZodiacProfile(
            id = ZodiacSignId.SAGITTARIUS,
            englishName = "Sagittarius",
            bengaliName = "ধনু রাশি",
            hindiName = "धनु राशि",
            symbol = "♐",
            elementEn = "Fire",
            elementBn = "অগ্নি (Fire)",
            elementHi = "अग्नि (Fire)",
            rulingPlanetEn = "Jupiter",
            rulingPlanetBn = "বৃহস্পতি (Jupiter)",
            rulingPlanetHi = "बृहस्पति / गुरु (Jupiter)",
            dateRangeEn = "November 22 – December 21",
            dateRangeBn = "২২ নভেম্বর – ২১ ডিসেম্বর",
            dateRangeHi = "22 नवंबर – 21 दिसंबर",
            modalityEn = "Mutable",
            modalityBn = "দ্ব্যাত্মক (Mutable)",
            modalityHi = "द्विस्वभाव (Mutable)",
            luckyColorEn = "Royal Purple & Indigo",
            luckyColorBn = "গাঢ় বেগুনি ও নীল",
            luckyColorHi = "शाही बैंगनी और नीला",
            luckyDayEn = "Thursday",
            luckyDayBn = "বৃহস্পতিবার",
            luckyDayHi = "गुरुवार",
            descriptionEn = "Traditionally associated with philosophical exploration, optimism, love of freedom, and broad-minded curiosity.",
            descriptionBn = "ঐতিহ্যগতভাবে দার্শনিক অনুসন্ধান, আশাবাদ, স্বাধীনচেতা মনোভাব এবং বিশ্বকে জানার আগ্রহের সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से दार्शनिक खोज, आशावाद, स्वतंत्रता-प्रेम और व्यापक जिज्ञासा से जुड़ा हुआ है।",
            strengthsEn = listOf("Generous", "Idealistic", "Good-humored", "Adventurous", "Honest"),
            strengthsBn = listOf("উদার", "আদর্শবাদী", "রসবোধসম্পন্ন", "অভিযানপ্রিয়", "সৎ"),
            strengthsHi = listOf("उदार", "आदर्शवादी", "हँसमुख", "साहसी यात्री", "ईमानदार"),
            weaknessesEn = listOf("Over-promising", "Impatient with routine", "Blunt"),
            weaknessesBn = listOf("অতিরিক্ত প্রতিশ্রুতি", "একঘেয়েমিতে অধৈর্য", "অপ্রিয় স্পষ্টবাদিতা"),
            weaknessesHi = listOf("अधिक वादा करना", "दिनचर्या से ऊब", "सीधा बोलना"),
            associatedTraitsEn = listOf("Lifelong explorer", "Big-picture thinker", "Optimistic outlook", "Candid speaker"),
            associatedTraitsBn = listOf("চিরন্তন অভিযাত্রী", "বৃহৎ দৃষ্টিভঙ্গি", "ইতিবাচক মানসিকতা", "খোলামেলা বক্তা"),
            associatedTraitsHi = listOf("आजीवन अन्वेषक", "बड़ी सोच वाला", "सकारात्मक दृष्टिकोण", "स्पष्ट वक्ता")
        ),
        ZodiacProfile(
            id = ZodiacSignId.CAPRICORN,
            englishName = "Capricorn",
            bengaliName = "মকর রাশি",
            hindiName = "मकर राशि",
            symbol = "♑",
            elementEn = "Earth",
            elementBn = "পৃথিবী/মাটি (Earth)",
            elementHi = "पृथ्वी (Earth)",
            rulingPlanetEn = "Saturn",
            rulingPlanetBn = "শনি (Saturn)",
            rulingPlanetHi = "शनि (Saturn)",
            dateRangeEn = "December 22 – January 19",
            dateRangeBn = "২২ ডিসেম্বর – ১৯ জানুয়ারি",
            dateRangeHi = "22 दिसंबर – 19 जनवरी",
            modalityEn = "Cardinal",
            modalityBn = "চর (Cardinal)",
            modalityHi = "चर (Cardinal)",
            luckyColorEn = "Charcoal Slate & Earth Brown",
            luckyColorBn = "ধূসর স্লেট ও মাটির বাদামী",
            luckyColorHi = "स्लेटी और मिट्टी भूरा",
            luckyDayEn = "Saturday",
            luckyDayBn = "শনিবার",
            luckyDayHi = "शनिवार",
            descriptionEn = "Traditionally associated with disciplined ambition, structural mastery, patience, and long-horizon responsibility.",
            descriptionBn = "ঐতিহ্যগতভাবে নিয়মানুবর্তিতা, উচ্চাকাঙ্ক্ষা, ধৈর্য এবং দীর্ঘমেয়াদী দায়িত্ববোধের সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से अनुशासित महत्वाकांक्षा, धैर्य, कार्यकुशलता और दीर्घकालिक ज़िम्मेदारी से जुड़ा हुआ है।",
            strengthsEn = listOf("Responsible", "Disciplined", "Self-controlled", "Prudent", "Persistent"),
            strengthsBn = listOf("দায়িত্বশীল", "শৃঙ্খলাবদ্ধ", "সংযমী", "বিচক্ষণ", "অধ্যবসায়ী"),
            strengthsHi = listOf("ज़िम्मेदार", "अनुशासित", "संयमी", "विवेकी", "लगनशील"),
            weaknessesEn = listOf("Overly reserved", "Pessimistic", "Work-obsessed"),
            weaknessesBn = listOf("অতিরিক্ত গম্ভীর", "নৈরাশ্যপ্রবণ", "অতিরিক্ত কর্মব্যস্ত"),
            weaknessesHi = listOf("अत्यधिक गंभीर", "निराशावादी झुकाव", "काम में अत्यधिक डूबा"),
            associatedTraitsEn = listOf("Strategic architect", "Goal-driven", "Calm under pressure", "Time-conscious"),
            associatedTraitsBn = listOf("কৌশলী নির্মাতা", "লক্ষ্যমুখী", "চাপের মুখে শান্ত", "সময়সচেতন"),
            associatedTraitsHi = listOf("रणनीतिक निर्माता", "लक्ष्य-केंद्रित", "दबाव में शांत", "समय का पाबंद")
        ),
        ZodiacProfile(
            id = ZodiacSignId.AQUARIUS,
            englishName = "Aquarius",
            bengaliName = "কুম্ভ রাশি",
            hindiName = "कुंभ राशि",
            symbol = "♒",
            elementEn = "Air",
            elementBn = "বায়ু (Air)",
            elementHi = "वायु (Air)",
            rulingPlanetEn = "Saturn (Traditional) / Uranus (Modern)",
            rulingPlanetBn = "শনি (প্রথাগত) / ইউরেনাস (আধুনিক)",
            rulingPlanetHi = "शनि (पारंपरिक) / अरुण (आधुनिक)",
            dateRangeEn = "January 20 – February 18",
            dateRangeBn = "২০ জানুয়ারি – ১৮ ফেব্রুয়ারি",
            dateRangeHi = "20 जनवरी – 18 फरवरी",
            modalityEn = "Fixed",
            modalityBn = "স্থির (Fixed)",
            modalityHi = "स्थिर (Fixed)",
            luckyColorEn = "Electric Cyan & Turquoise",
            luckyColorBn = "ফিরোজা ও উজ্জ্বল নীল",
            luckyColorHi = "फ़िरोज़ी और चमकीला नीला",
            luckyDayEn = "Saturday",
            luckyDayBn = "শনিবার",
            luckyDayHi = "शनिवार",
            descriptionEn = "Traditionally associated with independent thought, humanitarian ideals, inventive problem-solving, and forward-looking vision.",
            descriptionBn = "ঐতিহ্যগতভাবে স্বাধীন চিন্তাভাবনা, মানবিক আদর্শ, উদ্ভাবনী ক্ষমতা এবং ভবিষ্যৎমুখী দৃষ্টিভঙ্গির সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से स्वतंत्र विचार, मानवीय आदर्श, नवाचार और भविष्यवादी दृष्टिकोण से जुड़ा हुआ है।",
            strengthsEn = listOf("Progressive", "Original", "Independent", "Humanitarian", "Intellectual"),
            strengthsBn = listOf("প্রগতিশীল", "মৌলিক চিন্তাবিদ", "স্বাধীনচেতা", "মানবিক", "বুদ্ধিদীপ্ত"),
            strengthsHi = listOf("प्रगतिशील", "मौलिक विचारक", "स्वतंत्र", "मानवतावादी", "बौद्धिक"),
            weaknessesEn = listOf("Detached", "Aloof", "Unpredictable"),
            weaknessesBn = listOf("আবেগহীন ভাব", "দূরত্ব বজায় রাখা", "অপ্রত্যাশিত আচরণ"),
            weaknessesHi = listOf("भावनात्मक दूरी", "अलग-थलग रहना", "अप्रत्याशित"),
            associatedTraitsEn = listOf("Systems innovator", "Community-minded", "Independent thinker", "Visionary"),
            associatedTraitsBn = listOf("নতুনত্বের সন্ধানী", "সমাজসচেতন", "স্বতন্ত্র চিন্তাবিদ", "ভবিষ্যৎদ্রষ্টা"),
            associatedTraitsHi = listOf("नवाचारी", "समाज-जागरूक", "स्वतंत्र चिंतक", "दूरदर्शी")
        ),
        ZodiacProfile(
            id = ZodiacSignId.PISCES,
            englishName = "Pisces",
            bengaliName = "মীন রাশি",
            hindiName = "मीन राशि",
            symbol = "♓",
            elementEn = "Water",
            elementBn = "জল (Water)",
            elementHi = "जल (Water)",
            rulingPlanetEn = "Jupiter (Traditional) / Neptune (Modern)",
            rulingPlanetBn = "বৃহস্পতি (প্রথাগত) / নেপচুন (আধুনিক)",
            rulingPlanetHi = "बृहस्पति (पारंपरिक) / वरुण (आधुनिक)",
            dateRangeEn = "February 19 – March 20",
            dateRangeBn = "১৯ ফেব্রুয়ারি – ২০ মার্চ",
            dateRangeHi = "19 फरवरी – 20 मार्च",
            modalityEn = "Mutable",
            modalityBn = "দ্ব্যাত্মক (Mutable)",
            modalityHi = "द्विस्वभाव (Mutable)",
            luckyColorEn = "Seafoam Green & Lavender",
            luckyColorBn = "সমুদ্র সবুজ ও ল্যাভেন্ডার",
            luckyColorHi = "समुद्री हरा और लैवेंडर",
            luckyDayEn = "Thursday",
            luckyDayBn = "বৃহস্পতিবার",
            luckyDayHi = "गुरुवार",
            descriptionEn = "Traditionally associated with imaginative artistry, deep compassion, emotional receptivity, and poetic intuition.",
            descriptionBn = "ঐতিহ্যগতভাবে কল্পনাশক্তি, গভীর সহানুভূতি, শৈল্পিক মনন এবং প্রখর অন্তর্দৃষ্টির সাথে সম্পর্কিত।",
            descriptionHi = "पारंपरिक रूप से कल्पनाशीलता, गहरी करुणा, कलात्मकता और सहज अंतर्ज्ञान से जुड़ा हुआ है।",
            strengthsEn = listOf("Compassionate", "Artistic", "Intuitive", "Gentle", "Musical"),
            strengthsBn = listOf("সহানুভূতিশীল", "শিল্পমনা", "স্বজ্ঞাত", "নম্র", "সঙ্গীতপ্রিয়"),
            strengthsHi = listOf("करुणामय", "कलात्मक", "अंतर्ज्ञानी", "विनम्र", "संगीत प्रेमी"),
            weaknessesEn = listOf("Overly idealistic", "Escapist", "Easily overwhelmed"),
            weaknessesBn = listOf("অতিমাত্রায় আদর্শবাদী", "বাস্তবতা বিমুখতা", "দ্রুত আবেগাপ্লুত"),
            weaknessesHi = listOf("अत्यधिक आदर्शवादी", "यथार्थ से बचना", "जल्दी भावुक होना"),
            associatedTraitsEn = listOf("Creative dreamer", "Empathetic listener", "Soulful artist", "Adaptable spirit"),
            associatedTraitsBn = listOf("সৃজনশীল স্বপ্নদ্রষ্টা", "সহমর্মী শ্রোতা", "শৈল্পিক মন", "নমনীয় মানসিকতা"),
            associatedTraitsHi = listOf("रचनात्मक स्वप्नदृष्टा", "सहानुभूतिपूर्ण श्रोता", "भावुक कलाकार", "लचीला स्वभाव")
        )
    )

    private val signsById: Map<ZodiacSignId, ZodiacProfile> = allSigns.associateBy { it.id }

    fun getZodiacForDate(date: LocalDate): ZodiacProfile {
        val month = date.month
        val day = date.dayOfMonth

        val signId = when (month) {
            Month.JANUARY -> if (day <= 19) ZodiacSignId.CAPRICORN else ZodiacSignId.AQUARIUS
            Month.FEBRUARY -> if (day <= 18) ZodiacSignId.AQUARIUS else ZodiacSignId.PISCES
            Month.MARCH -> if (day <= 20) ZodiacSignId.PISCES else ZodiacSignId.ARIES
            Month.APRIL -> if (day <= 19) ZodiacSignId.ARIES else ZodiacSignId.TAURUS
            Month.MAY -> if (day <= 20) ZodiacSignId.TAURUS else ZodiacSignId.GEMINI
            Month.JUNE -> if (day <= 20) ZodiacSignId.GEMINI else ZodiacSignId.CANCER
            Month.JULY -> if (day <= 22) ZodiacSignId.CANCER else ZodiacSignId.LEO
            Month.AUGUST -> if (day <= 22) ZodiacSignId.LEO else ZodiacSignId.VIRGO
            Month.SEPTEMBER -> if (day <= 22) ZodiacSignId.VIRGO else ZodiacSignId.LIBRA
            Month.OCTOBER -> if (day <= 22) ZodiacSignId.LIBRA else ZodiacSignId.SCORPIO
            Month.NOVEMBER -> if (day <= 21) ZodiacSignId.SCORPIO else ZodiacSignId.SAGITTARIUS
            Month.DECEMBER -> if (day <= 21) ZodiacSignId.SAGITTARIUS else ZodiacSignId.CAPRICORN
        }

        val nearCusp = isDateNearBoundary(month, day)
        val baseProfile = signsById.getValue(signId)
        return if (nearCusp) baseProfile.copy(isNearCusp = true) else baseProfile
    }

    fun isDateNearBoundary(month: Month, day: Int): Boolean {
        val transitionDay = when (month) {
            Month.JANUARY -> 19
            Month.FEBRUARY -> 18
            Month.MARCH -> 20
            Month.APRIL -> 19
            Month.MAY -> 20
            Month.JUNE -> 20
            Month.JULY -> 22
            Month.AUGUST -> 22
            Month.SEPTEMBER -> 22
            Month.OCTOBER -> 22
            Month.NOVEMBER -> 21
            Month.DECEMBER -> 21
        }
        return abs(day - transitionDay) <= 1 || day == transitionDay + 1
    }

    fun getZodiacDetailsTable(profile: ZodiacProfile, lang: AppLanguage): List<TableRowItem> {
        return listOf(
            TableRowItem(
                key = "zodiac_name",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Zodiac Name (English)"
                    AppLanguage.BENGALI -> "রাশির নাম (English)"
                    AppLanguage.HINDI -> "राशि का नाम (English)"
                },
                value = profile.englishName
            ),
            TableRowItem(
                key = "zodiac_bn_name",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Bangla & Hindi Name"
                    AppLanguage.BENGALI -> "বাংলা ও হিন্দি নাম"
                    AppLanguage.HINDI -> "हिंदी और बांग्ला नाम"
                },
                value = when (lang) {
                    AppLanguage.HINDI -> "${profile.hindiName} (${profile.bengaliName})"
                    else -> "${profile.bengaliName} (${profile.hindiName})"
                }
            ),
            TableRowItem(
                key = "zodiac_symbol",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Zodiac Symbol"
                    AppLanguage.BENGALI -> "রাশি প্রতীক"
                    AppLanguage.HINDI -> "राशि चिन्ह"
                },
                value = profile.symbol
            ),
            TableRowItem(
                key = "zodiac_element",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Element"
                    AppLanguage.BENGALI -> "উপাদান (Element)"
                    AppLanguage.HINDI -> "तत्व (Element)"
                },
                value = profile.element(lang)
            ),
            TableRowItem(
                key = "ruling_planet",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Ruling Planet"
                    AppLanguage.BENGALI -> "শাসক গ্রহ"
                    AppLanguage.HINDI -> "स्वामी ग्रह"
                },
                value = profile.rulingPlanet(lang)
            ),
            TableRowItem(
                key = "date_range",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Date Range"
                    AppLanguage.BENGALI -> "প্রচলিত তারিখ সীমা"
                    AppLanguage.HINDI -> "तिथि सीमा"
                },
                value = profile.dateRange(lang)
            ),
            TableRowItem(
                key = "modality",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Modality"
                    AppLanguage.BENGALI -> "প্রকৃতি (Modality)"
                    AppLanguage.HINDI -> "प्रकृति (Modality)"
                },
                value = profile.modality(lang)
            ),
            TableRowItem(
                key = "lucky_color",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Lucky Color"
                    AppLanguage.BENGALI -> "শুভ রং (প্রথাগত)"
                    AppLanguage.HINDI -> "शुभ रंग (पारंपरिक)"
                },
                value = profile.luckyColor(lang)
            ),
            TableRowItem(
                key = "lucky_day",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Lucky Day"
                    AppLanguage.BENGALI -> "শুভ বার (প্রথাগত)"
                    AppLanguage.HINDI -> "शुभ दिन (पारंपरिक)"
                },
                value = profile.luckyDay(lang)
            ),
            TableRowItem(
                key = "strengths",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Strengths"
                    AppLanguage.BENGALI -> "ইতিবাচক দিক"
                    AppLanguage.HINDI -> "मजबूत पक्ष (Strengths)"
                },
                value = profile.strengths(lang).joinToString(", ")
            ),
            TableRowItem(
                key = "weaknesses",
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Weaknesses"
                    AppLanguage.BENGALI -> "দুর্বল দিক"
                    AppLanguage.HINDI -> "कमजोर पक्ष (Weaknesses)"
                },
                value = profile.weaknesses(lang).joinToString(", ")
            )
        )
    }

    fun getBoundaryDisclaimer(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH ->
            "Boundary Date Note: This birthday falls near a conventional zodiac transition boundary. Exact astrological sign placement may depend on the exact birth time, year, and geographic location."
        AppLanguage.BENGALI ->
            "সীমান্তবর্তী তারিখের নোট: আপনার জন্মতারিখটি দুটি রাশির সন্ধিক্ষণের কাছাকাছি। সূক্ষ্ম জ্যোতিষশাস্ত্রীয় গণনার জন্য জন্মের সঠিক সময়, বছর এবং স্থানের প্রয়োজন হতে পারে।"
        AppLanguage.HINDI ->
            "सीमावर्ती तिथि नोट: यह जन्मतिथि दो राशियों के संधिकाल के पास है। सटीक ज्योतिषीय गणना के लिए जन्म के सटीक समय, वर्ष और स्थान की आवश्यकता हो सकती है।"
    }

    fun getScientificDisclaimer(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH ->
            "Note: These are conventional Western Tropical Zodiac date ranges. Personality descriptions and lucky attributes represent traditional astrological associations, not scientifically established personality assessments."
        AppLanguage.BENGALI ->
            "বিশেষ দ্রষ্টব্য: রাশিচক্রের তারিখ সীমাগুলো প্রচলিত পাশ্চাত্য ট্রপিক্যাল জ্যোতিষশাস্ত্র অনুসারে প্রদত্ত এবং ব্যক্তিত্বের বিবরণগুলো ঐতিহ্যবাহী জ্যোতিষীয় ধারণা—এগুলো বৈজ্ঞানিকভাবে প্রমাণিত মনস্তাত্ত্বিক মূল্যায়ন নয়।"
        AppLanguage.HINDI ->
            "विशेष नोट: ये पारंपरिक पश्चिमी ट्रॉपिकल राशि चक्र की तिथि सीमाएँ हैं। व्यक्तित्व विवरण और शुभ विशेषताएँ पारंपरिक ज्योतिषीय मान्यताओं पर आधारित हैं, न कि वैज्ञानिक रूप से स्थापित मनोवैज्ञानिक मूल्यांकन पर।"
    }
}
