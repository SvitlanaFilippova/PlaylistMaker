package com.playlistmaker.ui.presentation


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R


object IntentManager {
    fun startIntentByType(intentType: IntentType, context: Context) {
        when (intentType) {
            IntentType.SHARE -> startShareIntent(context)
            IntentType.SUPPORT -> startSupportIntent(context)
            IntentType.AGREEMENT -> startAgreementIntent(context)
        }
    }

    private fun startShareIntent(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.settings_share_message))
        }
        context.startActivity(intent)
    }

    private fun startSupportIntent(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.settings_support_mailto))
            putExtra(
                Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.settings_support_email))
            )
            putExtra(
                Intent.EXTRA_SUBJECT, context.getString(R.string.settings_support_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT, context.getString(R.string.settings_support_message)
            )
        }

        context.startActivity(intent)
    }

    private fun startAgreementIntent(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(context.getString(R.string.settings_agreement_url))
        )
        context.startActivity(intent)
    }
}

enum class IntentType {
    SHARE,
    SUPPORT,
    AGREEMENT
}
