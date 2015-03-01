package bdoty.celerysplit.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import bdoty.celerysplit.R;
import bdoty.celerysplit.fragments.SettingsFragment;
import bdoty.celerysplit.models.SplitSet;
import bdoty.celerysplit.fragments.EditSplitFragment;
import bdoty.celerysplit.fragments.SplitsListFragment;
import bdoty.celerysplit.fragments.TimerFragment;
import bdoty.celerysplit.views.OptionalSwipeViewPager;
import bdoty.celerysplit.views.customtabpanel.TabPanel;

public class MainActivity extends ActionBarActivity implements SplitsListFragment.SplitFragmentListener
{
    private TimerFragment timerFrag;
    private SplitsListFragment splitsFrag;
    private EditSplitFragment splitsEditFrag;
    private OptionalSwipeViewPager viewPager;
    private TabPanel tabPanel;
    private SettingsFragment settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // VIEW PAGER SETUP
        FragmentPagerAdapter fragmentPagerAdapter = new timerPagerAdapter(getSupportFragmentManager());
        viewPager = (OptionalSwipeViewPager) findViewById(R.id.pager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(fragmentPagerAdapter);

        // FRAGMENT SETUP
        timerFrag = TimerFragment.newInstance();
        splitsEditFrag = EditSplitFragment.newInstance();
        splitsFrag = SplitsListFragment.newInstance(splitsEditFrag, this);
        settings = SettingsFragment.newInstance();


        tabPanel = (TabPanel) findViewById(R.id.tabPanel);
        tabPanel.onStart(viewPager);
    }

    /*@Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    */
    // FRAGMENT INTERACTIONS
    @Override
    public void onLoadSplits(String title, SplitSet set)
    {
        timerFrag.setLoadedSplits(set);
        timerFrag.setTitle(set.getTitle());
        viewPager.setCurrentItem(0);
        tabPanel.setPosition(0);
    }

    @Override
    public void onEditSplits(String title, SplitSet set)
    {
        splitsEditFrag.loadSplits(title, set);
    }

    // HANDLE SWITCHING FRAGMENTS
    public class timerPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Timer", "Runs", "Settings", "More"};
        public timerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return timerFrag;
                case 1:
                    return splitsFrag;
                case 2:
                    return settings;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override public int getItemPosition(Object object) {
            if (object.getClass().equals(splitsEditFrag.getClass()))return 2;
            return FragmentPagerAdapter.POSITION_NONE; }
    }
}