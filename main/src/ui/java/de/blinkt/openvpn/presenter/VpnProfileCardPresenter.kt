/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.presenter

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.ImageCardView
import de.blinkt.openvpn.R
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.VpnStatus
import de.blinkt.openvpn.fragments.Utils.getWarningText
import de.blinkt.openvpn.fragments.tv.OnLongPress

class VpnProfileCardPresenter(context: Context, private val longPressListener: OnLongPress? = null) :
    AbstractCardPresenter<ImageCardView, VpnProfile?>(context) {
    override fun onCreateView(): ImageCardView {
        val imageCardView = LayoutInflater.from(context)
            .inflate(R.layout.presenter_vpn_profile_card, null, false) as ImageCardView
        val resources = context.resources
        imageCardView.setMainImageDimensions(
            resources.getDimensionPixelSize(R.dimen.image_card_width),
            resources.getDimensionPixelSize(R.dimen.image_card_height)
        )
        return imageCardView
    }

    override fun onBindViewHolder(vpnProfile: VpnProfile?, imageCardView: ImageCardView) {
        if (vpnProfile == null) {
            imageCardView.titleText = context.getText(R.string.menu_add_profile)
            imageCardView.mainImage = context.getDrawable(R.drawable.ic_baseline_add_circle_outline_24)
            imageCardView.setOnLongClickListener(null)
        } else {
            imageCardView.titleText = vpnProfile.name
            val warningText = getWarningText(context, vpnProfile)
            if (vpnProfile.uuidString == VpnStatus.getLastConnectedVPNProfile()) {
                imageCardView.contentText = VpnStatus.getLastCleanLogMessage(imageCardView.context)
            } else {
                imageCardView.contentText = warningText
            }
            imageCardView.mainImage = context.getDrawable(R.drawable.ic_stat_vpn)
            imageCardView.setOnLongClickListener {
                longPressListener?.invoke(vpnProfile)
                true
            }
        }
    }

    override fun onUnbindViewHolder(imageCardView: ImageCardView) {
        imageCardView.titleText = null
        imageCardView.contentText = null
        imageCardView.mainImage = null
        imageCardView.setOnLongClickListener(null)
    }

}