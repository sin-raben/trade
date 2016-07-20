package pro.gofman.trade;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by roman on 21.07.16.
 */

public class ItemsTabPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public ItemsTabPagerAdapter(FragmentManager fm) {
        super(fm);

        tabs = new String[] {
                "Номенклатура",
                "Прайс-листы",
                "Фотография"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
