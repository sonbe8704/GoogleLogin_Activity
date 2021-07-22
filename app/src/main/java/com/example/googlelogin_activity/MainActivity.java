package com.example.googlelogin_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
//https://m.blog.naver.com/luku756/221335992253
//sha1키 디버그 릴리즈 둘다 등록해야함
//파이어베이스에서 구글로그인 사용설정해야함
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google;//구글로그인ㄴ버튼
    private FirebaseAuth auth;//파이어베이스 인증객체
    private GoogleApiClient googleApiClient;//api객체
    private static final int REQ_SIGN_GOOGLE = 100;//로그인결과코드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance();

        btn_google = (SignInButton) findViewById(R.id.btn_google);//
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                Log.e("MainActivity","스타트액티비티리저트");
                startActivityForResult(intent,REQ_SIGN_GOOGLE);//절차받는 화면으로 가서 정보를 가져올것임.
        //가지러가라

            }
        });
    }
//인증요청했을때 결과받는곳
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("MainActivity","로그인");
        if(requestCode==REQ_SIGN_GOOGLE){
            Log.e("MainActivity","로그인");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);//결과값을 받는다
            Log.e("MainActivity",String.valueOf(result.getStatus()));
            if(result.isSuccess()){
                Log.e("MainActivity","로그인");
                //인증성공일때
                GoogleSignInAccount account = result.getSignInAccount();//
                resultLogin(account);//로그인 결과값출력
            }
            else{
                Log.e("MainActivity","로그인실패");
            }
        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        Log.e("MainActivity","로그인시도1");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        //계정정보 받아오고
        //로그인 확인
        auth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.e("MainActivity","로그인시도2");
                if(task.isSuccessful()){

                    //로그인 성공
                    Toast.makeText(MainActivity.this,"성공",Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getApplicationContext(),ResultActivity.class);
                    intent.putExtra("nickname",account.getDisplayName());
                    intent.putExtra("photourl",String.valueOf(account.getPhotoUrl()));//uri형태로 담을수없다.
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"실패",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}