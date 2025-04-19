package com.donorapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SlotDto {
     private long slotId;
     private long hospitalId;
     private LocalDateTime startTime;
     private LocalDateTime endTime;
     private int availableBookings;
}
