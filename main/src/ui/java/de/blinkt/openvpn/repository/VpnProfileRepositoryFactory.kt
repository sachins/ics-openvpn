/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.repository

import android.app.Application

class VpnProfileRepositoryFactory private constructor() {

    companion object {
        fun getVpnProfileRepository(application: Application): VpnProfileRepository {
            return SharePreferenceVpnProfileRepository(application)
        }
    }
}