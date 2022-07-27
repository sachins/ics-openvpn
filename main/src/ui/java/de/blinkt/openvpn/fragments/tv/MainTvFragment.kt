/*
 * Copyright (c) 2012-2022 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.fragments.tv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import de.blinkt.openvpn.R
import de.blinkt.openvpn.viewmodel.BrowseViewModel

private const val HEADER_ID_1: Long = 1
private const val HEADER_NAME_1 = "Page Fragment"
private const val HEADER_ID_2: Long = 2
private const val HEADER_NAME_2 = "Rows Fragment"
private const val HEADER_ID_3: Long = 3
private const val HEADER_NAME_3 = "Settings Fragment"
private const val HEADER_ID_4: Long = 4
private const val HEADER_NAME_4 = "User agreement Fragment"

class MainTvFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private lateinit var viewModel: BrowseViewModel
    private lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUiElements()
        loadData()
        backgroundManager = BackgroundManager.getInstance(requireActivity()).apply {
            if (!isAttached) {
                attach(requireActivity().window)
            }
        }
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageRowFragmentFactory(backgroundManager))
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
            adapter = BrowseAdapter(requireContext(), viewModel.browseContent.value ?: emptyList())
        }
        /*view?.postDelayed({
            createRows()
        }, 2000)*/
    }

    private fun createRows() {
        val headerItem1 = HeaderItem(HEADER_ID_1, HEADER_NAME_1)
        val pageRow1 = PageRow(headerItem1)
        rowsAdapter.add(pageRow1)

        val headerItem2 = HeaderItem(HEADER_ID_2, HEADER_NAME_2)
        val pageRow2 = PageRow(headerItem2)
        rowsAdapter.add(pageRow2)

        val headerItem3 = HeaderItem(HEADER_ID_3, HEADER_NAME_3)
        val pageRow3 = PageRow(headerItem3)
        rowsAdapter.add(pageRow3)

        val headerItem4 = HeaderItem(HEADER_ID_4, HEADER_NAME_4)
        val pageRow4 = PageRow(headerItem4)
        rowsAdapter.add(pageRow4)
    }

    private class PageRowFragmentFactory(private val backgroundManager: BackgroundManager) : FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any?): Fragment {
            val row = rowObj as PageRow
            backgroundManager.drawable = null
            return SampleFragmentA()
        }
    }

    class SampleFragmentA : RowsSupportFragment() {

    }
}