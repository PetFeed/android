package com.petfeed.petfeed.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class User(
        @SerializedName("_id")
        var _id: String = "",

        @SerializedName("user_id")
        var id: String = "",

        @SerializedName("user_pw")
        var password: String = "",

        @SerializedName("nickname")
        var nickname: String = "",

        @SerializedName("last_conn")
        var lastConn: Date = Date(),

        @SerializedName("create_date")
        var createDate: Date = Date(),

        @SerializedName("rank")
        var rank: String = "General",

        @SerializedName("profile") // url
        var profile: String = "",

        @SerializedName("following")
        var following: ArrayList<String> = ArrayList(),

        @SerializedName("followers")
        var followers: ArrayList<String> = ArrayList(),

        @SerializedName("cards")
        var cards: ArrayList<Any> = ArrayList(), // 나중에,,

        @SerializedName("logs")
        var logs: ArrayList<Any> = ArrayList() //TODO:// Log 클래스
)