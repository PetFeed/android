package com.petfeed.petfeed.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Board(
        @SerializedName("createdate")
        var createDate: Date = Date(),

        @SerializedName("pictures")
        var pictures: ArrayList<String> = ArrayList(),

        @SerializedName("comments")
        var comments: ArrayList<String> = ArrayList(),

        @SerializedName("hash_tags")
        var hashTags: ArrayList<String> = ArrayList(),

        @SerializedName("likes")
        var likes: ArrayList<String> = ArrayList(),

        @SerializedName("_id")
        var boardId: String = "",

        @SerializedName("contents")
        var contents: String = "",

        @SerializedName("writer")
        var writer: String = "")
