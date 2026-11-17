package com.parasaur.appcategorywidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.RemoteViews

class CategoryWidgetProvider:AppWidgetProvider(){
 override fun onUpdate(c:Context,m:AppWidgetManager,ids:IntArray){ids.forEach{updateOne(c,m,it)}}
 companion object{
  private fun configurationPendingIntent(c:Context,id:Int):PendingIntent {
   val intent=Intent(c,WidgetConfigActivity::class.java)
    .setAction("com.parasaur.appcategorywidgets.CONFIGURE_WIDGET")
    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)
    .setData(Uri.parse("appcategorywidgets://configure/widget/$id"))
    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
   return PendingIntent.getActivity(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  }
  fun updateOne(c:Context,m:AppWidgetManager,id:Int){
   if(id==AppWidgetManager.INVALID_APPWIDGET_ID)return
   val s=WidgetSettings.load(c,id)
   val v=RemoteViews(c.packageName,R.layout.category_widget)
   v.setTextViewText(R.id.widget_title,s.category)
   v.setViewVisibility(R.id.widget_title,if(s.showTitle)View.VISIBLE else View.GONE)
   val alpha=s.opacity.coerceIn(0,100)*255/100
   val background=Color.argb(alpha,Color.red(s.background),Color.green(s.background),Color.blue(s.background))
   v.setInt(R.id.widget_root,"setBackgroundColor",background)
   v.setInt(R.id.widget_content,"setBackgroundColor",background)
   v.setViewVisibility(R.id.widget_list,if(s.layout=="List")View.VISIBLE else View.GONE)
   v.setViewVisibility(R.id.widget_grid,if(s.layout=="Grid")View.VISIBLE else View.GONE)
   val listService=Intent(c,CategoryWidgetService::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)
   listService.data=Uri.parse("appcategorywidgets://widget/$id/list")
   val gridService=Intent(c,CategoryWidgetService::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)
   gridService.data=Uri.parse("appcategorywidgets://widget/$id/grid")
   v.setRemoteAdapter(R.id.widget_list,listService)
   v.setRemoteAdapter(R.id.widget_grid,gridService)
   v.setInt(R.id.widget_grid,"setNumColumns",s.columns.coerceIn(2,5))
   v.setEmptyView(if(s.layout=="List")R.id.widget_list else R.id.widget_grid,R.id.widget_empty)
   val config=configurationPendingIntent(c,id)
   v.setOnClickPendingIntent(R.id.widget_config,config)
   v.setOnClickPendingIntent(R.id.widget_title,config)
   m.updateAppWidget(id,v)
   m.notifyAppWidgetViewDataChanged(id,intArrayOf(R.id.widget_list,R.id.widget_grid))
  }
  fun refreshAll(c:Context){val m=AppWidgetManager.getInstance(c);m.getAppWidgetIds(ComponentName(c,CategoryWidgetProvider::class.java)).forEach{updateOne(c,m,it)}}
 }
}