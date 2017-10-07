package com.byteshaft.doosra.accounts;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.byteshaft.doosra.MainActivity;
import com.byteshaft.doosra.R;
import com.byteshaft.doosra.utils.AppGlobals;

public class AccountManager extends AppCompatActivity {

    private static AccountManager sInstance;

    public static AccountManager getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppGlobals.isLogin()) {
            startActivity(new Intent(AccountManager.this, MainActivity.class));
        } else {
            setContentView(R.layout.activity_account_manager);
            loadLoginFragment(new Login());
        }
        sInstance = this;
    }

    private void loadLoginFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment, backStateName);
            fragmentTransaction.commit();
    }

    public void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment, backStateName);
        FragmentManager manager = getSupportFragmentManager();
        Log.i("TAG", backStateName);
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(getPackageName() + "accounts.AccountActivationCode");
        if (fragment instanceof AccountActivationCode) {
            Log.i("TAG", "fragment " + fragment.isVisible());

        }
        super.onBackPressed();
    }
}
