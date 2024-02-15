package com.myproject.cloudbridge.view.intro.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.myproject.cloudbridge.databinding.ActivityStartBinding
import com.myproject.cloudbridge.util.singleton.Utils
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.viewModel.IntroViewModel

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    private val viewModel: IntroViewModel by viewModels()

    /**
     * 1. 참조 URL : https://fre2-dom.tistory.com/115
     * 2. 카카오톡 로그인 결과 처리를 위한 Callback 함수
     * 3. 로그인 결과에 따라 필요한 동작과 예외 처리를 정의
     * */
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                // 사용자가 동의 화면에서 로그인을 취소
                // 로그인 이전 또는 초기 화면으로 이동시키는 등의 조치가 필요
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                }
                // 잘못된 앱 키를 전달
                // Android SDK 초기화 시 사용한 앱 키 값이 [내 애플리케이션] > [앱 키]의 네이티브 앱 키와 일치하는지 확인
                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                }
                // 리프레시 토큰이 만료되었거나 존재하지 않습니다
                // 카카오 로그인을 통해 토큰을 새로 발급
                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                }
                // 잘못된 파라미터를 전달
                // 요청 시 전달한 파라미터 값이 올바른지 확인
                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                }
                // 잘못된 동의 항목 ID를 전달
                // scopes 파라미터에 전달된 동의 항목 ID가 올바른지 확인
                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                }
                // Android 플랫폼 설정이 올바르지 않습니다.
                // [내 애플리케이션] > [플랫폼] > [Android]의 패키지명, [카카오 로그인] 사용 여부 등 앱 설정이 올바른지 확인
                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                }
                // 일시적인 서버 오류가 발생
                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                }
                // 요청 권한이 없는 앱입니다
                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                }
                // 에러 로그를 보고 문제를 파악
                else -> { // Unknown
                    Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (token != null) {
            val intent = Intent(this, SignOrLoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kakao_login_button = binding.LoginKBtn

        viewModel.checkFistFlag()

        viewModel.first.observe(this){first->
            if(first) {
                // 1. 처음 접속하는 유저가 아니면
                // 2. 카카오 SDK 초기화
                KakaoSdk.init(this, Utils.APP_KEY)

                // 3. 앱 실행 시 사용자가 앞서 로그인을 통해 발급 받은 토큰이 있는지 확인
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.d("IntroActivityLog", "토큰 정보 보기 실패")
                    }
                    // 4. 토큰이 존재한다면 MainActivity로 이동
                    else if (tokenInfo != null) {
                        Log.d("IntroActivityLog", "토큰 정보 보기 성공" +
                                "${tokenInfo.id}")

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
            } else {
                // 1. 처음 접속하는 유저이면
                // 2. 카카오 로그인 버튼을 보여주어 카카오 로그인을 유도
                binding.LoginKBtn.visibility = View.VISIBLE
            }
        }

//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)

        kakao_login_button.setOnClickListener {

            // 3. 카카오톡 실행 가능 여부를 확인
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                // 4. 카카오톡이 실행 가능하다면
                // 5. Android SDK가 카카오톡을 실행하고 사용자에게 앱 이용 관련 동의를 구하는 동의 화면을 출력
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)

                // 4. 카카오톡이 실행이 불가능하다면
            }else{
                // 5. 카카오 계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}