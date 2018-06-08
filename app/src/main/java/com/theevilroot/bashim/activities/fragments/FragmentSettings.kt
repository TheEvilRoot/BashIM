package com.theevilroot.bashim.activities.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.theevilroot.bashim.R
import com.theevilroot.bashim.activities.ActivityQuotes
import com.theevilroot.bashim.activities.adapters.SettingMenuAdapter
import com.theevilroot.bashim.objects.SettingEntry
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class FragmentSettings: TheFragment() {
    override fun updateUI() {
        userSubtitle.text = "Последняя цитатка: ${activity.app.session.lastID}"
    }

    private lateinit var activity: ActivityQuotes
    private lateinit var settingsList: RecyclerView
    private lateinit var userInfo: View
    private lateinit var userTitle: TextView
    private lateinit var userPicture: CircleImageView
    private lateinit var userSubtitle: TextView
    private lateinit var settingsAdapter: SettingMenuAdapter

    lateinit var defaultEntries: List<SettingEntry>
    lateinit var exitEntry: SettingEntry

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.layout_settings_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        settingsList = view.findViewById(R.id.settings_list)
        userInfo = view.findViewById(R.id.settings_user_info)
        userTitle = view.findViewById(R.id.settings_user_title)
        userSubtitle = view.findViewById(R.id.settings_user_subtitle)
        userPicture = view.findViewById(R.id.settings_user_picture)
        settingsList.layoutManager = LinearLayoutManager(context)
        settingsAdapter = SettingMenuAdapter()
        settingsList.adapter = settingsAdapter
        defaultEntries = listOf(
                SettingEntry(R.string.favorite_quotes_title, R.drawable.favorite, true, {_,_ ->
                    Toast.makeText(context, "Favorite quotes", Toast.LENGTH_SHORT).show()
                }),
                SettingEntry(R.string.preferences_title, R.drawable.preferences, true, {_,_ ->
                    Toast.makeText(context, "Preferences", Toast.LENGTH_SHORT).show()
                }),
                SettingEntry(R.string.about_title, R.drawable.about, true, {_,_ ->
                    Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
                }))
        exitEntry = SettingEntry(R.string.logout_title, R.drawable.exit, true, {_,_ ->
            Toast.makeText(context, "Log out", Toast.LENGTH_SHORT).show()
        },{itemView, iconView, titleView,_ ->
            val scale = itemView.context.resources.displayMetrics.density
            (itemView.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0,(scale * 24).roundToInt(),0,0)
            titleView.setTextColor(Color.RED)
        })
        activity.app.session.loadLocal(activity, {localLoaded ->
            if(localLoaded) {
                activity.app.session.requestVerification {remoteLoaded, remoteVerified ->
                    if(remoteLoaded && !remoteVerified) { // Token or username is invalid
                        activity.app.session.removeLocalUser(activity)
                    }else if(!remoteLoaded) {
                        activity.app.session.isOffline = true
                    }
                    activity.runOnUiThread { initSession() }
                }
            }else{
                activity.runOnUiThread { initSession() }
            }
        })
    }

    private fun initSession() {
        settingsAdapter.setEntries(defaultEntries)
        if(activity.app.session.isOnline || activity.app.session.isOffline) {
            userTitle.text = activity.app.session.username
            userSubtitle.text = "Последняя цитатка: ${activity.app.session.lastID}"
            userPicture.colorFilter = null
            if(activity.app.session.isOnline) {
                activity.app.session.requestUserPicture {
                    activity.runOnUiThread {
                        if (it != null) {
                            userPicture.setImageBitmap(it)
                        }else{
                            userPicture.setImageDrawable(resources.getDrawable(R.drawable.user))
                            userPicture.setColorFilter(resources.getColor(R.color.colorIcon))
                        }
                    }
                }
            }else{
                userPicture.setImageDrawable(resources.getDrawable(R.drawable.user))
                userPicture.setColorFilter(resources.getColor(R.color.colorIcon))
            }
            settingsAdapter.addEntry(exitEntry)
            userInfo.setOnClickListener {
                Toast.makeText(context, "User Info", Toast.LENGTH_SHORT).show()
            }
            userInfo.setOnLongClickListener { false }
        }else{
            userTitle.text = "Войти"
            userSubtitle.text = "Зажми чтобы зарегистрироваться!"
            userPicture.setImageDrawable(resources.getDrawable(R.drawable.user))
            userPicture.setColorFilter(resources.getColor(R.color.colorIcon))
            userInfo.setOnClickListener {
                Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
                showLogin()
            }
            userInfo.setOnLongClickListener {
                Toast.makeText(context, "Register", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    fun showLogin() {
        val view = layoutInflater.inflate(R.layout.layout_login, null, false)
        val loadingView = layoutInflater.inflate(R.layout.layout_loading, null, false)
        val dialog = AlertDialog.Builder(activity).setTitle("Авторизация").setView(view).create()
        val loginUsername = view.findViewById<EditText>(R.id.login_username)
        val loginPassword = view.findViewById<EditText>(R.id.login_password)
        val loginForgetPassword = view.findViewById<Button>(R.id.login_forget_password)
        val loginError = view.findViewById<TextView>(R.id.login_error)
        val login = view.findViewById<Button>(R.id.login_button)
        loginForgetPassword.setOnClickListener {
            Toast.makeText(context, "Ну, поздравляю!", Toast.LENGTH_SHORT).show()
        }
        login.setOnClickListener {
            if(loginUsername.text.isBlank() || loginPassword.text.isBlank())
                return@setOnClickListener
            dialog.setView(loadingView)
            dialog.setCancelable(false)
            dialog.setTitle("Авторизация...")
            activity.app.session.requestAuth(loginUsername.text.toString(), loginPassword.text.toString(), {
                activity.app.session.saveUser(activity)
                activity.runOnUiThread {
                    dialog.dismiss()
                    settingsAdapter.clearEntries()
                    initSession()
                }
            }, {exception,error, errorMessage ->
                activity.runOnUiThread {
                    dialog.setCancelable(true)
                    loginError.text = if(exception == null) {
                        "$error: $errorMessage"
                    }else{
                        "${exception.javaClass.simpleName}: $error"
                    }
                    dialog.setView(view)
                }
            })
        }
        dialog.show()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser)
            updateUI()
    }

    companion object {
        fun create(activity: ActivityQuotes): FragmentSettings {
            val fragment = FragmentSettings()
            fragment.activity = activity
            return fragment
        }
    }

}