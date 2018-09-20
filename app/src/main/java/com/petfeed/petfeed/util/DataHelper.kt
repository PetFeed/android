package com.petfeed.petfeed.util

import android.graphics.Bitmap
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.User


// 모든 데이터를 가지자
class DataHelper private constructor() {

    var token = ""
    var user = User()
    var mainBoards = ArrayList<Board>()
    var selectedBoard = Board()

    companion object {
        private var dataHelper: DataHelper? = null
        val datas: DataHelper?
            get() {
                if (dataHelper == null)
                    dataHelper = DataHelper()
                return dataHelper
            }
    }
}