package `in`.jadu.firstandroidweb3project

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.security.Provider
import java.security.Security


class Web3Activity : AppCompatActivity() {
    private lateinit var web3: Web3j
    private lateinit var file: File
    private lateinit var walletName: String
    private lateinit var credential: Credentials
    private lateinit var txtAddress: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web3)
        txtAddress = findViewById(R.id.text_address)
        val walletPath = findViewById<EditText>(R.id.walletpath)
        val etheriumWalletPath = walletPath.text.toString()
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //connection to localhost ganache
        web3 = Web3j.build(HttpService("http://10.0.2.2:7545"))

        setupBouncyCastle();
        file = File(filesDir, etheriumWalletPath) //"/data/user/0/in.jadu.firstandroidweb3proje

        //if file donot exist create one
        if (!file.exists()) {
            file.mkdir()
        } else {
            Toast.makeText(
                applicationContext, "Directory already created",
                Toast.LENGTH_LONG
            ).show();

        }


    }

    private fun setupBouncyCastle() {
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up a provider  when it's used for the first time.
            return
        if (provider.javaClass == BouncyCastleProvider::class.java) {
            return
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }

    fun connectToEthNetwork(v: View) {
        web3 = Web3j.build(HttpService("http://10.0.2.2:7545"))
        try {
            val clientVersion = web3.web3ClientVersion().sendAsync().get() //getAsync()
            if (!clientVersion.hasError()) {
                Toast.makeText(
                    applicationContext, "Connected to Ethereum Network",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    applicationContext, "Error connecting to Ethereum Network",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("Errorweb23", e.message.toString())
            e.printStackTrace()
        }
    }

    fun createWallet(v: View) {
        val credentialPassword = findViewById<EditText>(R.id.password)
        val password = credentialPassword.text.toString()
        try {
            walletName = WalletUtils.generateLightNewWalletFile(password, file)
            Toast.makeText(
                applicationContext, "Wallet created successfully",
                Toast.LENGTH_LONG
            ).show()
            credential = WalletUtils.loadCredentials(password, file.absolutePath + "/" + walletName)
            txtAddress.text = credential.address
            Log.e("WalletAddress", credential.address)
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext, "Error creating wallet",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Throws(java.lang.Exception::class)
    fun makeTransaction(v: View?) {
        // get the amout of eth value the user wants to send
        val Edtvalue = findViewById<EditText>(R.id.ethvalue)
        val value = Edtvalue.text.toString().toDouble()
        try {

            //You can use this
            //add chain id also
//            val chainId = web3.netVersion().send().netVersion;
//
//            val ethGetTransactionCount = web3.ethGetTransactionCount(
//                credential.address,
//                DefaultBlockParameterName.LATEST
//            ).sendAsync().get()
//
//            val nonce = ethGetTransactionCount.transactionCount
//            val rawTransaction = RawTransaction.createEtherTransaction(
//                nonce,
//                BigInteger.valueOf(1000000000),
//                BigInteger.valueOf(21000),
//                "0xB1C96d36f796059a06cc317303cF998b4fB4BEa3",
//                Convert.toWei(value, Convert.Unit.ETHER).toBigInteger()
//            )
//            val signedMessage: ByteArray =
//                TransactionEncoder.signMessage(rawTransaction, credential)
//            val hexValue = Numeric.toHexString(signedMessage)
//            val ethSendTransaction: EthSendTransaction =
//                web3.ethSendRawTransaction(hexValue).sendAsync().get()

            //or use this , but this is shorter approach
            val receipt: TransactionReceipt = Transfer.sendFundsEIP1559(
                web3,
                credential,
                "0xB1C96d36f796059a06cc317303cF998b4fB4BEa3",
                BigDecimal.valueOf(value),
                Convert.Unit.ETHER,
                BigInteger.valueOf(6721975L),
                DefaultGasProvider.GAS_LIMIT,
                BigInteger.valueOf(20000000000L),
            ).send()
            //print credentialaddress
            Log.d("credentialaddress", credential.address)

            //check if transaction is successful give a toast
            if (receipt.isStatusOK) {
                Toast.makeText(
                    this,
                    "Transaction successful: ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Transaction failed: ",
                    Toast.LENGTH_LONG
                ).show()
            }
            Log.d("ethgasprice", "${web3.ethGasPrice().send().gasPrice}")
            Log.d("transactionreceipt", " ${receipt.transactionHash}")
            Toast.makeText(
                this,
                "Transaction successful: ",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: java.lang.Exception) {
            Log.d("errorweb32", e.message.toString())
            Toast.makeText(
                this,
                "Transaction failed: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }



    fun retrieveBalance(v: View?) {
        val Edtvalue = findViewById<EditText>(R.id.ethvalue)
        val txtBalance = findViewById<TextView>(R.id.text_balance)
        try {
            val balance =
                web3.ethGetBalance(credential.address, DefaultBlockParameterName.LATEST).sendAsync()
                    .get()
            //convert balance to ether
            val etherBalance = Convert.fromWei(balance.balance.toString(), Convert.Unit.ETHER)
            txtBalance.text = getString(R.string.your_balance) + etherBalance.toString()
        } catch (e: java.lang.Exception) {

            Toast.makeText(
                this,
                "Error: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}