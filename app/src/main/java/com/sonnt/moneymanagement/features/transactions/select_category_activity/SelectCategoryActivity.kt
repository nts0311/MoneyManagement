package com.sonnt.moneymanagement.features.transactions.select_category_activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sonnt.moneymanagement.R
import com.sonnt.moneymanagement.constant.Constants
import com.sonnt.moneymanagement.data.entities.Category
import kotlinx.android.synthetic.main.activity_select_category.*

const val RESULT_CATEGORY_ID = "result_category_id"


class SelectCategoryActivity : AppCompatActivity() {

    companion object{
        const val ARG_ADD_ALL_CATEGORY = "add_all_category_type"
    }

    private var viewPagerAdapter: CategorySelectStateAdapter? = null
    private var addAllCategory = false

    //when clicking on a category, return it's id as the result
    var categoryClickListener: (category: Category) -> Unit = { category ->
        val returnIntent = Intent()
        returnIntent.putExtra(RESULT_CATEGORY_ID, category.id)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)

        if(intent.hasExtra(ARG_ADD_ALL_CATEGORY))
            addAllCategory = intent.getBooleanExtra(ARG_ADD_ALL_CATEGORY, false)

        setSupportActionBar(select_category_toolbar)

        setupViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.select_category_menu, menu)

        val searchItem = menu?.findItem(R.id.category_search_item)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val viewModelKey =
                    if (category_type_tab.selectedTabPosition == 0) Constants.TYPE_EXPENSE
                    else Constants.TYPE_INCOME

                val currentFragmentVM = ViewModelProvider(this@SelectCategoryActivity)
                    .get(viewModelKey, CategorySelectFragViewModel::class.java)

                if (newText != null)
                    currentFragmentVM.setSearchQuery(newText)

                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupViewPager() {
        viewPagerAdapter = CategorySelectStateAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter?.categoryClickListener = categoryClickListener
        category_viewpager.adapter = viewPagerAdapter
        viewPagerAdapter?.addAllCategory = this.addAllCategory

        category_type_tab.tabMode = TabLayout.MODE_FIXED

        TabLayoutMediator(category_type_tab, category_viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = Constants.TYPE_EXPENSE
                1 -> tab.text = Constants.TYPE_INCOME
            }
        }.attach()
    }
}