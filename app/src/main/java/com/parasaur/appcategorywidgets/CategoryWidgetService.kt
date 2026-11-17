package com.parasaur.appcategorywidgets

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.drawable.Icon
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class CategoryWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = Factory(this,intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,0))
    private class Factory(val c: android.content.Context,val id: Int) : RemoteViewsFactory {
        private var items = emptyList<android.content.pm.ApplicationInfo>()
        override fun onCreate() {}
        override fun onDataSetChanged() {
            val category=c.getSharedPreferences("widget_settings",0).getString("category:" + id,"All apps")
            items=CategoryStore.apps(c).filter { category=="All apps" || CategoryStore.categoryFor(c,it)==category }
        }
        override fun onDestroy() {}
        override fun getCount()=items.size
        override fun getViewAt(position: Int): RemoteViews? {
            if(position !in items.indices) return null
            val app=items[position]
            val row=RemoteViews(c.packageName,R.layout.category_widget_row)
            row.setTextViewText(R.id.app_name,c.packageManager.getApplicationLabel(app).toString())
            row.setImageViewIcon(R.id.app_icon,Icon.createWithDrawable(c.packageManager.getApplicationIcon(app)))
            val launch=c.packageManager.getLaunchIntentForPackage(app.packageName)
            if(launch!=null) row.setOnClickFillInIntent(R.id.app_row,Intent().putExtra("launch_intent",launch))
            return row
        }
        override fun getLoadingView(): RemoteViews?=null
        override fun getViewTypeCount()=1
        override fun getItemId(position: Int)=items.getOrNull(position)?.packageName?.hashCode()?.toLong() ?: position.toLong()
        override fun hasStableIds()=true
    }
}
