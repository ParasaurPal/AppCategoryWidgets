package com.parasaur.appcategorywidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class CategoryWidgetService:RemoteViewsService(){
 override fun onGetViewFactory(intent:Intent):RemoteViewsFactory=Factory(this,intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,0))
 private class Factory(val c:android.content.Context,val id:Int):RemoteViewsFactory{
  private var items=emptyList<android.content.pm.ApplicationInfo>();private var s=WidgetSettings()
  override fun onCreate(){}
  override fun onDataSetChanged(){
   s=WidgetSettings.load(c,id)
   val list=CategoryStore.apps(c).filter{when(s.category){"All apps"->true;CategoryStore.UNCATEGORIZED->CategoryStore.isUncategorized(c,it.packageName);else->CategoryStore.categoryFor(c,it)==s.category}}
   items=if(s.sort=="Z–A")list.sortedByDescending{c.packageManager.getApplicationLabel(it).toString().lowercase()}else list
  }
  override fun onDestroy(){}
  override fun getCount()=items.size
  override fun getViewAt(pos:Int):RemoteViews?{
   if(pos !in items.indices)return null
   val app=items[pos];val grid=s.layout=="Grid"
   val row=RemoteViews(c.packageName,if(grid)R.layout.category_widget_grid_item else R.layout.category_widget_row)
   row.setTextViewText(R.id.app_name,c.packageManager.getApplicationLabel(app).toString())
   row.setTextColor(R.id.app_name,s.textColor);row.setTextViewTextSize(R.id.app_name,android.util.TypedValue.COMPLEX_UNIT_SP,s.textSize.toFloat())
   row.setViewVisibility(R.id.app_icon,if(s.showIcons)android.view.View.VISIBLE else android.view.View.GONE)
   row.setInt(R.id.app_icon,"setMaxWidth",s.iconSize);row.setInt(R.id.app_icon,"setMaxHeight",s.iconSize)
   if(s.showIcons){
    val drawable=c.packageManager.getApplicationIcon(app)
    val bitmap=Bitmap.createBitmap(drawable.intrinsicWidth.coerceAtLeast(1),drawable.intrinsicHeight.coerceAtLeast(1),Bitmap.Config.ARGB_8888)
    drawable.setBounds(0,0,bitmap.width,bitmap.height)
    drawable.draw(Canvas(bitmap))
    row.setImageViewBitmap(R.id.app_icon,bitmap)
   }
   c.packageManager.getLaunchIntentForPackage(app.packageName)?.let{launch->
    val pi=PendingIntent.getActivity(c,id*100000+(app.packageName.hashCode() and 0x7fffffff),launch,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    row.setOnClickPendingIntent(R.id.app_row,pi)
   }
   return row
  }
  override fun getLoadingView():RemoteViews?=null
  override fun getViewTypeCount()=2
  override fun getItemId(pos:Int)=items.getOrNull(pos)?.packageName?.hashCode()?.toLong()?:pos.toLong()
  override fun hasStableIds()=true
 }
}
