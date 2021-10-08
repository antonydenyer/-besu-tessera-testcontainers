pragma solidity 0.5.9;

import "./SimpleStorage.sol";

contract RemoteSimpleStorage {
    SimpleStorage public simpleStorage;

    constructor(SimpleStorage _simpleStorage) public {
        simpleStorage = _simpleStorage;
    }

    function set(uint value) public {
        simpleStorage.set(value);
    }

    function get() public view returns (uint) {
        return simpleStorage.get();
    }
}
