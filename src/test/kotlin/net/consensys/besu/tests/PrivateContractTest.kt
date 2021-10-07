package net.consensys.besu.tests

import java.math.BigInteger
import net.consensys.besu.testcontainers.BesuNetwork
import org.allfunds.blockchain.besu.tests.contracts.SimpleStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.web3j.protocol.besu.response.privacy.PrivateTransactionReceipt
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import org.web3j.tx.gas.DefaultGasProvider

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrivateContractTest {

    private val instance = BesuNetwork()

    @AfterAll
    internal fun afterAll() {
        instance.stop()
    }

    @Test
    fun canDeployPrivateContractWithGas() {
        val alice = instance.get("alice")

        val initialBalance = alice.web3j.ethGetBalance(alice.credentials.address, LATEST).send().balance

        assertThat(initialBalance).isGreaterThan(BigInteger.TEN)

        val privacyGroup = alice.web3j.privCreatePrivacyGroup(
            mutableListOf(
                instance.get("alice").publicKey,
                instance.get("bob").publicKey
            ),
            "Group Name",
            "Group Description"
        ).send()

        val transactionManger = alice.getTransactionManger(privacyGroup.privacyGroupId)

        val simpleStorageContract = SimpleStorage.deploy(
            alice.web3j,
            transactionManger,
            DefaultGasProvider()
        ).send()

        val receipt = simpleStorageContract.set(BigInteger.valueOf(42)).send() as PrivateTransactionReceipt

        assertThat(simpleStorageContract.get().send()).isEqualTo(BigInteger.valueOf(42))

        val commitmentTransaction = alice.web3j.ethGetTransactionByHash(receipt.getcommitmentHash()).send().transaction.get()
        assertThat(commitmentTransaction.gas).isGreaterThan(BigInteger.ONE)

        val finalBalance = alice.web3j.ethGetBalance(alice.credentials.address, LATEST).send().balance

        assertThat(finalBalance).isLessThan(initialBalance)
    }
}
