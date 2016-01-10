package com.dreissig7.annicounter;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, MainActivity.PREFS);
        addHelper(MainActivity.PREFS_BACKUP_KEY, helper);
    }
}