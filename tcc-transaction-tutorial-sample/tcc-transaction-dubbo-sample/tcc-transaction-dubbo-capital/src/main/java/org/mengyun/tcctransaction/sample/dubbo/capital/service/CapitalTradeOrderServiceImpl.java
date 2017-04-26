package org.mengyun.tcctransaction.sample.dubbo.capital.service;

import com.mchange.util.Base64FormatException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.dubbo.capital.domain.entity.CapitalAccount;
import org.mengyun.tcctransaction.sample.dubbo.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.dubbo.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.dubbo.capital.domain.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Created by changming.xie on 4/2/16.
 */
@Service("capitalTradeOrderService")
public class CapitalTradeOrderServiceImpl implements CapitalTradeOrderService {

    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Autowired
    TradeOrderRepository tradeOrderRepository;

    @Override
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord", transactionContextEditor = DubboTransactionContextEditor.class)
    @Transactional
    public String record(CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital try record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        if (1 == 1) {
            throw new Base64FormatException();
        }

        try {
            Thread.sleep(6000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TradeOrder tradeOrder = new TradeOrder(
                tradeOrderDto.getSelfUserId(),
                tradeOrderDto.getOppositeUserId(),
                tradeOrderDto.getMerchantOrderNo(),
                tradeOrderDto.getAmount()
        );

        tradeOrderRepository.insert(tradeOrder);

        CapitalAccount transferFromAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

        transferFromAccount.transferFrom(tradeOrderDto.getAmount());

        capitalAccountRepository.save(transferFromAccount);
        return "success";
    }

    @Transactional
    public void confirmRecord(CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital confirm record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        tradeOrder.confirm();
        tradeOrderRepository.update(tradeOrder);

        CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

        transferToAccount.transferTo(tradeOrderDto.getAmount());

        capitalAccountRepository.save(transferToAccount);
    }

    @Transactional
    public void cancelRecord(CapitalTradeOrderDto tradeOrderDto) {
        System.out.println("capital cancel record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount capitalAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());

            capitalAccountRepository.save(capitalAccount);
        }
    }
}
