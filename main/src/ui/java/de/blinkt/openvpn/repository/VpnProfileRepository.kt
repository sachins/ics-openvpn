/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.repository

import android.app.Application
import de.blinkt.openvpn.VpnProfile

interface VpnProfileRepository {
    val application: Application

    fun getAllProfiles(): Collection<VpnProfile>

    fun addProfile(vpnProfile: VpnProfile)

    fun findProfileByName(name: String): VpnProfile?

    fun saveProfile(vpnProfile: VpnProfile)

    fun saveAllProfiles()
}