package com.parasaur.appcategorywidgets

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object CategoryStore {
 private const val PREFS="category_store"
 const val UNCATEGORIZED="Uncategorized"
 private fun prefs(c:Context)=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE)
 val builtIn=listOf("Art & Design","Auto & Vehicles","Beauty","Books & Reference","Business","Comics","Communication","Dating","Education","Entertainment","Events","Finance","Food & Drink","Games","Health & Fitness","House & Home","Libraries & Demo","Lifestyle","Maps & Navigation","Medical","Music & Audio","News & Magazines","Parenting","Personalization","Photography","Productivity","Shopping","Social","Sports","Tools","Travel & Local","Video Players & Editors","Weather","Other")
 fun categories(c:Context)=(builtIn+prefs(c).getStringSet("custom_categories",emptySet()).orEmpty()).distinct().sorted()
 fun addCategory(c:Context,name:String){val n=name.trim();if(n.isNotEmpty()&&n!=UNCATEGORIZED){val s=prefs(c).getStringSet("custom_categories",emptySet()).orEmpty().toMutableSet();s.add(n);prefs(c).edit().putStringSet("custom_categories",s).apply()}}
 fun assigned(c:Context,pkg:String)=prefs(c).getString("app:$pkg",null)
 fun clearCategory(c:Context,pkg:String){prefs(c).edit().remove("app:$pkg").remove("play:$pkg").apply()}
 fun assign(c:Context,pkg:String,cat:String){prefs(c).edit().putString("app:$pkg",cat).apply()}
 fun cachedPlayCategory(c:Context,pkg:String)=prefs(c).getString("play:$pkg",null)
 fun savePlayCategory(c:Context,pkg:String,cat:String){prefs(c).edit().putString("play:$pkg",cat).apply()}
 fun isUncategorized(c:Context,pkg:String)=assigned(c,pkg)==null&&cachedPlayCategory(c,pkg)==null
 fun autoCategory(i:ApplicationInfo)=when(i.category){
  ApplicationInfo.CATEGORY_GAME->"Games";ApplicationInfo.CATEGORY_AUDIO->"Music & Audio";ApplicationInfo.CATEGORY_VIDEO->"Video Players & Editors"
  ApplicationInfo.CATEGORY_IMAGE->"Photography";ApplicationInfo.CATEGORY_SOCIAL->"Social";ApplicationInfo.CATEGORY_NEWS->"News & Magazines"
  ApplicationInfo.CATEGORY_MAPS->"Maps & Navigation";ApplicationInfo.CATEGORY_PRODUCTIVITY->"Productivity";else->"Other"}
 fun categoryFor(c:Context,i:ApplicationInfo)=assigned(c,i.packageName)?:cachedPlayCategory(c,i.packageName)?:autoCategory(i)
 @Suppress("DEPRECATION")
 fun apps(c:Context):List<ApplicationInfo>{
  val p=c.packageManager
  val launcher=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  // The launcher query is the authoritative source for apps with a home-screen
  // launch activity. Keep its ApplicationInfo directly instead of querying all
  // installed packages a second time and accidentally dropping valid results.
  return p.queryIntentActivities(launcher,PackageManager.MATCH_DEFAULT_ONLY)
   .asSequence()
   .filter{it.activityInfo.enabled && it.activityInfo.applicationInfo.enabled}
   .map{it.activityInfo.applicationInfo}
   .distinctBy{it.packageName}
   .sortedBy{p.getApplicationLabel(it).toString().lowercase()}
   .toList()
 }
}
