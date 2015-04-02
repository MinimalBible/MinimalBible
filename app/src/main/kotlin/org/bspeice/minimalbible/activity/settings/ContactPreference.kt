package org.bspeice.minimalbible.activity.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.Preference
import android.util.AttributeSet
import org.bspeice.minimalbible.R

class ContactPreference(val ctx: Context, val attrs: AttributeSet)
: Preference(ctx, attrs), Preference.OnPreferenceClickListener {

    init {
        setOnPreferenceClickListener(this);
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val address = ctx.getString(R.string.contact_developer_address)
        val pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0)


        val i = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null))
        i.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.contact_developer_subject))
        i.putExtra(Intent.EXTRA_TEXT, """

Useful information for the developer:
Build code: ${pInfo.versionCode}
Build name: ${pInfo.versionName}
Android SDK: ${Build.VERSION.SDK_INT}""")
        ctx.startActivity(i)

        return true;
    }
}
