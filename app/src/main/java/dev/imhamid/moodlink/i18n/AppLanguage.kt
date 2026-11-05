package dev.imhamid.moodlink.i18n

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

/**
 * The two languages the app ships in. The choice is stored in SharedPreferences and
 * applied by wrapping the base context, so Compose, the widgets and the layout
 * direction (LTR for English, RTL for Persian) all follow the same setting.
 */
enum class Lang(val tag: String, val rtl: Boolean) {
    EN("en", false),
    FA("fa", true);

    companion object {
        fun fromTag(tag: String?): Lang? = entries.firstOrNull { it.tag == tag }
    }
}

object AppLanguage {
    private const val PREFS = "moodlink_prefs"
    private const val KEY = "language"

    /** The language the person picked, or the device language when they have not picked one. */
    fun current(context: Context): Lang {
        val saved = Lang.fromTag(
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null),
        )
        if (saved != null) return saved
        val system = context.resources.configuration.locales[0].language
        return Lang.fromTag(system) ?: Lang.EN
    }

    fun save(context: Context, lang: Lang) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, lang.tag).apply()
    }

    /** Returns a context whose resources and layout direction match [current]. */
    fun wrap(base: Context): Context {
        val locale = Locale.forLanguageTag(current(base).tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return base.createConfigurationContext(config)
    }

    /** Saves [lang] and rebuilds the activity so every screen picks it up. */
    fun switchTo(activity: Activity, lang: Lang) {
        if (current(activity) == lang) return
        save(activity, lang)
        activity.recreate()
    }
}

/** Walks the wrapper chain; Compose's LocalContext is not always the Activity itself. */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
