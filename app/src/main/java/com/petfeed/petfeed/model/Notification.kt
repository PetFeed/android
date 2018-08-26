package com.petfeed.petfeed.model

import android.graphics.drawable.Drawable

class Notification(var time: String = "",
                   var content: String = "",
                   var img: Drawable? = null,
                   var isChecked: Boolean = false)