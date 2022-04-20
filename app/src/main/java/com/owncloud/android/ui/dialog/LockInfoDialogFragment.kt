/*
 * Nextcloud Android client application
 *
 * @author Álvaro Brey Vilas
 * Copyright (C) 2022 Álvaro Brey Vilas
 * Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.owncloud.android.ui.dialog

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.StyleSpan
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nextcloud.client.account.User
import com.owncloud.android.R
import com.owncloud.android.databinding.LockInfoDialogBinding
import com.owncloud.android.datamodel.OCFile
import com.owncloud.android.utils.DisplayUtils
import com.owncloud.android.utils.theme.ThemeButtonUtils

/**
 * Dialog that shows lock information for a locked file
 */
class LockInfoDialogFragment(private val user: User, private val file: OCFile) :
    DialogFragment(),
    DisplayUtils.AvatarGenerationListener {
    private lateinit var binding: LockInfoDialogBinding

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val alertDialog = it as AlertDialog
            ThemeButtonUtils.themeBorderlessButton(alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout for the dialog
        val inflater = requireActivity().layoutInflater
        binding = LockInfoDialogBinding.inflate(inflater, null, false)

        val view = binding.root

        // Setup layout
        val username = file.lockOwnerDisplayName ?: file.lockOwnerId
        val usernameText = DisplayUtils.createTextWithSpan(
            getString(R.string.locked_by, username),
            username,
            StyleSpan(android.graphics.Typeface.BOLD)
        )
        binding.lockedByUsername.text = usernameText

        DisplayUtils.setAvatar(
            user,
            file.lockOwnerId!!,
            file.lockOwnerDisplayName,
            this,
            requireContext().resources.getDimension(R.dimen.list_item_avatar_icon_radius),
            resources,
            binding.lockedByAvatar,
            context
        )

        // Build the dialog
        val dialog = MaterialAlertDialogBuilder(requireActivity(), R.style.Theme_ownCloud_Dialog)
            .setTitle(R.string.file_lock_dialog_title)
            .setView(view)
            .setNeutralButton(R.string.dismiss) { _, _ ->
                dismiss()
            }
            .create()

        return dialog
    }

    override fun avatarGenerated(avatarDrawable: Drawable?, callContext: Any) {
        (callContext as ImageView).setImageDrawable(avatarDrawable)
    }

    override fun shouldCallGeneratedCallback(tag: String, callContext: Any): Boolean {
        return (callContext as ImageView).tag == tag
    }
}
