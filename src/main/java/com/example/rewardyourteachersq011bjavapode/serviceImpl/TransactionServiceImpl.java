package com.example.rewardyourteachersq011bjavapode.serviceImpl;

import com.example.rewardyourteachersq011bjavapode.enums.TransactionType;
import com.example.rewardyourteachersq011bjavapode.exceptions.UserNotFoundException;
import com.example.rewardyourteachersq011bjavapode.models.Transaction;
import com.example.rewardyourteachersq011bjavapode.models.User;
import com.example.rewardyourteachersq011bjavapode.repository.TransactionRepository;
import com.example.rewardyourteachersq011bjavapode.repository.UserRepository;
import com.example.rewardyourteachersq011bjavapode.response.ApiResponse;
import com.example.rewardyourteachersq011bjavapode.service.TransactionService;
import com.example.rewardyourteachersq011bjavapode.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final UUID uuid = UUID.randomUUID();


    @Override
    public ApiResponse<List<Transaction>> findAllTransactionForAUser(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return new ApiResponse<>("success", LocalDateTime.now(), transactionRepository.findAllByUser_Id(user.getId()));
    }

    @Override
    public Transaction saveTransaction(BigDecimal amount, TransactionType transactionType, User sender, User receiver) {
        Transaction transaction = new Transaction();

        if (transactionType.equals(TransactionType.CREDIT)) {
            transaction.setUuid(uuid.toString());
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.CREDIT);
            transaction.setDescription("Credit Alert of: " + amount + " Sender is: " + sender.getName());
            transaction.setUser(receiver);
        } else {
            transaction.setUuid(uuid.toString());
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.DEBIT);
            transaction.setDescription("Debit Alert of: " + amount + " Receiver is: " + receiver.getName());
            transaction.setUser(sender);

        }
        return transactionRepository.save(transaction);
    }

    @Override
    public ApiResponse<BigDecimal> totalMoneySent(){
        String userEmail = userUtil.getAuthenticatedUserEmail();
        List<Transaction> debitTransactionList = transactionRepository.findAllByUserEmailAndTransactionType(userEmail, TransactionType.DEBIT);
        return new ApiResponse<>("success", LocalDateTime.now(),debitTransactionList.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }




}
