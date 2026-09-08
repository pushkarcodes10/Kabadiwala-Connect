package com.kabadiwalaconnect.language

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kabadiwalaconnect.KabadiwalaApplication
import java.util.Locale

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val locale: Locale,
    val region: String = "India"
) {
    val displayName: String
        get() = "$nativeName ($name)"
}

class LanguageManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val LANGUAGE_KEY = "selected_language_code"
    
    private val _currentLanguage = MutableLiveData<Language>()
    val currentLanguage: LiveData<Language> = _currentLanguage
    
    companion object {
        @Volatile private var INSTANCE: LanguageManager? = null
        fun getInstance(context: Context): LanguageManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LanguageManager(context.applicationContext).also { INSTANCE = it }
        }
    }
    
    init {
        val savedCode = prefs.getString(LANGUAGE_KEY, "en")
        val initialLang = SUPPORTED_LANGUAGES.find { it.code == savedCode } ?: ENGLISH
        _currentLanguage.value = initialLang
        applyLocale(context, initialLang.locale)
    }
    
    fun setLanguage(language: Language) {
        if (_currentLanguage.value?.code == language.code) return
        
        prefs.edit().putString(LANGUAGE_KEY, language.code).apply()
        _currentLanguage.value = language
        applyLocale(KabadiwalaApplication.getInstance(), language.locale)
    }
    
    fun getCurrentLanguage(): Language = _currentLanguage.value ?: ENGLISH
    
    fun applyLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = Configuration(resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                KabadiwalaApplication.getInstance().createConfigurationContext(config)
            } catch (_: Exception) {}
        }
    }
}

// 28 Indian Languages: 22 Scheduled Official Languages + English, English (India), Bhojpuri, Rajasthani, Chhattisgarhi, Haryanvi
val ENGLISH = Language(
    code = "en",
    name = "English",
    nativeName = "English",
    locale = Locale("en", "US"),
    region = "All-India / Global"
)

val ENGLISH_INDIA = Language(
    code = "en_IN",
    name = "English (India)",
    nativeName = "English (India)",
    locale = Locale("en", "IN"),
    region = "Pan-India"
)

val HINDI = Language(
    code = "hi",
    name = "Hindi",
    nativeName = "हिन्दी",
    locale = Locale("hi", "IN"),
    region = "North & Central India"
)

val BENGALI = Language(
    code = "bn",
    name = "Bengali",
    nativeName = "বাংলা",
    locale = Locale("bn", "IN"),
    region = "West Bengal, Tripura, Assam"
)

val TELUGU = Language(
    code = "te",
    name = "Telugu",
    nativeName = "తెలుగు",
    locale = Locale("te", "IN"),
    region = "Andhra Pradesh & Telangana"
)

val MARATHI = Language(
    code = "mr",
    name = "Marathi",
    nativeName = "मराठी",
    locale = Locale("mr", "IN"),
    region = "Maharashtra & Goa"
)

val TAMIL = Language(
    code = "ta",
    name = "Tamil",
    nativeName = "தமிழ்",
    locale = Locale("ta", "IN"),
    region = "Tamil Nadu & Puducherry"
)

val GUJARATI = Language(
    code = "gu",
    name = "Gujarati",
    nativeName = "ગુજરાતી",
    locale = Locale("gu", "IN"),
    region = "Gujarat, Daman & Diu"
)

val KANNADA = Language(
    code = "kn",
    name = "Kannada",
    nativeName = "ಕನ್ನಡ",
    locale = Locale("kn", "IN"),
    region = "Karnataka"
)

val MALAYALAM = Language(
    code = "ml",
    name = "Malayalam",
    nativeName = "മലയാളം",
    locale = Locale("ml", "IN"),
    region = "Kerala & Lakshadweep"
)

val PUNJABI = Language(
    code = "pa",
    name = "Punjabi",
    nativeName = "ਪੰਜਾਬੀ",
    locale = Locale("pa", "IN"),
    region = "Punjab, Chandigarh & Delhi"
)

val ODIA = Language(
    code = "or",
    name = "Odia",
    nativeName = "ଓଡ଼ିଆ",
    locale = Locale("or", "IN"),
    region = "Odisha"
)

