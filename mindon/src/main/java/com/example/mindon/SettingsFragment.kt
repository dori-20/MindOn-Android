package com.example.mindon

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

// 동적 설정 제어 연결
        val prefs = preferenceManager.sharedPreferences ?: return
        val defaultHour = prefs.getInt("reminder_hour", 21)
        val defaultMinute = prefs.getInt("reminder_minute", 0)

        val timePref = findPreference<Preference>("reminder_time")

        // 초기 visible 설정
        timePref?.summary = String.format("%02d:%02d에 알림 예정", defaultHour, defaultMinute)

        timePref?.setOnPreferenceClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                prefs.edit()
                    .putInt("reminder_hour", hour)
                    .putInt("reminder_minute", minute)
                    .apply()

                timePref.summary = String.format("%02d:%02d에 알림 예정", hour, minute)
                AlarmHelper.setDailyReminder(requireContext())

                Toast.makeText(requireContext(), "알림 시간이 설정되었습니다", Toast.LENGTH_SHORT).show()
            }, defaultHour, defaultMinute, true).show()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "daily_reminder_on") {
            val on = sharedPreferences?.getBoolean("daily_reminder_on", false) == true
            if (on) {
                AlarmHelper.setDailyReminder(requireContext())
                Toast.makeText(requireContext(), "리마인더 알림이 설정되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                AlarmHelper.cancelReminder(requireContext())
                Toast.makeText(requireContext(), "리마인더 알림이 꺼졌습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
