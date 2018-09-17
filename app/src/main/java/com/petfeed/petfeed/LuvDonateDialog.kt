package com.petfeed.petfeed

import android.app.Dialog
import android.content.Context
import android.os.Bundle

class LuvDonateDialog : Dialog {

    constructor(context: Context) : super(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_luv_donate)
    }
}