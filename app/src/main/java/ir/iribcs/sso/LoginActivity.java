package ir.iribcs.sso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


//Yaghoob Siahmargooei Was Here
public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN=1111;
    String TAG="4Yaghoob";
    String Token="";
    Button btn_SignIn;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    TextView TV_Wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login Page");
        setContentView(R.layout.activity_login);

        //Define UI components!
        btn_SignIn=(Button) findViewById(R.id.login_btn_signin);
        TV_Wait=(TextView) findViewById(R.id.login_tv_wait);
        TV_Wait.setVisibility(View.VISIBLE);

        //Buttons Listener!
        if (btn_SignIn!=null)
            btn_SignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //If Click SignInWithGoogle! We See GOOGLE Account dialog Intent!
                    signIn();
                }
            });

        //Try to Get Latest Account Info (IF You Close the app recently!)
        account = GoogleSignIn.getLastSignedInAccount(this);
        //Let Decide for Next Step?
        if (account != null) {
            Token=account.getIdToken();
            //Send Post Request To SERVER API if Account was exist!
            //I Use This Request for checking IS TOKEN VALID???????
            new INIT.OkHttp_API_GetUserInfo(getBaseContext(), new OnAsyncTaskListener() {
                @Override
                public void onSuccess(String result) {
                    //Yes Recent Token From Account Is VALID Yet!!!!
                    Log.i(TAG,"{LOGIN} Check AsyncTask OKKKKKKKKKKKK "+ result);
                    //Start MainActivity! Profile Page!
                    StartNextActivity();
                }

                @Override
                public void onFailure(String error) {
                    Log.i(TAG,"{LOGIN} Check AsyncTask ERROR " + error);
                    Log.d(TAG, "{LOGIN} Check ERROR ");
                    //So User Must SignIn Manually! RENEW Token I Mean!
                    btn_SignIn.setVisibility(View.VISIBLE);
                    TV_Wait.setVisibility(View.GONE);
                }
            }).execute(INIT.SERVER_API_URL,Token);

        } else {
            //Oh! I cant find any account that recently used!
            Log.d(TAG, "{LOGIN} Not logged Near Time! Yet!");
            btn_SignIn.setVisibility(View.VISIBLE);
            TV_Wait.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            Log.i(TAG,"{LOGIN} ACC. >>>>>>>>> " + account.getEmail()  + " | "  + account.getIdToken());
            // Signed in successfully, show authenticated UI.
            btn_SignIn.setVisibility(View.GONE);
            TV_Wait.setVisibility(View.VISIBLE);
            //Here We Go! Signed In Locally!
            StartNextActivity();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            TV_Wait.setVisibility(View.VISIBLE);
            TV_Wait.setText("خطا در احراز هویت با گوگل! \n لطفا دسترسی به اینترنت و نیز اطلاعات حساب گوگل خود را بررسی نمایید.\n ممکن است تنظیمات حساب گوگل را انجام نداده باشید");
            Log.i(TAG, "{LOGIN} GoogleSignInOptions  code=" + e.getStatusCode() + "   > " + e.getMessage() );
            //updateUI(null);
        }
    }
    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    private void StartNextActivity()
    {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
