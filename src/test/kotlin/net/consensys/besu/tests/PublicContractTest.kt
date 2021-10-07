package net.consensys.besu.tests

import java.math.BigInteger
import net.consensys.besu.testcontainers.BesuNetwork
import org.allfunds.blockchain.besu.tests.contracts.SimpleStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.web3j.tx.gas.DefaultGasProvider

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicContractTest {

    private val instance = BesuNetwork()

    @AfterAll
    internal fun afterAll() {
        instance.stop()
    }

    @Test
    fun canDeployPublicContract() {
        val alice = instance.get("alice")

        val simpleStorageContract = SimpleStorage.deploy(
            alice.web3j,
            alice.credentials,
            DefaultGasProvider()
        ).send()

        simpleStorageContract.set(BigInteger.valueOf(42)).send()

        assertThat(simpleStorageContract.get().send()).isEqualTo(BigInteger.valueOf(42))
    }
}
