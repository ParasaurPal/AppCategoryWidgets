package com.parasaur.appcategorywidgets

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.concurrent.Executors

class MainActivity:Activity(){
 private lateinit var root:LinearLayout;private var filter="All apps";private val executor=Executors.newSingleThreadExecutor()
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);draw()}
 override fun onDestroy(){executor.shutdownNow();super.onDestroy()}
 private fun draw(){
  root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,20,18,12);setBackgroundColor(0xFF17151E.toInt())}
  root.addView(TextView(this).apply{text="App Category Widgets";textSize=24f;setTextColor(0xFFF2EAF8.toInt())})
  root.addView(TextView(this).apply{text="Make as many independent app widgets as you want. Each widget can use a Play Store or custom category, list/grid layout, sorting and its own appearance.";setTextColor(0xFFBDB4C8.toInt());setPadding(0,8,0,10)})
  val actions=LinearLayout(this)
  actions.addView(Button(this).apply{text="New category";setOnClickListener{addCategory()}},LinearLayout.LayoutParams(0,-2,1f))
  actions.addView(Button(this).apply{text="Look up Play Store categories";setOnClickListener{lookup(this)}},LinearLayout.LayoutParams(0,-2,1f))
  root.addView(actions)
  root.addView(TextView(this).apply{text="Play Store lookup reads public listing metadata when available. Google has no supported public category API; failed lookups can be assigned manually.";textSize=12f;setTextColor(0xFFBDB4C8.toInt());setPadding(2,4,2,8)})
  root.addView(TextView(this).apply{text="Add widgets from your home screen: long-press → Widgets → App Category Widgets. Configure each widget separately.";setTextColor(0xFFE0D3ED.toInt());setPadding(2,4,2,8)})
  val scroll=ScrollView(this);val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};scroll.addView(list);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));setContentView(root)
  val choices=listOf("All apps",CategoryStore.UNCATEGORIZED)+CategoryStore.categories(this);val spinner=Spinner(this);spinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,choices);spinner.setSelection(choices.indexOf(filter).coerceAtLeast(0));list.addView(spinner)
  spinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
   override fun onNothingSelected(p:AdapterView<*>?){}
   override fun onItemSelected(p:AdapterView<*>?,v:View?,pos:Int,rowId:Long){if(filter!=choices[pos]){filter=choices[pos];draw()}}
  }
  val pm=packageManager
  CategoryStore.apps(this).filter{when(filter){"All apps"->true;CategoryStore.UNCATEGORIZED->CategoryStore.isUncategorized(this,it.packageName);else->CategoryStore.categoryFor(this,it)==filter}}.forEach{info->
   val row=LinearLayout(this).apply{gravity=Gravity.CENTER_VERTICAL;setPadding(2,7,2,7)}
   row.addView(ImageView(this).apply{setImageDrawable(pm.getApplicationIcon(info))},LinearLayout.LayoutParams(42,42))
   val label=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(10,0,4,0)}
   label.addView(TextView(this).apply{text=pm.getApplicationLabel(info).toString();textSize=16f;setTextColor(0xFFF2EAF8.toInt())})
   val uncategorized=CategoryStore.isUncategorized(this@MainActivity,info.packageName)
   val src=if(uncategorized)"Not assigned" else if(CategoryStore.assigned(this@MainActivity,info.packageName)!=null)"Manual" else "Play Store lookup"
   label.addView(TextView(this).apply{text="Category: "+(if(uncategorized)CategoryStore.UNCATEGORIZED else CategoryStore.categoryFor(this@MainActivity,info))+" • "+src;textSize=12f;setTextColor(0xFFBDB4C8.toInt())})
   row.addView(label,LinearLayout.LayoutParams(0,-2,1f));row.addView(Button(this).apply{text="Change";setOnClickListener{choose(info.packageName)}});list.addView(row)
   list.addView(View(this).apply{setBackgroundColor(0xFF393341.toInt())},LinearLayout.LayoutParams(-1,1))
  }
 }
 private fun lookup(button:Button){
  button.isEnabled=false;button.text="Looking up…"
  executor.execute{
   val apps=CategoryStore.apps(this);var found=0
   apps.forEach{if(CategoryStore.assigned(this,it.packageName)==null&&PlayStoreCategoryLookup.lookup(this,it.packageName)!=null)found++}
   runOnUiThread{Toast.makeText(this,"Play Store category results: "+found+" of "+apps.size,Toast.LENGTH_LONG).show();draw();CategoryWidgetProvider.refreshAll(this)}
  }
 }
 private fun choose(pkg:String){
  val cats=listOf("Unassign (move to Uncategorized)") + CategoryStore.categories(this)
  AlertDialog.Builder(this).setTitle("Assign category").setItems(cats.toTypedArray(), { _: android.content.DialogInterface, i: Int ->
   if(i==0)CategoryStore.clearCategory(this,pkg) else CategoryStore.assign(this,pkg,cats[i])
   draw();CategoryWidgetProvider.refreshAll(this)
  }).setNeutralButton("Create new category", { _: android.content.DialogInterface, _: Int -> addCategory() }).show()
 }
 private fun addCategory(){val input=EditText(this).apply{hint="Category name"}
  AlertDialog.Builder(this).setTitle("Create category").setView(input).setPositiveButton("Create"){_,_->filter=input.text.toString().trim();CategoryStore.addCategory(this,filter);draw();CategoryWidgetProvider.refreshAll(this)}.setNegativeButton("Cancel",null).show()
 }
}
