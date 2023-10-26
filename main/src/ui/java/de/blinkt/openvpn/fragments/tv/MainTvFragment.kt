/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.fragments.tv

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.ViewModelProvider
import com.obsez.android.lib.filechooser.ChooserDialog
import de.blinkt.openvpn.LaunchVPN
import de.blinkt.openvpn.R
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.activities.ConfigConverter
import de.blinkt.openvpn.activities.DisconnectVPN
import de.blinkt.openvpn.activities.VPNPreferences
import de.blinkt.openvpn.core.ConnectionStatus
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.PasswordDialogFragment.Companion.newInstance
import de.blinkt.openvpn.core.VpnStatus
import de.blinkt.openvpn.viewmodel.BrowseViewModel

private const val START_VPN_CONFIG = 92
private const val IMPORT_PROFILE = 231
private const val TAG = "MainTvFragment"

class MainTvFragment : BrowseSupportFragment(), VpnStatus.StateListener {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private lateinit var viewModel: BrowseViewModel
    private lateinit var backgroundManager: BackgroundManager
    private var lastIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUiElements()
        loadData()
        backgroundManager = BackgroundManager.getInstance(requireActivity()).apply {
            if (!isAttached) {
                attach(requireActivity().window)
            }
        }
        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                null -> onAddOrDuplicateProfile(null)
                else -> startOrStopVPN(item as VpnProfile)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        VpnStatus.addStateListener(this)
    }

    override fun onPause() {
        super.onPause()
        VpnStatus.removeStateListener(this)
    }

    private fun setupUiElements() {
//        badgeDrawable = resources.getDrawable(R.mipmap.banner_tv, null)
        title = getString(R.string.app)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
    }

    private fun loadData() {
        adapter = rowsAdapter
        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]
        viewModel.browseContent.observe(this) {
            adapter = BrowseAdapter(requireContext(), viewModel.browseContent.value ?: emptyList()) {
                editVPN(it)
            }
        }
    }

    private fun onAddOrDuplicateProfile(copyProfile: VpnProfile?) {
        val context: Context? = activity
        if (context != null) {
            val entry = EditText(context)
            entry.setSingleLine()
            val dialog = AlertDialog.Builder(context)
            if (copyProfile == null) dialog.setTitle(R.string.menu_add_profile) else {
                dialog.setTitle(getString(R.string.duplicate_profile_title, copyProfile.name))
                entry.setText(getString(R.string.copy_of_profile, copyProfile.name))
            }
            dialog.setMessage(R.string.add_profile_name_prompt)
            dialog.setView(entry)
            dialog.setNeutralButton(R.string.menu_import_short) { _, _ -> startFilePicker() }
            dialog.setPositiveButton(
                android.R.string.ok
            ) { dialog12: DialogInterface?, which: Int ->
                val name = entry.text.toString()
                if (viewModel.findVpnProfileByName(name) == null) {
                    val profile: VpnProfile
                    if (copyProfile != null) {
                        profile = copyProfile.copy(name)
                        // Remove restrictions on copy profile
                        profile.mProfileCreator = null
                        profile.mUserEditable = true
                    } else {
                        profile = VpnProfile(name)
                    }
                    addProfile(profile)
                    editVPN(profile)
                } else {
                    Toast.makeText(
                        activity,
                        R.string.duplicate_profile_name,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            dialog.setNegativeButton(android.R.string.cancel, null)
            dialog.create().show()
        }
    }

    private fun addProfile(profile: VpnProfile) {
        viewModel.addVpnProfile(profile)
        viewModel.saveAllProfiles()
        viewModel.saveVpnProfile(profile)
    }

    private fun editVPN(profile: VpnProfile) {
        val vpnPrefIntent = Intent(requireActivity(), VPNPreferences::class.java)
            .putExtra(requireActivity().packageName + ".profileUUID", profile.uuid.toString())
        startActivityForResult(vpnPrefIntent, START_VPN_CONFIG)
    }

    private fun startFilePicker(): Boolean {
        ChooserDialog(requireActivity())
            .withChosenListener { dir, dirFile ->
                Toast.makeText(requireContext(), "Selected: $dir", Toast.LENGTH_LONG).show()
                startConfigImport(Uri.fromFile(dirFile))
            }
            .withOnCancelListener {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                it.cancel()
            }
            .build()
            .show()
        return true
    }

    private fun startConfigImport(uri: Uri) {
        val startImport = Intent(activity, ConfigConverter::class.java)
        startImport.action = ConfigConverter.IMPORT_PROFILE
        startImport.data = uri
        startActivityForResult(startImport, IMPORT_PROFILE)
    }

    private fun startOrStopVPN(profile: VpnProfile) {
        if (VpnStatus.isVPNActive() && profile.uuidString == VpnStatus.getLastConnectedVPNProfile()) {
            val i = lastIntent
            if (i != null) {
                startActivity(i)
            } else {
                val disconnectVPN = Intent(activity, DisconnectVPN::class.java)
                startActivity(disconnectVPN)
            }
        } else {
            startVPN(profile)
        }
    }

    private fun startVPN(profile: VpnProfile) {
        viewModel.saveVpnProfile(profile)
        val intent = Intent(activity, LaunchVPN::class.java)
        intent.putExtra(LaunchVPN.EXTRA_KEY, profile.uuid.toString())
        intent.action = Intent.ACTION_MAIN
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            IMPORT_PROFILE -> {
                val profileUUID = data?.getStringExtra(VpnProfile.EXTRA_PROFILEUUID) ?: return
                viewModel.reinit()
            }
        }
    }

    override fun updateState(
        state: String?,
        logmessage: String?,
        localizedResId: Int,
        level: ConnectionStatus,
        intent: Intent?
    ) {
        requireActivity().runOnUiThread {
            lastIntent = intent
            rowsAdapter.notifyItemRangeChanged(0, viewModel.browseContent.value?.size ?: 0)
            showUserRequestDialogIfNeeded(level, intent)
        }
    }

    private fun showUserRequestDialogIfNeeded(level: ConnectionStatus, intent: Intent?): Boolean {
        if (level == ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT) {
            if (intent?.getStringExtra(OpenVPNService.EXTRA_CHALLENGE_TXT) != null) {
                val pwInputFrag = newInstance(intent, false)
                pwInputFrag?.show(parentFragmentManager, "dialog") ?: return false
                return true
            }
        }
        return false
    }

    override fun setConnectedVPN(uuid: String?) {
        Toast.makeText(context, "Connected $uuid", Toast.LENGTH_SHORT).show()
    }
}