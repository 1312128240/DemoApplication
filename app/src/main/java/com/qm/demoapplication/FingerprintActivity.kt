package com.qm.demoapplication

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import kotlinx.android.synthetic.main.activity_fingerprint.*
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class FingerprintActivity : AppCompatActivity() {

    private var fingerprintManager: FingerprintManagerCompat?=null  //6.0使用
    private var mBiometricPrompt: BiometricPrompt?=null
    private val DEFAULT_KEY_NAME = "ABC123456"
    private var DEFAULT_KEYSTORE_NAME="AndroidKeyStore"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)
        fingerprintManager= FingerprintManagerCompat.from(this)
        initView()
    }



    private fun initView(){

        btn_check_fingerprinted.setOnClickListener {
            //检测是否有指纹识别功能
            val hasFingerprint=fingerprintManager?.isHardwareDetected?:false
            if(!hasFingerprint){
                Toast.makeText(this.applicationContext, "不支持指纹识别功能", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //检测系统中是否已经录入指纹
            val hasOneFingerprint=fingerprintManager?.hasEnrolledFingerprints()?:false
            if (!hasOneFingerprint){
//                Toast.makeText(this,"系统中是否添加至少一个指纹",Toast.LENGTH_SHORT).show()
                showGoSettingDialog()
                return@setOnClickListener
            }

            if (Build.VERSION_CODES.P<=Build.VERSION.SDK_INT){
                 checkFingerprint1()
            }else{
                checkFingerprint2()
            }
        }
    }


    @TargetApi(29)
    private fun checkFingerprint1(){
        mBiometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("指纹验证Demo")
                .setDescription("获取系统指纹用于登录")
                .setSubtitle("指纹登录")
                //.setConfirmationRequired(true)
                .setNegativeButton("取消", mainExecutor, { dialogInterface, i ->
                    Toast.makeText(this@FingerprintActivity, "取消", Toast.LENGTH_SHORT).show()
                })
                .build()
        val mCancellationSignal = CancellationSignal()
        mCancellationSignal.setOnCancelListener(object : CancellationSignal.OnCancelListener {
            override fun onCancel() {
                println("指纹 cancel")
            }
        })

        mBiometricPrompt?.authenticate(mCancellationSignal, mainExecutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                println("指纹 AuthenticationSucceeded")
                Toast.makeText(this@FingerprintActivity, "验证成功", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@FingerprintActivity, "验证Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@FingerprintActivity, "验证Error", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                //在认证期间遇到可恢复的错误时调用。
                Toast.makeText(this@FingerprintActivity, "验证Help", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /*
     *
     *对称加密
     *
     */
    @TargetApi(23)
    private fun checkFingerprint2(){
        try {

            //1,新建一个KeyStore密钥库存放密钥：
            val keyStore= KeyStore.getInstance(DEFAULT_KEYSTORE_NAME)
            keyStore.load(null)

            //2,获取秘钥生成器，用于生成秘钥
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, DEFAULT_KEYSTORE_NAME)
            val builder = KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()

            //3,通过密钥初始化Cipher对象，生成加密对象CryptoObject;
            val key= keyStore.getKey(DEFAULT_KEY_NAME, null) as SecretKey
            val cipher= Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val cryptoObject =FingerprintManagerCompat.CryptoObject(cipher);

            //4,调用authenticate() 方法启动指纹传感器并开始监听。
            val mCancellationSignal = androidx.core.os.CancellationSignal()
            fingerprintManager?.authenticate(cryptoObject, 0, mCancellationSignal, object : FingerprintManagerCompat.AuthenticationCallback() {
                override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errMsgId, errString)
                    println("指纹-->onAuthenticationError-->${errString}")
                }


                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    println("指纹-->onAuthenticationFailed")
                }

                override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpMsgId, helpString)
                    println("指纹-->onAuthenticationHelp-->${helpString}")
                }

                override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    println("指纹-->onAuthenticationSucceeded")

                }
            }, null)

        } catch (e: Exception) {
            println("指纹检测异常-->${e}")
        }
    }



    private fun showGoSettingDialog(){
        AlertDialog.Builder(this)
                .setTitle("需要录制指纹")
                .setNegativeButton("取消", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }

                })
                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
//                        val pcgName = "com.android.settings";
//                        val clsName = "com.android.settings.Settings";
//                        val intent = Intent();
//                        val componentName = ComponentName(pcgName, clsName);
//                        intent.setAction(Intent.ACTION_VIEW);
//                        startActivity(intent);

                        val intent = Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);

                    }

                }).show()
    }
}
