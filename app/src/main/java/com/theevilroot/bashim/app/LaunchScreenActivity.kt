package com.theevilroot.bashim.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import kotlin.concurrent.thread

class LaunchScreenActivity: AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var app: SimpleBash
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launch_screen)

        progressBar = findViewById(R.id.progressBar)
        app = application as SimpleBash

        thread(start = true, block = {
            Thread.sleep(4000)
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
                    app.lastTab = data.get("lastTab").asInt
                    app.lastQuote = data.get("lastQuote").asInt
                    app.favorites = ArrayList(data.get("favorites").asJsonArray.map { it.asJsonObject }.map { Quote(it.get("id").asString, it.get("rate").asString, it.get("content").asString, it.get("url").asString) })
                    runOnUiThread { startActivity(Intent(this, QuotesActivity::class.java)); finish() }
                }
            }catch (e: Exception) {
                runOnUiThread { showError(e) }
            }
        })

    }

    private fun showError(e: Exception) {
        Toast.makeText(this, "Exception: ${e.javaClass}", Toast.LENGTH_SHORT).show()
    }

    private fun loadFile(file: File): String {
        return file.readText()
    }

    private fun makeFile(file: File): String {
        file.createNewFile()
        val json = JsonObject()
        json.addProperty("lastTab", 0)
        json.addProperty("lastQuote", 1)
        json.add("favorites", JsonArray())
        val text = GsonBuilder().setPrettyPrinting().create().toJson(json)
        file.writeText(text)
        return text
    }

}