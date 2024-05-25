package com.topjohnwu.magisk.ui.home

import android.os.Build
import android.os.PowerManager
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.content.getSystemService
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.core.Config
import com.topjohnwu.magisk.core.Const
import com.topjohnwu.magisk.core.base.BaseActivity
import com.topjohnwu.magisk.core.ktx.reboot as systemReboot

object RebootMenu {

    private fun reboot(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reboot_normal -> systemReboot()
            R.id.action_reboot_userspace -> systemReboot("userspace")
            R.id.action_reboot_bootloader -> systemReboot("bootloader")
            R.id.action_reboot_download -> systemReboot("download")
            R.id.action_reboot_edl -> systemReboot("edl")
            R.id.action_reboot_recovery -> systemReboot("recovery")
            R.id.action_reboot_safe_mode -> {
                val status = !item.isChecked
                item.isChecked = status
                Config.bootloop = if (status) 2 else 0
            }
            else -> Unit
        }
        return true
    }

    fun inflate(activity: BaseActivity): PopupMenu {
        val themeWrapper = ContextThemeWrapper(activity, R.style.Foundation_PopupMenu)
        val menu = PopupMenu(themeWrapper, activity.findViewById(R.id.action_reboot))
        activity.menuInflater.inflate(R.menu.menu_reboot, menu.menu)
        menu.setOnMenuItemClickListener(RebootMenu::reboot)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            activity.getSystemService<PowerManager>()?.isRebootingUserspaceSupported == true) {
            menu.menu.findItem(R.id.action_reboot_userspace).isVisible = true
        }
        if (Const.Version.isCanary()) {
            menu.menu.findItem(R.id.action_reboot_safe_mode).isChecked = Config.bootloop >= 2
        } else {
            menu.menu.findItem(R.id.action_reboot_safe_mode).isVisible = false
        }
        return menu
    }

}
