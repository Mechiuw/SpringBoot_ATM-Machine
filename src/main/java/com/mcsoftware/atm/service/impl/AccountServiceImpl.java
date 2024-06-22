package com.mcsoftware.atm.service.impl;

import com.mcsoftware.atm.model.dto.request.AccountRequest;
import com.mcsoftware.atm.model.dto.response.AccountResponse;
import com.mcsoftware.atm.model.entity.Account;
import com.mcsoftware.atm.model.entity.User;
import com.mcsoftware.atm.repository.AccountRepository;
import com.mcsoftware.atm.repository.UserRepository;
import com.mcsoftware.atm.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public void minimumDeposit(BigDecimal currentBalance) {
        if(currentBalance.compareTo(new BigDecimal("500000")) < 0){
            throw new IllegalArgumentException("[WARNING] DEPOSIT BALANCE MUST BE LEAST 500000");
        }

    };

    public void checkAccountChanges(Account account,AccountRequest accountRequest){
        String accNumber = accountRequest.getAccountNumber();
        String accUserId = accountRequest.getUser().getId();
        if(!account.getAccountNumber().equals(accNumber)){
            throw new IllegalArgumentException(String.format("not the same updated changes of %s",accNumber));
        }
        if(!account.getUser().getId().equals(accUserId)){
            throw new IllegalArgumentException(String.format("not the same updated changes of %s",accUserId));
        }
    }

    @Override
    public AccountResponse create(AccountRequest accountRequest) {

        //validates minimum deposit account
        minimumDeposit(accountRequest.getBalance());

        String userId = accountRequest.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("not found user with id : %s",userId)));

        assert user != null : "user must not be null or not found";

        Account account = Account.builder()
                .accountNumber(accountRequest.getAccountNumber())
                .balance(accountRequest.getBalance())
                .user(user)
                .build();
        Account savedAccount = accountRepository.save(account);

        return AccountResponse.builder()
                .id(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .balance(savedAccount.getBalance())
                .userId(savedAccount.getUser().getId())
                .build();
    }

    @Override
    public List<Account> getAll() {
        try {
            List<Account> accountList = accountRepository.findAll();
            if(!accountList.isEmpty()){
                return accountList;
            } else {
                throw new NoSuchElementException("not found any account on list");
            }
        } catch (Exception e){
            System.err.printf("Exception while retrieving accounts : %s%n",e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public AccountResponse getById(String id) {
        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException(String.format("not found account with id : %s", id)));

            String userId = account.getUser().getId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException(String.format("not found user with id : %s", userId)));
            return AccountResponse.builder()
                    .id(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .userId(user.getId())
                    .balance(account.getBalance())
                    .build();
        } catch (NoSuchElementException e) {
            System.err.printf("Not found specific user associated with the account : %s%n",e.getMessage());
            return AccountResponse.builder()
                    .id("account id not found")
                    .userId("user not found")
                    .build();
        }
        catch (Exception e){
            System.err.printf("Exception handled while retrieving account : %s%n",e.getMessage());
            return null;
        }
    }

    @Override
    public AccountResponse update(String id, AccountRequest accountRequest) {
        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException(String.format("not found account with id : %s", id)));
            account.setAccountNumber(accountRequest.getAccountNumber());
            account.setUser(accountRequest.getUser());

            Account updatedAccount = accountRepository.save(account);
            checkAccountChanges(updatedAccount, accountRequest);

            return AccountResponse.builder()
                    .id(updatedAccount.getId())
                    .accountNumber(updatedAccount.getAccountNumber())
                    .userId(updatedAccount.getUser().getId())
                    .balance(updatedAccount.getBalance())
                    .build();
        } catch (NoSuchElementException e){
            System.err.printf("No Such Element of %s%n",e.getMessage());
            return AccountResponse.builder()
                    .id("not found")
                    .accountNumber("not found")
                    .userId("not found")
                    .balance(BigDecimal.ZERO)
                    .build();
        } catch (IllegalArgumentException e){
            System.err.printf("Validations error associated with the account : %s%n",e.getMessage());
            return AccountResponse.builder()
                    .id("validation error")
                    .accountNumber("validation error")
                    .userId("validation error")
                    .balance(BigDecimal.ZERO)
                    .build();
        } catch (Exception e) {
            System.err.printf("Exception called %s%n",e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public AccountResponse softDeleteAccount(String id, AccountRequest accountRequest) {
        return null;
    }

    @Override
    public AccountResponse checkCurrentBalance(String id) {
        return null;
    }

    @Override
    public AccountResponse depositBalance(String id, BigDecimal deposit) {
        return null;
    }

    @Override
    public AccountResponse withdrawBalance(String id, BigDecimal withdraw) {
        return null;
    }

    @Override
    public AccountResponse transferBalance(String accountId, String transferId, BigDecimal transfer) {
        return null;
    }
}
