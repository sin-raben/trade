package pro.gofman.trade.Docs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import pro.gofman.trade.R;

public class DocActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.worker);
        toolbar.setTitle(R.string.title_activity_docs);

        ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter2 pa = new PagerAdapter2( getSupportFragmentManager() );
        pa.addFragment( DocsFragment.newInstance(" ", " "), "Шапка" );
        pa.addFragment( DocsFragment.newInstance(" ", " "), "Товары" );
        vp.setAdapter( pa );

        TabLayout tl = (TabLayout) findViewById(R.id.tabLayout);
        tl.setupWithViewPager(vp);

    }





    static class PagerAdapter2 extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter2(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

}
