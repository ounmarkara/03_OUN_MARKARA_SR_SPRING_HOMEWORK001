package org.example.spring_homework001.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class BulkUpdatePaymentStatus {
    private Integer[] ticketIds;
    private boolean paymentStatus;
}