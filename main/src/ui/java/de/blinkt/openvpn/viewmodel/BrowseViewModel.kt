/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.repository.VpnProfileRepositoryFactory

class BrowseViewModel(application: Application) : AndroidViewModel(application) {

    private val vpnProfileRepository = VpnProfileRepositoryFactory.getVpnProfileRepository(application)
    val browseContent = MutableLiveData<Collection<VpnProfile>>()

    init {
        browseContent.value = getVpnProfileList()
    }

    private fun getVpnProfileList(): Collection<VpnProfile> {
        return vpnProfileRepository.getAllProfiles()
    }

    fun reinit() {
        browseContent.value = getVpnProfileList()
    }

    fun addVpnProfile(vpnProfile: VpnProfile) {
        vpnProfileRepository.addProfile(vpnProfile)
    }

    fun findVpnProfileByName(name: String): VpnProfile? {
        return vpnProfileRepository.findProfileByName(name)
    }

    fun saveVpnProfile(vpnProfile: VpnProfile) {
        vpnProfileRepository.saveProfile(vpnProfile)
    }

    fun saveAllProfiles() {
        vpnProfileRepository.saveAllProfiles()
    }
}