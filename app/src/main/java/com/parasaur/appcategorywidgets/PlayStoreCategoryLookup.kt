package com.parasaur.appcategorywidgets

import android.content.Context
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

object PlayStoreCategoryLookup {
 fun lookup(c:Context,pkg:String):String? {
  CategoryStore.cachedPlayCategory(c,pkg)?.let{return it}
  var conn:HttpURLConnection?=null
  return try {
   conn=(URL("https://play.google.com/store/apps/details?id=$pkg&hl=en_US").openConnection() as HttpURLConnection).apply{
    connectTimeout=10000;readTimeout=10000;setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36")}
   if(conn.responseCode !in 200..299)return null
   val doc=Jsoup.parse(conn.inputStream.bufferedReader().use{it.readText()})
   val raw=doc.selectFirst("meta[itemprop=genre]")?.attr("content")?.takeIf{it.isNotBlank()}?:doc.selectFirst("[itemprop=genre]")?.text()
   val cat=CategoryStore.builtIn.firstOrNull{it.equals(raw?.trim(),true)}?:when(raw?.trim()?.lowercase()){
    "video players","video players & editors"->"Video Players & Editors";"auto and vehicles"->"Auto & Vehicles"
    "food and drink"->"Food & Drink";"books and reference"->"Books & Reference";"art and design"->"Art & Design"
    "news and magazines"->"News & Magazines";"music and audio"->"Music & Audio";"health and fitness"->"Health & Fitness"
    "house and home"->"House & Home";"maps and navigation"->"Maps & Navigation";"travel and local"->"Travel & Local";else->null}
   if(cat!=null)CategoryStore.savePlayCategory(c,pkg,cat)
   cat
  }catch(_:Exception){null}finally{conn?.disconnect()}
 }
}
