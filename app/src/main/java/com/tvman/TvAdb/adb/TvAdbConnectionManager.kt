package com.tvman.TvAdb.adb

import android.content.Context
import android.os.Build
import android.util.Log
import io.github.muntashirakon.adb.AbsAdbConnectionManager
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Date

class TvAdbConnectionManager private constructor(context: Context) : AbsAdbConnectionManager() {

    private val filesDir = context.applicationContext.filesDir
    private val privateKey: PrivateKey
    private val certificate: Certificate

    init {
        // Make sure BouncyCastle is available
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }

        // Required for Wireless Debugging TLS pairing
        try {
            if (Security.getProvider("Conscrypt") == null) {
                Security.insertProviderAt(org.conscrypt.Conscrypt.newProvider(), 1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to install Conscrypt", e)
        }

        setApi(Build.VERSION.SDK_INT)

        val loaded = loadKeyAndCert()
        if (loaded != null) {
            privateKey = loaded.first
            certificate = loaded.second
            Log.i(TAG, "Loaded existing ADB key + cert")
        } else {
            val generated = generateKeyAndCert()
            privateKey = generated.first
            certificate = generated.second
            saveKeyAndCert(privateKey, certificate)
            Log.i(TAG, "Generated new ADB key + cert")
        }
    }

    override fun getPrivateKey(): PrivateKey = privateKey
    override fun getCertificate(): Certificate = certificate
    override fun getDeviceName(): String = "TvAdb"

    private fun keyFile() = File(filesDir, "adb_private.key")
    private fun certFile() = File(filesDir, "adb_cert.der")

    private fun loadKeyAndCert(): Pair<PrivateKey, Certificate>? {
        return try {
            if (!keyFile().exists() || !certFile().exists()) return null
            val key = KeyFactory.getInstance("RSA")
                .generatePrivate(PKCS8EncodedKeySpec(keyFile().readBytes()))
            val cert = FileInputStream(certFile()).use {
                CertificateFactory.getInstance("X.509").generateCertificate(it)
            }
            key to cert
        } catch (e: Exception) {
            Log.w(TAG, "Could not load key/cert", e)
            null
        }
    }

    private fun saveKeyAndCert(key: PrivateKey, cert: Certificate) {
        try {
            keyFile().writeBytes(key.encoded)
            FileOutputStream(certFile()).use { it.write(cert.encoded) }
        } catch (e: Exception) {
            Log.e(TAG, "Could not save key/cert", e)
        }
    }

    private fun generateKeyAndCert(): Pair<PrivateKey, X509Certificate> {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048, SecureRandom())
        val kp = kpg.generateKeyPair()

        val now = System.currentTimeMillis()
        val notBefore = Date(now - 24L * 60 * 60 * 1000)
        val notAfter = Date(now + 10L * 365 * 24 * 60 * 60 * 1000)

        val builder = X509v3CertificateBuilder(
            X500Name("CN=TvAdb"),
            BigInteger(64, SecureRandom()),
            notBefore,
            notAfter,
            X500Name("CN=TvAdb"),
            SubjectPublicKeyInfo.getInstance(kp.public.encoded)
        )

        val signer = JcaContentSignerBuilder("SHA256WithRSA").build(kp.private)
        val holder = builder.build(signer)
        val cert = JcaX509CertificateConverter().getCertificate(holder)

        return kp.private to cert
    }

    companion object {
        private const val TAG = "TvAdbConnectionManager"

        @Volatile private var instance: TvAdbConnectionManager? = null

        @Throws(Exception::class)
        fun getInstance(context: Context): AbsAdbConnectionManager {
            return instance ?: synchronized(this) {
                instance ?: TvAdbConnectionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
