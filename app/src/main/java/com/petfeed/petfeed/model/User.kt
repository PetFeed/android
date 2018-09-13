package com.petfeed.petfeed.model

import com.google.gson.annotations.SerializedName
import java.util.*

class User(
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
        var following: Array<User> = Array(0) { User() },

        @SerializedName("followers")
        var followers: Array<User> = Array(0) { User() },

        @SerializedName("cards")
        var cards: Array<Any> = Array(0){""}, // 나중에,,

        @SerializedName("logs")
        var logs: Array<Any> = Array(0){""} //TODO:// Log 클래스
)