val ASSAMESE = Language(
    code = "as",
    name = "Assamese",
    nativeName = "অসমীয়া",
    locale = Locale("as", "IN"),
    region = "Assam"
)

val URDU = Language(
    code = "ur",
    name = "Urdu",
    nativeName = "اردو",
    locale = Locale("ur", "IN"),
    region = "J&K, Telangana, Delhi, UP"
)

val BHOJPURI = Language(
    code = "bho",
    name = "Bhojpuri",
    nativeName = "भोजपुरी",
    locale = Locale("bho", "IN"),
    region = "Bihar, UP & Jharkhand"
)

val RAJASTHANI = Language(
    code = "mwr",
    name = "Rajasthani / Marwari",
    nativeName = "राजस्थानी",
    locale = Locale("mwr", "IN"),
    region = "Rajasthan"
)

val CHHATTISGARHI = Language(
    code = "hne",
    name = "Chhattisgarhi",
    nativeName = "छत्तीसगढ़ी",
    locale = Locale("hne", "IN"),
    region = "Chhattisgarh"
)

val HARYANVI = Language(
    code = "bgc",
    name = "Haryanvi",
    nativeName = "हरियाणवी",
    locale = Locale("bgc", "IN"),
    region = "Haryana & Delhi NCR"
)

val MAITHILI = Language(
    code = "mai",
    name = "Maithili",
    nativeName = "मैथिली",
    locale = Locale("mai", "IN"),
    region = "Bihar & Jharkhand"
)

val SANSKRIT = Language(
    code = "sa",
    name = "Sanskrit",
    nativeName = "संस्कृतम्",
    locale = Locale("sa", "IN"),
    region = "Ancient / Pan-India"
)

val NEPALI = Language(
    code = "ne",
    name = "Nepali",
    nativeName = "नेपाली",
    locale = Locale("ne", "NP"),
    region = "Sikkim, North Bengal & Assam"
)

val KONKANI = Language(
    code = "kok",
    name = "Konkani",
    nativeName = "कोंकणी",
    locale = Locale("kok", "IN"),
    region = "Goa, Konkan & coastal Karnataka"
)

val SINDHI = Language(
    code = "sd",
    name = "Sindhi",
    nativeName = "سنڌي",
    locale = Locale("sd", "IN"),
    region = "Gujarat, Maharashtra, Rajasthan"
)

val KASHMIRI = Language(
    code = "ks",
    name = "Kashmiri",
    nativeName = "کٲشُر",
    locale = Locale("ks", "IN"),
    region = "Jammu & Kashmir"
)

val DOGRI = Language(
    code = "doi",
    name = "Dogri",
    nativeName = "डोगरी",
    locale = Locale("doi", "IN"),
    region = "Jammu & Kashmir & Himachal"
)

val MANIPURI = Language(
    code = "mni",
    name = "Manipuri / Meitei",
    nativeName = "মৈতৈলোন্",
    locale = Locale("mni", "IN"),
    region = "Manipur & Northeast"
)

val BODO = Language(
    code = "brx",
    name = "Bodo",
    nativeName = "बड़ो",
    locale = Locale("brx", "IN"),
    region = "Bodoland & Assam"
)

val SANTALI = Language(
    code = "sat",
    name = "Santali",
    nativeName = "ᱥᱟᱱᱛᱟᱲᱤ",
    locale = Locale("sat", "IN"),
    region = "Jharkhand, Odisha & West Bengal"
)

val SUPPORTED_LANGUAGES: List<Language> = listOf(
    ENGLISH,
    ENGLISH_INDIA,
    HINDI,
    BENGALI,
    TELUGU,
    MARATHI,
    TAMIL,
    GUJARATI,
    KANNADA,
    MALAYALAM,
    PUNJABI,
    ODIA,
    ASSAMESE,
    URDU,
    BHOJPURI,
    RAJASTHANI,
    CHHATTISGARHI,
    HARYANVI,
    MAITHILI,
    SANSKRIT,
    NEPALI,
    KONKANI,
    SINDHI,
    KASHMIRI,
    DOGRI,
    MANIPURI,
    BODO,
    SANTALI
)