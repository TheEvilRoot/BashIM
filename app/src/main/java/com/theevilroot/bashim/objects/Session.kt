package com.theevilroot.bashim.objects

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import com.theevilroot.bashim.R
import com.theevilroot.bashim.utils.BACKEND_URL
import com.theevilroot.bashim.utils.isInternetAvailable
import com.theevilroot.bashim.utils.parseInt
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import java.io.File
import javax.crypto.EncryptedPrivateKeyInfo
import kotlin.concurrent.thread

class Session {

    var isOnline = false
    var isOffline = false
    var stepCount = 10 // Will specified in Preferences
    var currentID = 0 // Will load from Account or Cache
    var lastID = -1 // Will load on request {@link #requestLastID()}

    var username: String = "[UNAUTHORIZED]"
    var userID: Int = -1

    private var userToken = "[UNAUTHORIZED]"
    private var userPicture: Bitmap? = null


    fun requestLastIDAsync(context: Context,onLoad: (Boolean) -> Unit) {
        thread(true) { onLoad(requestLastIDBlock(context)) }
    }
    /**
     *  MUST run NOT in UI Thread
     */
    fun requestLastIDBlock(context: Context): Boolean {
        if(!context.isInternetAvailable())
            return false
        val result = requestLastID()
        if(result < 0)
            return false
        lastID = result
        currentID = lastID
        return true
    }

    private fun requestLastID(): Int{
        val doc = Jsoup.connect("https://bash.im/").get()
        return parseInt(doc.select(".quote")[0].select(".id").text().replace("\\D".toRegex(), ""), -1)
    }

    /**
     * Async request user picture
     */
    fun requestUserPicture(onLoad: (Bitmap?) -> Unit) {
        thread(true) {
            if(userPicture != null) {
                onLoad(userPicture)
                return@thread
            }
            try {
                val req = Jsoup.connect("$BACKEND_URL/userPicture.php?user_id=$userID&token=$userToken").execute()
                if(req.statusCode() != 200)
                    throw RuntimeException("Picture backend is unavailable")
            }catch (e: UnsupportedMimeTypeException) {
                try {
                    userPicture = Picasso.get().load("$BACKEND_URL/userPicture.php?user_id=$userID&token=$userToken").error(R.drawable.user).get()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
            onLoad(userPicture)
        }
    }

    fun requestAuth(username: String, password: String, onSuccess: () -> Unit, onError: (Throwable?,String, String) -> Unit) {
        thread(true) {
            if(isOnline) {
                onError(null,"Client error!", "Already authorized!")
                return@thread
            }
            try {
                val doc = Jsoup.connect("$BACKEND_URL/userAuth.php").data("username", username).data("password", password).post()
                val json = JsonParser().parse(doc.text()).asJsonObject
                if(json["error"].asBoolean) {
                    onError(null, json["msg"].asString, json["data"].asJsonObject["error"].asString ?: "")
                    return@thread
                }
                val user = json["data"].asJsonObject["user"].asJsonObject
                this.username = user["username"].asString
                this.userID = user["id"].asInt
                this.userToken = json["data"].asJsonObject["token"].asString
                this.isOnline = true
                onSuccess()
            }catch (e: Exception) {
                e.printStackTrace()
                onError(e, e.message?:"", "")
            }
        }
    }

    fun requestVerification(onResult: (Boolean, Boolean) -> Unit) {
        thread(true) {
            try {
                val doc = Jsoup.connect("$BACKEND_URL/userVerify.php").data("username", username).data("token", userToken).timeout(5000).post()
                val json = JsonParser().parse(doc.text()).asJsonObject
                if(json["error"].asBoolean) {
                    onResult(true, false)
                    return@thread
                }
                val user = json["data"].asJsonObject["user"].asJsonObject
                this.username = user["username"].asString
                this.userID = user["id"].asInt
                this.isOnline = true
                onResult(true, true)
            }catch (e: Exception) {
                e.printStackTrace()
                onResult(false, false)
            }
        }
    }

    fun saveUser(context: Context) {
        val file = File(context.filesDir, "userdata.json")
        if(!file.exists())
            file.createNewFile()
        val json = JsonObject()
        json.addProperty("username", username)
        json.addProperty("token", userToken)
        file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
    }

    fun removeLocalUser(context: Context) {
        val file = File(context.filesDir, "userdata.json")
        file.deleteOnExit()
    }

    fun loadLocal(context: Context,onResult: (Boolean) -> Unit) {
        thread(true) {
            try {
                val file = File(context.filesDir, "userdata.json")
                if (!file.exists()) {
                    onResult(false)
                    return@thread
                }
                val json = JsonParser().parse(file.readText()).asJsonObject
                this.username = json["username"].asString
                this.userToken = json["token"].asString
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}