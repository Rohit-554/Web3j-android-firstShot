// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract FundTransfer {
    uint public balanceReceived;
    function deposit() public payable {
        balanceReceived += msg.value;
    }

    function getContractBalance() public view returns(uint){
        return address(this).balance;
    }

    function withdrawAll() public {
        address payable to = payable(msg.sender);
        to.transfer(getContractBalance());
    }

    function withdrawToAdress(address payable to) public {
        to.transfer(getContractBalance());
    }
}
