package com.google.samples.propertyanimation

import android.animation.*
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button
    lateinit var rotateXButton: Button
    lateinit var pivotDialog: AlertDialog
    var defaultPivot: Float = 0f
    var pivot : Float by Delegates.observable(0f) {_, _, newValue ->
        star.pivotX = newValue
        Log.d("pivot", "${star.pivotX} pivot new value")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)
        rotateXButton = findViewById(R.id.rotateXButton)

        Log.d("pivot", "${star.pivotX} pivot on create")
        defaultPivot = star.width.toFloat()
        pivot = defaultPivot

        val view = layoutInflater.inflate(R.layout.dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        pivotDialog = AlertDialog.Builder(this).create()
        pivotDialog.setView(view)
        pivotDialog.setTitle("Change PivotX for star")
        pivotDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Change"){dialog, which ->
            pivot = editText.text.toString().toFloat()
        }

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }

        rotateXButton.setOnClickListener{
            rotaterX()
        }

        Log.d("pivot", "${star.pivotX} pivot on create")
    }

    private fun rotater() {
        Log.d("pivot", "${star.pivotX} pivot")

        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        animator.duration = 1000
        animator.disableDuringAnimation(rotateButton)
        animator.start()
    }

    private fun rotaterX() {
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION_X, -360f, 0f)
        animator.duration = 1000
        animator.disableDuringAnimation(rotateXButton)
        animator.start()
    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 0f, 200f)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableDuringAnimation(scaleButton)
        animator.start()
    }

    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableDuringAnimation(fadeButton)
        animator.start()
    }

    private fun colorizer() {
        val animatorBg = ObjectAnimator.ofArgb(star.parent,
            "backgroundColor", Color.BLACK, Color.RED)
        val animator = ObjectAnimator.ofArgb(star,
            "backgroundColor", Color.BLACK, Color.GREEN)
        animatorBg.duration = 1000
        animatorBg.repeatCount = 1
        animatorBg.repeatMode = ObjectAnimator.REVERSE
        animatorBg.disableDuringAnimation(colorizeButton)
        animatorBg.start()
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW = star.width.toFloat()
        var starH = star.height.toFloat()

        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)

        newStar.scaleX = Math.random().toFloat() * 1.5f + 0.1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        newStar.translationX = Math.random().toFloat() * containerW - starW / 2

        container.addView(newStar)

        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f + Math.random().toFloat())

        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION, (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()

        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (500 + Math.random() * 1500).toLong()

        set.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })

        set.start()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.pivot -> {
                pivotDialog.show()
                true
            }
            R.id.resetPivot -> {
                pivot = defaultPivot
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    star.resetPivot()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ObjectAnimator.disableDuringAnimation(view : View){
        this.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

}
