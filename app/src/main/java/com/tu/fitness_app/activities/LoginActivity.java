package com.tu.fitness_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.Model.Calories;
import com.tu.fitness_app.Model.Setting;
import com.tu.fitness_app.Model.User;
import com.tu.fitness_app.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 0;
    private static final String TAG = "LoginActivity";
    public static String USER_ID = "";
    public static String USER_EMAIL = "";
    public static String USER_NAME = "";
    public static int mSeries1 = 0;
    public static float mSeries2 = 0;
    public static float calRef = 0f;
    public static float user_fat = 0f;
    public static float user_carbs = 0f;
    public static float user_protein = 0f;
    public static float mode = 0f;
    public static List<String> day = new ArrayList<>();
    Date today = new Date();
    // Authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;// kiem tra user
    private DatabaseReference mDatabase; // lay database

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        for (String provider : AuthUI.SUPPORTED_PROVIDERS) {
            Log.v(this.getClass().getName(), provider);
        }

        mAuth = FirebaseAuth.getInstance(); // Tao kết nối đến user hiện tại
        mDatabase = FirebaseDatabase.getInstance().getReference(); // lấy data của thằng user hiện tại
        mAuthListener = firebaseAuth -> updateInfo();


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.fitness_logo)
                        .build(),
                RC_SIGN_IN);
    }
    private void initializeUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();

        DatabaseReference usersRef = mDatabase.child("Users");
        DatabaseReference caloriesRef = mDatabase.child("Calories");
        DatabaseReference settingRef = mDatabase.child("Setting");

        User newUser = new User("", "", "", 0, "", 0, 0, 0);
        usersRef.child(userId).setValue(newUser);

        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        Calories calories = new Calories(0, 0, 0, 0);
        caloriesRef.child(userId).child(date).setValue(calories);

        Setting setting = new Setting();
        int intSetting = Setting.SetSetting(0);
        settingRef.child(userId).setValue(intSetting);
    }

    private DatabaseReference getUsersRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser(); // gọi hàm cho user hiện tại
        String userId = user != null ? user.getUid() : null; // lấy id cho user hiện tại
        assert userId != null;
        return mDatabase.child("Users").child(userId).child(ref);
    }

    private DatabaseReference getCaloriesRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Calories").child(userId).child(date).child(ref);
    }

    private DatabaseReference getSettingRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        return mDatabase.child("Setting").child(userId).child(ref);
    }

    private DatabaseReference getWorkoutDaysRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        return mDatabase.child("WorkoutDays").child(userId).child(ref);
    }

    private void getUserInfo() {
        getUsersRef("stepgoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                mSeries1 = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getUsersRef("caloriegoal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                mSeries2 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getUsersRef("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                USER_NAME = (String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getCaloriesRef("totalcalories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    calRef = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    calRef = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getCaloriesRef("totalfat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_fat = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_fat = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getCaloriesRef("totalcarbs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_carbs = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_carbs = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getCaloriesRef("totalprotein").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_protein = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_protein = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getSettingRef("mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mode = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                } else {
                    mode = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        getWorkoutDaysRef("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String data = snapshot.getValue(String.class);
                        day.add(data);
                    }
                } else {
                    day = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
            USER_EMAIL = user.getEmail();
            // Picasso.with(ActivityFUIAuth.this).load(user.getPhotoUrl()).into(imgProfile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                assert response != null;
                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
                IdpResponse idpResponse = data.getParcelableExtra("extra_idp_response");
                assert idpResponse != null;
                boolean isNew = idpResponse.isNewUser();

                if (isNew) {
                //  Launch app intro
                    Intent i = new Intent(LoginActivity.this, AppIntroActivity.class);
                    startActivity(i);
                    initializeUserInfo();
                } else {
                    getUserInfo();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                }
                updateInfo();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Check network connection and try again", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, "Unexpected Error, we are trying to resolve the issue. Please check back soon", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }
}
