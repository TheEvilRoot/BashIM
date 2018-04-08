package com.theevilroot.bashim.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import kotlin.concurrent.thread
import org.apache.commons.lang.exception.ExceptionUtils
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE



class LaunchScreenActivity: AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var app: SimpleBash
    lateinit var errorView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launch_screen)

        progressBar = findViewById(R.id.progressBar)
        app = application as SimpleBash
        errorView = findViewById(R.id.error_view)

        thread(start = true, block = {
            Thread.sleep(1000)
            val file = File(filesDir, "simplebash.json")
            try {
                val json = JsonParser().parse(if (!file.exists()) {
                    makeFile(file)
                } else {
                    loadFile(file)
                })
                if(json == null || !json.isJsonObject) {
                    Toast.makeText(this, "Json is NULL", Toast.LENGTH_SHORT).show()
                }else{
                    val data = json.asJsonObject
                    app.lastQuote = data.get("lastQuote").asInt
                    app.lastTab = data.get("lastTab").asInt
                    app.favorites = ArrayList(data.get("favorites").asJsonArray.map { it.asJsonObject }.map { Quote(it.get("id").asString, it.get("rate").asString, it.get("content").asString, it.get("url").asString) })
                    runOnUiThread { startActivity(Intent(this, QuotesActivity::class.java)); finish() }
                }
            }catch (e: Exception) {
                runOnUiThread { showError(e) }
                file.delete()
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun showError(e: Exception) {
        errorView.setOnClickListener {
            AlertDialog.Builder(this).setMessage(ExceptionUtils.getFullStackTrace(e)).setPositiveButton("Скопировать", {di, i ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("tracktrace", ExceptionUtils.getFullStackTrace(e))
                clipboard.primaryClip = clip
            }).create().show()
        }
        errorView.text = "При загрузке приложения произошла ошибка.\nПопробуйте перезапустить приложение, если ошибка появится и впредь, то отпрвьте ~этот текст~ в раздел Репортов в HokeyApp"
        errorView.visibility = View.VISIBLE
        progressBar.isIndeterminate = false
        progressBar.progress = 100
        e.printStackTrace()
    }

    private fun loadFile(file: File): String {
        return file.readText()
    }

    private fun makeFile(file: File): String {
        file.createNewFile()
        val json = JsonObject()
        json.addProperty("lastQuote", 1)
        json.addProperty("lastTab", 0)
        json.add("favorites", JsonArray())
        val text = GsonBuilder().setPrettyPrinting().create().toJson(json)
        file.writeText(text)
        return text
    }

}