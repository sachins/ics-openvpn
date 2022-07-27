/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.activities.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import de.blinkt.openvpn.R

class TvActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)
    }
}