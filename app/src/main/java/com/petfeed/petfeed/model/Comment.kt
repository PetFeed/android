package com.petfeed.petfeed.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Comment(
        @SerializedName("parent")
        var parentId: String? = "",

        @SerializedName("_id")
        var commentId: String = "",

        @SerializedName("writer")
        var writer: User? = User(),

        @SerializedName("content")
        var content: String? = "",

        @SerializedName("create_date")
        var createDate: Date? = Date(),

        @SerializedName("re_comments")
        var reComment: ArrayList<Comment> = ArrayList())