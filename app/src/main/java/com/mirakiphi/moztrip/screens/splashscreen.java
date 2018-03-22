package com.mirakiphi.moztrip.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.mirakiphi.moztrip.R;

public class splashscreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        final RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(isOnline())
                {Intent i = new Intent(splashscreen.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();}
                else
                {
                    final Snackbar snackbar = Snackbar
                            .make(relativeLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ProgressDialog checkNetDialog = ProgressDialog.show(splashscreen.this,"Checking", "Please wait...",false,false);

                            if(isOnline()==true)
                            {
                    /*
                    * if the user is now online ,
                    * stop the progress dialog,
                    * stop this activity and
                     * return to mainActivity */

                                checkNetDialog.dismiss();
                                Intent returnIntent=new Intent(splashscreen.this,MainActivity.class);
                                startActivity(returnIntent);
                                splashscreen.this.finish();

                            }
                            else
                            {   //otherwise ,dismiss the progress dialog after 2 seconds so that user can retry
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkNetDialog.dismiss();
                                        snackbar.show();
                                    }
                                }, 2000);
                            }
                        }


                    });

                    snackbar.show();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    public boolean isOnline(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        boolean result=(networkInfo!=null&&networkInfo.isConnected());

        return result;
    }


}