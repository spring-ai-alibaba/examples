package com.alibaba.cloud.ai.node;

import com.alibaba.cloud.ai.domain.Result;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description
 * @create 2025/12/14 11:30
 */
@RequiredArgsConstructor
public class OrderNode implements NodeAction {
    private final String inputKey;
    private final String outputKey;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Result input = state.value(inputKey, Result.empty());
        if (input == null) {
            throw new RuntimeException("input is null");
        }
        Result.SlotParams slotParams = input.slots();
        // 模拟订票
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate departureDate = LocalDate.parse(slotParams.departureDate());
        int hour = ThreadLocalRandom.current().nextInt(6, 23);
        String message = String.format("""
                        ✅ 订票成功！
                        
                        订单号：%s
                        出发：%s
                        到达：%s
                        日期：%s
                        
                        祝您旅途愉快！
                        """,
                orderId,
                slotParams.departureCity(),
                slotParams.arrivalCity(),
                LocalDateTime.of(departureDate, LocalTime.of(hour, 0))
        );
        return Map.of(outputKey, message);
    }
}
