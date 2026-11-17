package com.parasaur.appcategorywidgets

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object CategoryStore {
    private const val PREFS = "category_store"
    private const val CUSTOM = "custom_categories"
    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    val builtIn = listOf("Art & Design","Auto & Vehicles","Beauty","Books & Reference","Business","Comics","Communication","Dating","Education","Entertainment","Events","Finance","Food & Drink","Games","Health & Fitness","House & Home","Libraries & Demo","Lifestyle","Maps & Navigation","Medical","Music & Audio","News & Magazines","Parenting","Personalization","Photography","Productivity","Shopping","Social","Sports","Tools","Travel & Local","Video Players","Weather","Other")
    fun categories(c: Context) = (builtIn + prefs(c).getStringSet(CUSTOM, emptySet()).orEmpty()).distinct().sorted()
    fun addCategory(c: Context, name: String) {
        val set = prefs(c).getStringSet(CUSTOM, emptySet()).orEmpty().toMutableSet()
        if (name.trim().isNotEmpty()) set.add(name.trim())
        prefs(c).edit().putStringSet(CUSTOM, set).apply()
    }
    fun assigned(c: Context, pkg: String) = prefs(c).getString("app:" + pkg, null)
    fun assign(c: Context, pkg: String, category: String) { prefs(c).edit().putString("app:" + pkg, category).apply() }
    fun autoCategory(info: ApplicationInfo): String = when (info.category) {
        ApplicationInfo.CATEGORY_GAME -> "Games"
        ApplicationInfo.CATEGORY_AUDIO -> "Music & Audio"
        ApplicationInfo.CATEGORY_VIDEO -> "Video Players"
        ApplicationInfo.CATEGORY_IMAGE -> "Photography"
        ApplicationInfo.CATEGORY_SOCIAL -> "Social"
        ApplicationInfo.CATEGORY_NEWS -> "News & Magazines"
        ApplicationInfo.CATEGORY_MAPS -> "Maps & Navigation"
        ApplicationInfo.CATEGORY_PRODUCTIVITY -> "Productivity"
        else -> "Other"
    }
    fun categoryFor(c: Context, info: ApplicationInfo) = assigned(c, info.packageName) ?: autoCategory(info)
    fun apps(c: Context): List<ApplicationInfo> {
        val pm = c.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { pm.getApplicationLabel(it).toString().lowercase() }
    }
}
