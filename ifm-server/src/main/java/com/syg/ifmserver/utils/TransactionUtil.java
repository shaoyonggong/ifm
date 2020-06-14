package com.syg.ifmserver.utils;

import com.syg.ifmcommon.exception.IFMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Predicate;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Component
public class TransactionUtil {

    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;

    public boolean transact(Predicate predicate) throws IFMException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {

            boolean bool = predicate.test(null);

            if (bool) {

                transactionManager.commit(status);

                return true;
            } else {

                transactionManager.rollback(status);

                return false;
            }

        } catch (Exception ex) {

            transactionManager.rollback(status);

            throw new IFMException(ex.getMessage());
        }
    }
}
