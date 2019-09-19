package us.cyberstar.data.ext

import android.content.SharedPreferences



fun SharedPreferences.setUserPhone(value: String) = edit().putString("userPhone", value).apply()
fun SharedPreferences.userPhone() = getString("userPhone", null)

fun SharedPreferences.setAppMode(value: String) = edit().putString("appMode", value).apply()
fun SharedPreferences.appMode() = getString("appMode", null)

fun SharedPreferences.setAppToken(value: String) = edit().putString("appToken", value).apply()
fun SharedPreferences.appToken() = getString("appToken", null)
//fun SharedPreferences.appToken() = getString("appToken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJpYXQiOjE1NjIzNDQ3MjEsImV4cCI6MTU5MzQ0ODcyMX0.utBMaJX_RPH7UpmT06YkV0SUGpMad97cJL0fOeZKWIc")
