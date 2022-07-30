/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.fragments.tv

import android.content.Context
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import de.blinkt.openvpn.R
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.presenter.VpnProfileCardPresenter

class BrowseAdapter(context: Context, vpnProfiles: Collection<VpnProfile>) : ArrayObjectAdapter(ListRowPresenter()) {
    init {
        addVpnProfiles(context, vpnProfiles)
    }

    private fun addVpnProfiles(context: Context, vpnProfiles: Collection<VpnProfile>) {
        val cardPresenter = VpnProfileCardPresenter(context)
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        listRowAdapter.addAll(0, vpnProfiles)
        listRowAdapter.add(null) // To put add profile cell
        val headerItem = HeaderItem(context.getString(R.string.vpn_list_title))
        add(ListRow(headerItem, listRowAdapter))
    }
}