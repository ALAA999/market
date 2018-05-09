package com.example.acer.zebdashop;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static ArrayList<discount_class> arrayList_discount;
    public static ArrayList<product_class> arrayList_product;
    boolean isEmp = false;
    private Toolbar toolbar;
    boolean open = true;
    private TabLayout tabLayout;
    Animation train, rotate, train_out, anim2;
    private ViewPager viewPager;
    Button setting, call, tools, messege;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            my_settings();
        }
        if (item.getItemId() == R.id.call_us) {
            call_us();
        }
        return super.onOptionsItemSelected(item);
    }

    public void my_settings() {
        final Dialog dilog = new Dialog(this);
        dilog.setContentView(R.layout.settings_dialog);
        dilog.setCancelable(true);
        dilog.setTitle("الإعدادت");
        dilog.show();
        final Switch aSwitch = dilog.findViewById(R.id.notes_switch);
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    editor.putBoolean("notHasNotification", false);
                } else {
                    editor.putBoolean("notHasNotification", true);
                }
                editor.commit();
            }
        });
        dilog.findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_msg();
            }
        });
        dilog.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_msg();
            }
        });
        if (!preferences.getBoolean("notHasNotification", false)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
    }

    public void call_us() {
        Intent Call = new Intent(Intent.ACTION_DIAL);
        Call.setData(Uri.parse("tel:0599367408"));
        if (Call.resolveActivity(getPackageManager()) != null) {
            startActivity(Call);
        }
    }

    public void send_msg() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, "alaa07996@gmail.com");
        intent.setType("plain/text");
        startActivity(Intent.createChooser(intent, "مشاركة التطبيق"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = findViewById(R.id.setting);
        tools = findViewById(R.id.tools);
        messege = findViewById(R.id.messege);
        call = findViewById(R.id.call);
        train = AnimationUtils.loadAnimation(this, R.anim.train);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.anim2);
        train_out = AnimationUtils.loadAnimation(this, R.anim.train_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.anim);
        setting.bringToFront();
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (open) {
                    open = false;
                    setting.startAnimation(rotate);
                    tools.startAnimation(train);
                    call.startAnimation(train);
                    messege.startAnimation(train);
                    tools.setVisibility(View.VISIBLE);
                    call.setVisibility(View.VISIBLE);
                    messege.setVisibility(View.VISIBLE);
                    tools.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            my_settings();
                        }
                    });
                    messege.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            send_msg();
                        }
                    });
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            call_us();
                        }
                    });
                } else {
                    open = true;
                    setting.startAnimation(anim2);
                    tools.startAnimation(train_out);
                    call.startAnimation(train_out);
                    messege.startAnimation(train_out);
                    tools.setVisibility(View.INVISIBLE);
                    call.setVisibility(View.INVISIBLE);
                    messege.setVisibility(View.INVISIBLE);
                }
            }
        });
        arrayList_discount = new ArrayList<>();
        arrayList_product = new ArrayList<>();
        preferences = getSharedPreferences("Data", MODE_PRIVATE);
        editor = preferences.edit();
        isEmp = preferences.getBoolean("IsEmp", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); It adds back click

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.prices,
                R.drawable.discounts,
                R.drawable.ic_person_black_24dp
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Prices(), "الأسعار");
        adapter.addFrag(new discounts(), "العروض");
        if (isEmp) {
            adapter.addFrag(new product_options(), "موظفين");
        } else {
            adapter.addFrag(new emp(), "موظفين");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
