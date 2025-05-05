package com.example.main

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
// библа для дебага с помощью Logcat
import android.util.Log

class MainActivity : AppCompatActivity() {

    // глобал переменные, надо бы вообще их устранить и засунуть в onCreate
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mainBlock: RelativeLayout
    private lateinit var mainBlockSlot: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // находим по айдишнику лэйауты и вьюхи из xml
        drawerLayout = findViewById(R.id.drawer_layout)

        mainBlock = findViewById<RelativeLayout>(R.id.main)
        // onTouchListener - при касании вызывает функцию
        mainBlock.setOnTouchListener {v, event -> handleMove(v,event)}
        mainBlockSlot = findViewById<TextView>(R.id.mainSlot)
        // onDragListener - для зон дропа блоков
        mainBlockSlot.setOnDragListener {v, event -> onDrag(v, event)}

        findViewById<TextView>(R.id.newBlock).setOnTouchListener {v, event -> handleDrag(v, event)}

        findViewById<TextView>(R.id.item3).setOnTouchListener {v, event -> handleDrag(v, event, true)}

        //findViewById<RelativeLayout>(R.id.rootLayout).setOnDragListener {v, event -> onDrag(v, event)}
    }

    // функция для сетапа дрэг энд дроп ивента
    private fun handleDrag(v: View, event: MotionEvent, flag: Boolean = false): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            Log.d("HandleDrag", "Down")
            drawerLayout.closeDrawer(GravityCompat.START)
            v.startDragAndDrop(null, View.DragShadowBuilder(v), v, 0)
            if (!flag) {
                v.visibility = View.INVISIBLE
            }
            return true
        }
        return false
    }

    // логика дрэг энд дропа
    private fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d("DragEvent", "Started")
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("DragEvent", "Entered")
                v.visibility = View.VISIBLE
                v.invalidate()
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("DragEvent", "Exited")
                v.visibility = View.GONE
                v.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                Log.d("DragEvent", "Dropped")
                val draggedView = event.localState as View
                draggedView.x = v.x
                draggedView.y = v.y
                return false
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("DragEvent", "Ended")
                if (!event.result) {
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    val draggedView = event.localState as View
                    draggedView.x = x.toFloat() - (draggedView.width / 2)
                    draggedView.y = y.toFloat() - (draggedView.height / 2)
                    draggedView.visibility = View.VISIBLE
                }
                return true
            }
        }
        return true
    }

    // эт юзал для создания нью блоков при перетаскивании из левой панели
    /*
    val item = event.localState as TextView
                    val newBlock = TextView(this).apply {
                        layoutParams = RelativeLayout.LayoutParams(dpToPx(250f), dpToPx(50f))
                        x = event.x - (dpToPx(250f / 2))
                        y = event.y - (dpToPx(50f / 2))
                        setBackgroundColor(Color.BLACK)
                        text = item.text
                        setTextColor(Color.WHITE)
                        setPadding(dpToPx(10f), dpToPx(10f), dpToPx(10f), dpToPx(10f))
                    }

                    val rootLayout = findViewById<RelativeLayout>(R.id.rootLayout)
                    rootLayout.addView(newBlock)

                    newBlock.setOnTouchListener { v, event -> handleDrag(v, event) }
     */

    // прост перетаскивание блоков, ща прописано ток для блока main
    // вообще сюда надо реализовать всю логику из onDrag чтобы избавиться от баганного дрэг энд дроп
    private fun handleMove(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.performClick()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                v.x = event.rawX - (v.width / 2)
                v.y = event.rawY - (v.height / 4)
                return true
            }
        }
        return false
    }

    // kotlin не шарит за xml-ное dp, поэтому функция для конвертирования
    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}