package com.petfeed.petfeed.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {
    val AD_FCM_TOKEN = "AD_FCM_TOKEN"
    val AD_LOGIN_ID = "AD_LOGIN_ID"
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        sendRegistrationToServer(FirebaseInstanceId.getInstance().token)
        if (isValidString(refreshedToken)) { //토큰이 널이거나 빈 문자열이 아닌 경우
            if (!isValidString(getSharedPreferencesStringData(applicationContext, AD_FCM_TOKEN))) { //토큰에 데이터가 없는 경우에만 저장
                setSharedPreferencesStringData(applicationContext, AD_FCM_TOKEN, refreshedToken)
            }
        }
        Log.e("firebaseToken", "$refreshedToken")
    }

    fun getSharedPreferencesStringData(context: Context, key: String): String? {
        val pref = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        return pref.getString(key, null)
    }

    fun setSharedPreferencesStringData(context: Context, key: String, value: String?) {
        val pref = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        val edit = pref.edit()
        edit.putString(key, value)
        edit.apply()
    }

    fun isValidString(string: String?): Boolean = string == null || string.isEmpty()
    private fun sendRegistrationToServer(token: String?) {
        //FCM 토큰 갱신
    }
}