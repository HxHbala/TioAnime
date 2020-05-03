package com.axiel7.tioanime;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.BuildConfig;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
import org.acra.annotation.AcraNotification;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.data.StringFormat;

@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "tioanimebugs@gmail.com")
@AcraNotification(resTitle = R.string.bug_title, resText = R.string.bug_text, resChannelName = R.string.bug_notification_channel, sendOnClick = true)
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
        ACRA.init(this, builder);
    }
}
