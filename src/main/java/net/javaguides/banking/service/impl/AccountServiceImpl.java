package net.javaguides.banking.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.javaguides.banking.dto.AccountDto;
import net.javaguides.banking.entity.Account;
import net.javaguides.banking.mapper.AccountMapper;
import net.javaguides.banking.repository.AccountRepository;
import net.javaguides.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        log.info("Creating account for customer: {}", accountDto.getAccountHolderName());
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount =accountRepository.save(account);
        log.info("Account created with ID: {}", savedAccount.getId());
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        log.info("Fetching account with ID: {}", id);
     Account account =   accountRepository.findById(id)
             .orElseThrow(()->new RuntimeException("Account does not exists"));
     return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {

        log.info("Depositing {} to account ID {}", amount, id);
        Account account = accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account id does not exists"));

        double total = account.getBalance()+amount;
        account.setBalance(total);
        Account savedAccount =  accountRepository.save(account);
        log.info("Deposit successful. New balance for account ID {}: {}", id, savedAccount.getBalance());
        return AccountMapper.mapToAccountDto(savedAccount);

    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        log.info("Withdrawing {} from account ID {}", amount, id);
        Account account = accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account id does not exists"));
        if(account.getBalance()<amount){
            log.warn("Insufficient balance for account ID {}. Current balance: {}, requested: {}", id, account.getBalance(), amount);
            throw new RuntimeException("Insufficient amount");
        }
        double total = account.getBalance()-amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        log.info("Withdrawal successful. New balance for account ID {}: {}", id, savedAccount.getBalance());
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        log.info("Fetching all accounts");
        List<Account> accounts = accountRepository.findAll();
        List<AccountDto> accountDtos = accounts.stream()
                .map(account -> AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());
        log.info("Total accounts fetched: {}", accountDtos.size());
        return accountDtos;
    }

    @Override
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        Account account =   accountRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Account does not exists"));
        accountRepository.deleteById(id);
        log.info("Account with ID {} deleted successfully", id);
    }
}
