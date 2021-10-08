package net.consensys.besu.tests

import net.consensys.besu.testcontainers.BesuNetwork
import org.allfunds.blockchain.besu.tests.contracts.RemoteSimpleStorage
import org.allfunds.blockchain.besu.tests.contracts.SimpleStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrivateToPublicStateTest {

    private val instance = BesuNetwork()

    @AfterAll
    internal fun afterAll() {
        instance.stop()
    }

    @Test
    fun canDeployPrivateContractThatTalksToPublicState() {
        val alice = instance.get("alice")

        val privacyGroup = alice.web3j.privCreatePrivacyGroup(
            mutableListOf(
                instance.get("alice").publicKey,
                instance.get("bob").publicKey
            ),
            "Group Name",
            "Group Description"
        ).send()

        val transactionManger = alice.getTransactionManger(privacyGroup.privacyGroupId)

        val publicSimpleStorage = SimpleStorage.deploy(
            alice.web3j,
            alice.credentials,
            DefaultGasProvider()
        ).send()

        publicSimpleStorage.set(BigInteger.TEN).send()

        val privateRemote = RemoteSimpleStorage.deploy(
            alice.web3j,
            transactionManger,
            DefaultGasProvider(),
            publicSimpleStorage.contractAddress
        ).send()

        assertThat(privateRemote.get().send()).isEqualTo(BigInteger.TEN)
    }

    @Test
    fun canDeployPrivateContractThatTalksToPublicPublicState() {
        val alice = instance.get("alice")

        val privacyGroup = alice.web3j.privCreatePrivacyGroup(
            mutableListOf(
                instance.get("alice").publicKey,
                instance.get("bob").publicKey
            ),
            "Group Name",
            "Group Description"
        ).send()

        val transactionManger = alice.getTransactionManger(privacyGroup.privacyGroupId)

        val publicSimpleStorage = SimpleStorage.deploy(
            alice.web3j,
            alice.credentials,
            DefaultGasProvider()
        ).send()

        publicSimpleStorage.set(BigInteger.TEN).send()

        val publicRemote = RemoteSimpleStorage.deploy(
            alice.web3j,
            alice.credentials,
            DefaultGasProvider(),
            publicSimpleStorage.contractAddress
        ).send()

        assertThat(publicRemote.get().send()).isEqualTo(BigInteger.TEN)

        val privateRemote = RemoteSimpleStorage.deploy(
            alice.web3j,
            transactionManger,
            DefaultGasProvider(),
            publicRemote.contractAddress
        ).send()

        assertThat(privateRemote.get().send()).isEqualTo(BigInteger.TEN)
    }
}