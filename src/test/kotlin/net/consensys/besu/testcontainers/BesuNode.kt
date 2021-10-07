package net.consensys.besu.testcontainers

import org.web3j.crypto.Credentials
import org.web3j.protocol.besu.Besu
import org.web3j.tx.PrivateTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.response.PollingPrivateTransactionReceiptProcessor
import org.web3j.utils.Base64String
import org.web3j.utils.Restriction

data class BesuNode(
    val web3j: Besu,
    val credentials: Credentials,
    val publicKey: Base64String
) {

    fun getTransactionManger(privacyGroupId: Base64String): TransactionManager {
        return PrivateTransactionManager(
            web3j,
            credentials,
            PollingPrivateTransactionReceiptProcessor(web3j, 1000, 30),
            -1,
            publicKey,
            privacyGroupId,
            Restriction.RESTRICTED
        )
    }
}
