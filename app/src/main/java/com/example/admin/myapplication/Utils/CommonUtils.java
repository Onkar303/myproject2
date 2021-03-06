package com.example.admin.myapplication.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.myapplication.Adapter.CustomAdapter;
import com.example.admin.myapplication.Listners.ConfigureDarkTheme;
import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.Model.SplashModel;
import com.example.admin.myapplication.ProfileActivity;
import com.example.admin.myapplication.R;

public class CommonUtils {

    public static void myCustomAlertDialog(Context context,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              dialogInterface.dismiss();
            }
        });


        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void longPressImage (Context context,SplashModel model) {
        try {
            ConstraintSet constraintSet = new ConstraintSet();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.longpress_image, null);
            ConstraintLayout constraintLayout = (ConstraintLayout) v.findViewById(R.id.longpress_contraintlayout);
            constraintSet.clone(constraintLayout);
            constraintLayout.setBackgroundColor(Color.parseColor(model.getColor()));
            ImageView imageView = (ImageView) v.findViewById(R.id.prodifile_image_longPress);
            constraintSet.setDimensionRatio(imageView.getId(), String.valueOf(model.getWidth()) + ":" + String.valueOf(model.getHeight()));
            constraintSet.applyTo(constraintLayout);
            if(isConnected(context))
            {
                Glide.with(context).load(model.getUrls().getRegular()).into(imageView);
            }
            else
            {
                CustomToast(context, Constants.CONNECTION_LOST);
            }
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.setView(v);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setWindowAnimations(R.style.CustomTheme);
            CommonUtils.setStickyNavigationBar(dialog.getWindow());
            dialog.show();
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

    }


    public static void showPopUp(final Context context,final SplashModel splashModel,final View layout) {
        LinearLayout download, showProfile, setasbackground;
        ImageView close;
        final android.app.AlertDialog.Builder bottomSheetDialog = new android.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_view, null, false);
        showProfile = (LinearLayout) view.findViewById(R.id.showprofile_linear_layout);
        close = (ImageView) view.findViewById(R.id.bottom_dialog_close);
        download = (LinearLayout) view.findViewById(R.id.bottom_dialog_download);
        setasbackground = (LinearLayout) view.findViewById(R.id.set_as_background_linearlayout_bottomsheet);
        bottomSheetDialog.setView(view);
        final android.app.AlertDialog dialog = bottomSheetDialog.create();
        dialog.getWindow().setWindowAnimations(R.style.CustomTheme);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CommonUtils.setStickyNavigationBar(dialog.getWindow());
        dialog.show();
        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("myobject", splashModel);
                context.startActivity(i);
                dialog.dismiss();




            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkpermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (!checkpermission) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 1);
                }

                if (checkpermission) {
                    new DownlaodImage(context,splashModel,layout).execute();

                } else {

                    CustomToast(context,"Permission not granted");
                }
                dialog.dismiss();

            }
        });

        setasbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SetBackground(context,splashModel).execute();
                dialog.dismiss();
            }
        });
        //bottomSheetDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_bottom_sheet);
    }

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if(info.getState() == NetworkInfo.State.CONNECTING || info.getState() == NetworkInfo.State.CONNECTED)
        {
            return true;
        }
        return false;
    }

    public static void configureDarkTheme(Context context,boolean isDark)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.DARK_THEME,Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.IS_DARK,isDark);
        editor.apply();

    }


    public static boolean getThemePreference(Context context)
    {

        SharedPreferences preferences = context.getSharedPreferences(Constants.DARK_THEME,Context.MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(Constants.IS_DARK,false);
        return isDark;
    }


    public static void getTheme(Context context,ConfigureDarkTheme theme)
    {
        theme.isDark(getThemePreference(context));
    }


    public static void CustomToast(Context context,String toastMessage){

        LayoutInflater inflater = ((AppCompatActivity)context).getLayoutInflater();
        View customView = inflater.inflate(R.layout.customtoast,(ViewGroup)((AppCompatActivity)context).findViewById(R.id.customToast));

        TextView textView = (TextView)customView.findViewById(R.id.ToastText);
        textView.setText(toastMessage);

        CardView toastCard = (CardView)customView.findViewById(R.id.toastCard);

        if(getThemePreference(context))
        {
            toastCard.setBackgroundColor(Color.parseColor(Constants.MATERIAL_BLACK));
            textView.setTextColor(Color.parseColor(Constants.MATERIAL_GGREY));
            toastCard.setRadius(10);
        }
        else
        {
            toastCard.setBackgroundColor(Color.parseColor(Constants.MATERIAL_GGREY));
            textView.setTextColor(Color.parseColor(Constants.MATERIAL_BLACK));
            toastCard.setRadius(10);
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customView);

        toast.show();


    }

    public static void setTransLucentNavigationBar(Window window)
    {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    public static void setFullScreen(Window window)
    {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setStickyNavigationBar(Window window){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_IMMERSIVE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }


    public static void setStickyNavigatinBar(View v){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_IMMERSIVE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }



}
