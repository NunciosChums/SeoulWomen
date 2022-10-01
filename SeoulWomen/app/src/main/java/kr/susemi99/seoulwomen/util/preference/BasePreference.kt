package kr.susemi99.seoulwomen.util.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.PreferencesProvider

open class BasePreference(context: Context) : KotprefModel(context, preferencesProvider) {
  companion object {
    private var encryptedSharedPreference: SharedPreferences? = null
    private val preferencesProvider = PreferencesProvider { context, name, _ ->
      encryptedSharedPreference ?: run {
        val masterKeyAlias = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
          context,
          name,
          masterKeyAlias,
          EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
          EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
      }.also {
        encryptedSharedPreference = it
      }
    }
  }
}