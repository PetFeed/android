package com.petfeed.petfeed.model


// 모든 데이터를 가지자
class DataHelper private constructor() {

    var token = ""
    var user = User()
    var mainBoards = ArrayList<Board>()
    var searchBoards = ArrayList<Board>()
    var myBoards: ArrayList<Board>? = null

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