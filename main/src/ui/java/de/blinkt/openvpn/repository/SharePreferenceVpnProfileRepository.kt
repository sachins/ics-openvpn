/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.repository

import android.app.Application
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ProfileManager

class SharePreferenceVpnProfileRepository(override val application: Application) : VpnProfileRepository {

    private val profileManager = ProfileManager.getInstance(application)

    override fun getAllProfiles(): Collection<VpnProfile> {
        return profileManager.profiles
    }
}