package ir.iribcs.sso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

//Yaghoob Siahmargooei Was Here
public class MainActivity extends AppCompatActivity {

    int RC_SIGN_IN=2222;
    String TAG="4Yaghoob";
    String Token="";
    Button btn_SignOut,btn_fromServer;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_SignOut=(Button) findViewById(R.id.main_btn_signout);
        btn_fromServer=(Button) findViewById(R.id.main_btn_fromserver);

        if (btn_SignOut!=null)
            btn_SignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                }
            });

        if (btn_fromServer!=null)
            btn_fromServer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetFromServer();
                }
            });

        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Token=account.getIdToken();
            UpdateUIviaLocalAccount(account);
            btn_fromServer.setVisibility(View.VISIBLE);
            btn_SignOut.setVisibility(View.VISIBLE);
        } else {
            Token="";
            Log.d(TAG, "{MAIN} Not logged Near Time! Yet!");
            RollBackToLogin();
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG,"{MAIN} SignOut Successfully!");
                        Token="";
                        RollBackToLogin();
                    }
                });
    }
    // [END signOut]

    private void GetFromServer() {
        if(!Token.equals(""))
        {
            new INIT.OkHttp_API_GetUserInfo(getBaseContext(), new OnAsyncTaskListener() {
                @Override
                public void onSuccess(String result) {
                    Log.i(TAG,"{MAIN} AsyncTask OKKKKKKKKKKKK "+ result);
                    UpdateUIviaServerAccount(result);
                }

                @Override
                public void onFailure(String error) {
                    Log.i(TAG,"{MAIN} AsyncTask ERROR " + error);
                    //I use this text container just for displaying ERROR! Ok?
                    TextView tv_data1=(TextView) findViewById(R.id.main_tv_SERVERAdditionData1);
                    TextView tv_data2=(TextView) findViewById(R.id.main_tv_SERVERAdditionData2);
                    tv_data1.setText("خطا در دریافت اطلاعات از سرور \n لطفا اینترنت دستگاه خود را بررسی نمایید.");
                    tv_data2.setText(error);
                }
            }).execute(INIT.SERVER_API_URL,Token);
        }
    }

    private void UpdateUIviaLocalAccount(GoogleSignInAccount acc)
    {
        ImageView IV_avatar = (ImageView) findViewById(R.id.main_avatar);
        TextView tv_email=(TextView) findViewById(R.id.main_tv_email);
        TextView tv_dispname=(TextView) findViewById(R.id.main_tv_dispname);
        TextView tv_name=(TextView) findViewById(R.id.main_tv_name);
        TextView tv_family=(TextView) findViewById(R.id.main_tv_family);

        tv_email.setText(acc.getEmail());
        tv_dispname.setText(acc.getDisplayName());
        tv_name.setText(acc.getGivenName());
        tv_family.setText(acc.getFamilyName());
        Picasso.with(getBaseContext()).load(acc.getPhotoUrl()).into(IV_avatar);
    }

    private void UpdateUIviaServerAccount(String acc)
    {
        try {
            JSONObject JsonAcc=new JSONObject(acc);

            ImageView IV_SERVERavatar = (ImageView) findViewById(R.id.main_SERVERavatar);
            TextView tv_SERVERemail=(TextView) findViewById(R.id.main_tv_SERVERemail);
            TextView tv_SERVERdispname=(TextView) findViewById(R.id.main_tv_SERVERdispname);
            TextView tv_SERVERname=(TextView) findViewById(R.id.main_tv_SERVERname);
            TextView tv_SERVERfamily=(TextView) findViewById(R.id.main_tv_SERVERfamily);
            TextView tv_data1=(TextView) findViewById(R.id.main_tv_SERVERAdditionData1);
            TextView tv_data2=(TextView) findViewById(R.id.main_tv_SERVERAdditionData2);

            tv_SERVERemail.setText(JsonAcc.getString("email"));
            tv_SERVERdispname.setText(JsonAcc.getString("name"));
            tv_SERVERname.setText(JsonAcc.getString("given_name"));
            tv_SERVERfamily.setText(JsonAcc.getString("family_name"));
            tv_data1.setText(JsonAcc.getString("data1"));
            tv_data2.setText(JsonAcc.getString("data2"));
            Picasso.with(getBaseContext()).load(JsonAcc.getString("picture")).into(IV_SERVERavatar);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void RollBackToLogin()
    {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}

