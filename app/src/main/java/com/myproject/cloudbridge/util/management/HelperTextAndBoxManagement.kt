package com.myproject.cloudbridge.util.management

import android.content.Context
import androidx.core.content.ContextCompat
import com.myproject.cloudbridge.R

fun Context.setHelperBoxBlack() = ContextCompat.getColor(this, R.color.helper_box_color_black)
fun Context.setHelperTextRed() = ContextCompat.getColor(this, R.color.helper_text_color_red)
fun Context.setHelperTextRedList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_red)
fun Context.setHelperTextGreen() = ContextCompat.getColor(this, R.color.helper_text_color_green)
fun Context.setHelperTextGreenList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_green)