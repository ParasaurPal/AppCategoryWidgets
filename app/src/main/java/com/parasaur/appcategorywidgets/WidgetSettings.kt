package com.parasaur.appcategorywidgets

import android.content.Context

data class WidgetSettings(
 val category:String="All apps", val layout:String="List", val sort:String="A–Z",
 val background:Int=0xFF17151E.toInt(), val opacity:Int=80, val textColor:Int=0xFFF2EAF8.toInt(),
 val textSize:Int=14, val iconSize:Int=40, val columns:Int=3, val showIcons:Boolean=true, val showTitle:Boolean=true
) {
 companion object {
  private fun key(name:String,id:Int)="${name}:$id"
  fun load(c:Context,id:Int):WidgetSettings {
   val p=c.getSharedPreferences("widget_settings",Context.MODE_PRIVATE)
   return WidgetSettings(p.getString(key("category",id),"All apps")?:"All apps",p.getString(key("layout",id),"List")?:"List",
    p.getString(key("sort",id),"A–Z")?:"A–Z",p.getInt(key("background",id),0xFF17151E.toInt()),p.getInt(key("opacity",id),80),
    p.getInt(key("textColor",id),0xFFF2EAF8.toInt()),p.getInt(key("textSize",id),14),p.getInt(key("iconSize",id),40),
    p.getInt(key("columns",id),3),p.getBoolean(key("showIcons",id),true),p.getBoolean(key("showTitle",id),true))
  }
  fun save(c:Context,id:Int,s:WidgetSettings):Boolean {
   val p=c.getSharedPreferences("widget_settings",Context.MODE_PRIVATE)
   val ok=p.edit().putString(key("category",id),s.category).putString(key("layout",id),s.layout).putString(key("sort",id),s.sort)
    .putInt(key("background",id),s.background).putInt(key("opacity",id),s.opacity).putInt(key("textColor",id),s.textColor)
    .putInt(key("textSize",id),s.textSize).putInt(key("iconSize",id),s.iconSize).putInt(key("columns",id),s.columns)
    .putBoolean(key("showIcons",id),s.showIcons).putBoolean(key("showTitle",id),s.showTitle).commit()
   return ok && load(c,id)==s
  }
 }
}