package `in`.jadu.firstandroidweb3project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import `in`.jadu.firstandroidweb3project.contracts.contracts.`in`.jadu.firstandroidweb3project.Fundtransfer_sol_FundTransfer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import kotlin.random.Random

class smartContractdeploy : AppCompatActivity() {
    private lateinit var web3: Web3j
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_contractdeploy)
        initializeWeb3j()
        val credential = org.web3j.crypto.Credentials.create("0x85f625bab06ec744cc92e13178824e9734d5357cf3981b15bd1e3082f644b2fe")
        val deployContract:Fundtransfer_sol_FundTransfer = Fundtransfer_sol_FundTransfer.deploy(web3,credential,BigInteger.valueOf(20000000000),
            BigInteger.valueOf(6721975)
        ).sendAsync().get()
        val contractAddress = deployContract.contractAddress
        Log.d("contractAddress",contractAddress)
        val contract = Fundtransfer_sol_FundTransfer.load(contractAddress,web3,credential,BigInteger.valueOf(20000000000),
            BigInteger.valueOf(6721975))
        lifecycleScope.launch(Dispatchers.IO){
            Log.d("isValid",contract.isValid.toString())
        }

        val getBalance = contract.contractBalance.sendAsync()
        Log.d("getBalance",getBalance.get().toString())

        val sendEth = contract.deposit().sendAsync()
        Log.d("sendEth",sendEth.get().toString())

        val withdrawAll = contract.withdrawAll().sendAsync()
        Log.d("withdrawAll",withdrawAll.get().toString())

        val withdraw = contract.withdrawToAdress("0x85f625bab06ec744cc92e13178824e9734d5357cf3981b15bd1e3082f644b2fe").sendAsync()


    }

    private fun initializeWeb3j() {
        web3 = Web3j.build(HttpService("http://10.0.2.2:7545"))
        //create credentials

    }

}