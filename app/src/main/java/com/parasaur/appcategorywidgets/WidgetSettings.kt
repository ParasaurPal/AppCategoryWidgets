package com.parasaur.appcategorywidgets

import android.content.Context

data class WidgetSettings(
 val category:String="All apps", val layout:String="List", val sort:String="A–Z",
 val background:Int=0xFF17151E.toInt(), val opacity:Int=80, val textColor:Int=0xFFF2EAF8.toInt(),
 val textSize:Int=14, val iconSize:Int=40, val columns:Int=3, val showIcons:Boolean=true, val showTitle:Boolean=true
) {
 companion object {
  fun load(c:Context,id:Int):WidgetSettings {
   val p=c.getSharedPreferences("widget_settings",0)
   return WidgetSettings(p.getString("category:$id","All apps")?:"All apps",p.getString("layout:$id","List")?:"List",
    p.getString("sort:$id","A–Z")?:"A–Z",p.getInt("background:$id",0xFF17151E.toInt()),p.getInt("opacity:$id",80),
    p.getInt("textColor:$id",0xFFF2EAF8.toInt()),p.getInt("textSize:$id",14),p.getInt("iconSize:$id",40),
    p.getInt("columns:$id",3),p.getBoolean("showIcons:$id",true),p.getBoolean("showTitle:$id",true))
  }
  fun save(c:Context,id:Int,s:WidgetSettings) { c.getSharedPreferences("widget_settings",0).edit()
   .putString("category:$id",s.category).putString("layout:$id",s.layout).putString("sort:$id",s.sort)
   .putInt("background:$id",s.background).putInt("opacity:$id",s.opacity).putInt("textColor:$id",s.textColor)
   .putInt("textSize:$id",s.textSize).putInt("iconSize:$id",s.iconSize).putInt("columns:$id",s.columns)
   .putBoolean("showIcons:$id",s.showIcons).putBoolean("showTitle:$id",s.showTitle).apply()
  }
 }
}
