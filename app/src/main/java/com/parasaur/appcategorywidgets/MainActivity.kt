package com.parasaur.appcategorywidgets

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private var filter = "All apps"
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); draw() }
    private fun draw() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(18, 22, 18, 12); setBackgroundColor(0xFF17151E.toInt()) }
        root.addView(TextView(this).apply { text = "App Category Widgets"; textSize = 24f; setTextColor(0xFFF2EAF8.toInt()) })
        root.addView(TextView(this).apply { text = "Organize installed apps into categories and create independent scrolling widgets."; setTextColor(0xFFBDB4C8.toInt()); setPadding(0,8,0,12) })
        val bar = LinearLayout(this)
        bar.addView(Button(this).apply { text = "New category"; setOnClickListener { addCategory() } }, LinearLayout.LayoutParams(0,-2,1f))
        bar.addView(Button(this).apply { text = "Refresh"; setOnClickListener { draw() } }, LinearLayout.LayoutParams(0,-2,1f))
        root.addView(bar)
        root.addView(TextView(this).apply { text = "Add widgets from your home screen: long-press → Widgets → App Category Widgets. Choose a category for each widget."; setTextColor(0xFFE0D3ED.toInt()); setPadding(4,12,4,12) })
        val scroll = ScrollView(this)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        setContentView(root)
        val choices = listOf("All apps") + CategoryStore.categories(this)
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, choices)
        spinner.setSelection(choices.indexOf(filter).coerceAtLeast(0))
        list.addView(spinner)
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                if (filter != choices[pos]) { filter = choices[pos]; draw() }
            }
        }
        val pm = packageManager
        CategoryStore.apps(this).filter { filter == "All apps" || CategoryStore.categoryFor(this,it) == filter }.forEach { info ->
            val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(2,7,2,7) }
            row.addView(ImageView(this).apply { setImageDrawable(pm.getApplicationIcon(info)) }, LinearLayout.LayoutParams(42,42))
            val label = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(10,0,4,0) }
            label.addView(TextView(this).apply { text = pm.getApplicationLabel(info).toString(); textSize=16f; setTextColor(0xFFF2EAF8.toInt()) })
            label.addView(TextView(this).apply { text = "Category: " + CategoryStore.categoryFor(this@MainActivity,info); textSize=12f; setTextColor(0xFFBDB4C8.toInt()) })
            row.addView(label, LinearLayout.LayoutParams(0,-2,1f))
            row.addView(Button(this).apply { text="Change"; setOnClickListener { choose(info.packageName) } })
            list.addView(row)
            list.addView(android.view.View(this).apply { setBackgroundColor(0xFF393341.toInt()) }, LinearLayout.LayoutParams(-1,1))
        }
    }
    private fun choose(pkg: String) {
        val values = CategoryStore.categories(this).toTypedArray()
        AlertDialog.Builder(this).setTitle("Assign category").setItems(values) { _, which ->
            CategoryStore.assign(this,pkg,values[which]); draw(); CategoryWidgetProvider.refreshAll(this)
        }.show()
    }
    private fun addCategory() {
        val input = EditText(this).apply { hint="Category name" }
        AlertDialog.Builder(this).setTitle("Create category").setView(input).setPositiveButton("Create") { _, _ ->
            CategoryStore.addCategory(this,input.text.toString()); filter=input.text.toString().trim(); draw()
        }.setNegativeButton("Cancel",null).show()
    }
}
