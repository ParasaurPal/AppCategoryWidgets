package com.parasaur.appcategorywidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*

class WidgetConfigActivity:Activity(){
 private var id=AppWidgetManager.INVALID_APPWIDGET_ID
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setResult(RESULT_CANCELED)
  id=intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)?:AppWidgetManager.INVALID_APPWIDGET_ID
  if(id==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return}
  val old=WidgetSettings.load(this,id)
  val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(22,18,22,18)}
  root.addView(TextView(this).apply{text="Configure this widget";textSize=23f})
  fun field(label:String,v:View){root.addView(TextView(this).apply{text=label;setPadding(0,9,0,2)});root.addView(v)}
  fun spinner(items:List<String>,sel:String)=Spinner(this).apply{
   val a=ArrayAdapter(this@WidgetConfigActivity,android.R.layout.simple_spinner_item,items)
   a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
   adapter=a
   post { setSelection(items.indexOf(sel).coerceAtLeast(0),false) }
  }
  val categoryOptions=listOf("All apps",CategoryStore.UNCATEGORIZED)+CategoryStore.categories(this)
  val cat=spinner(categoryOptions,old.category);field("Category",cat)
  val layoutOptions=listOf("List","Grid")
  val mode=spinner(layoutOptions,old.layout);field("Layout",mode)
  val sortOptions=listOf("A–Z","Z–A")
  val sort=spinner(sortOptions,old.sort);field("Sort",sort)
  fun seek(min:Int,max:Int,value:Int)=SeekBar(this).apply{this.max=max-min;progress=(value-min).coerceIn(0,this.max);tag=min}
  val opacity=seek(0,100,old.opacity);field("Background opacity (0 = transparent)",opacity)
  val textSize=seek(10,24,old.textSize);field("Text size",textSize)
  val iconSize=seek(20,64,old.iconSize);field("Icon size",iconSize)
  val cols=seek(2,5,old.columns);field("Grid columns",cols)
  val bg=EditText(this).apply{hint="#17151E";setText(String.format("#%06X",0xFFFFFF and old.background))};field("Background color",bg)
  val fg=EditText(this).apply{hint="#F2EAF8";setText(String.format("#%06X",0xFFFFFF and old.textColor))};field("Text color",fg)
  val icons=CheckBox(this).apply{text="Show app icons";isChecked=old.showIcons};root.addView(icons)
  val title=CheckBox(this).apply{text="Show widget title";isChecked=old.showTitle};root.addView(title)
  root.addView(Button(this).apply{text="Save widget";setOnClickListener{
   fun color(e:EditText,default:Int)=try{Color.parseColor(e.text.toString())}catch(_:Exception){default}
   val selectedCategory=categoryOptions.getOrElse(cat.selectedItemPosition){"All apps"}
   val selectedLayout=layoutOptions.getOrElse(mode.selectedItemPosition){"List"}
   val selectedSort=sortOptions.getOrElse(sort.selectedItemPosition){"A–Z"}
   val s=WidgetSettings(selectedCategory,selectedLayout,selectedSort,color(bg,old.background),opacity.progress,color(fg,old.textColor),textSize.progress+10,iconSize.progress+20,cols.progress+2,icons.isChecked,title.isChecked)
   WidgetSettings.save(this@WidgetConfigActivity,id,s)
   CategoryWidgetProvider.updateOne(this@WidgetConfigActivity,AppWidgetManager.getInstance(this@WidgetConfigActivity),id)
   setResult(RESULT_OK,Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id));finish()
  }})
  root.addView(Button(this).apply{text="Cancel";setOnClickListener{finish()}})
  val scroll=ScrollView(this);scroll.addView(root);setContentView(scroll)
 }
}
