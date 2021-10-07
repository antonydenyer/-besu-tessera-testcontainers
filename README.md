# Testing Ethereum Networks with Testcontainers

The following code will create a 3 node besu network with 1 miner and 2 full nodes with privacy enabled and 2 tessera enclaves. 
The network is configured with docker-compose and was inspired by https://docs.goquorum.consensys.net/en/stable/Tutorials/Quorum-Dev-Quickstart/

## Running

`./gradlew check` will run the tests

The test simply deploys a smart contract into a private dev network then sets a value. This could be used as a template or inspiration for further testing.

