package com.parasaur.appcategorywidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class CategoryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(c: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { updateOne(c,manager,it) } }
    companion object {
        fun updateOne(c: Context, manager: AppWidgetManager, id: Int) {
            val category = c.getSharedPreferences("widget_settings",0).getString("category:" + id,"All apps") ?: "All apps"
            val views = RemoteViews(c.packageName,R.layout.category_widget)
            views.setTextViewText(R.id.widget_title,category)
            val service = Intent(c,CategoryWidgetService::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)
            views.setRemoteAdapter(R.id.widget_list,service)
            views.setEmptyView(R.id.widget_list,R.id.widget_empty)
            val open = PendingIntent.getActivity(c, id, c.packageManager.getLaunchIntentForPackage(c.packageName), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setPendingIntentTemplate(R.id.widget_list,open)
            manager.updateAppWidget(id,views)
            manager.notifyAppWidgetViewDataChanged(id,R.id.widget_list)
        }
        fun refreshAll(c: Context) {
            val m=AppWidgetManager.getInstance(c)
            m.getAppWidgetIds(ComponentName(c,CategoryWidgetProvider::class.java)).forEach { updateOne(c,m,it) }
        }
    }
}
