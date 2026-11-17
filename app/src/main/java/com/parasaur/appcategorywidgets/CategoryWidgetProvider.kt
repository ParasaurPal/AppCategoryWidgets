package com.parasaur.appcategorywidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews

class CategoryWidgetProvider:AppWidgetProvider(){
 override fun onUpdate(c:Context,m:AppWidgetManager,ids:IntArray){ids.forEach{updateOne(c,m,it)}}
 companion object{
  fun updateOne(c:Context,m:AppWidgetManager,id:Int){
   val s=WidgetSettings.load(c,id);val v=RemoteViews(c.packageName,R.layout.category_widget)
   v.setTextViewText(R.id.widget_title,s.category);v.setViewVisibility(R.id.widget_title,if(s.showTitle)View.VISIBLE else View.GONE)
   v.setInt(R.id.widget_root,"setBackgroundColor",(s.background and 0x00FFFFFF) or ((s.opacity.coerceIn(0,100)*255/100) shl 24))
   v.setViewVisibility(R.id.widget_list,if(s.layout=="List")View.VISIBLE else View.GONE)
   v.setViewVisibility(R.id.widget_grid,if(s.layout=="Grid")View.VISIBLE else View.GONE)
   val service=Intent(c,CategoryWidgetService::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)
   service.data=Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME))
   v.setRemoteAdapter(R.id.widget_list,service);v.setRemoteAdapter(R.id.widget_grid,service)
   v.setInt(R.id.widget_grid,"setNumColumns",s.columns.coerceIn(2,5))
   v.setEmptyView(if(s.layout=="List")R.id.widget_list else R.id.widget_grid,R.id.widget_empty)
   m.updateAppWidget(id,v);m.notifyAppWidgetViewDataChanged(id,R.id.widget_list);m.notifyAppWidgetViewDataChanged(id,R.id.widget_grid)
  }
  fun refreshAll(c:Context){val m=AppWidgetManager.getInstance(c);m.getAppWidgetIds(ComponentName(c,CategoryWidgetProvider::class.java)).forEach{updateOne(c,m,it)}}
 }
}
