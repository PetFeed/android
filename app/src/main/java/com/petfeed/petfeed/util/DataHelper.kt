package com.petfeed.petfeed.util

import com.petfeed.petfeed.model.User


// 모든 데이터를 가지자
class DataHelper private constructor() {

    var user = User()

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