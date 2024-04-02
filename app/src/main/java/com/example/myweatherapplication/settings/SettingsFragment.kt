package com.example.myweatherapplication.settings

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.myweatherapplication.Const
import com.example.myweatherapplication.home.view.HomeMapsActivity
//import com.example.myweatherapplication.home.view.HomeMapsActivity
import com.example.myweatherapplication.MainActivity
import com.example.myweatherapplication.R
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var defaultPref: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val languagePreference: ListPreference = findPreference("language_name")!!
        languagePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                var language: String = newValue as String
                if (language == "auto")
                    language = Resources.getSystem().configuration.locales[0].language
                setLanguage(language)
                true
            }

        val tempPreference: ListPreference = findPreference("temperature_unit")!!
        tempPreference.setOnPreferenceChangeListener { _, tempUnit ->
            Const.tempUnit = tempUnit as String
            true
        }

        val windSpeedPreference: ListPreference = findPreference("wind_speed")!!
        windSpeedPreference.setOnPreferenceChangeListener { _, windSpeedUnit ->
            Const.windSpeedUnit = windSpeedUnit as String
            true
        }

        val LocationPreference: ListPreference = findPreference("location_choose")!!
        LocationPreference.setOnPreferenceChangeListener { _, location ->
            Const.location = location as String
            if (Const.location == "MAP"){
                val intent = Intent(requireContext(), HomeMapsActivity::class.java)
                startActivity(intent)
            }
            true
        }


    }

    private fun setLanguage(language: String) {
        Const.language = language
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        (requireActivity() as MainActivity).restart()
    }


}