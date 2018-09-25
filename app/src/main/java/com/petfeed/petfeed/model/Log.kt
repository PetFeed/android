package com.petfeed.petfeed.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Log(
        @SerializedName("date")
        var date: Date = Date(),

        @SerializedName("_id")
        var _id: String = "",

        @SerializedName("type")
        var type: String = "",

        @SerializedName("dataId")
        var dataId: String = "",

        @SerializedName("text")
        var text: String = "",

        @SerializedName("from")
        var fromUser: User = User(),

        @SerializedName("to")
        var toId: String = "",

        var isChecked: Boolean? = false)