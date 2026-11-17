package com.parasaur.appcategorywidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.*

class WidgetConfigActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        val id=intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val options=listOf("All apps")+CategoryStore.categories(this)
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        root.addView(TextView(this).apply { text="Choose a category for this widget"; textSize=20f })
        val spinner=Spinner(this)
        spinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,options)
        root.addView(spinner)
        root.addView(Button(this).apply {
            text="Add widget"
            setOnClickListener {
                getSharedPreferences("widget_settings",MODE_PRIVATE).edit().putString("category:" + id,options[spinner.selectedItemPosition]).apply()
                CategoryWidgetProvider.updateOne(this@WidgetConfigActivity,AppWidgetManager.getInstance(this@WidgetConfigActivity),id)
                setResult(RESULT_OK,Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id)); finish()
            }
        })
        setContentView(root)
    }
}
