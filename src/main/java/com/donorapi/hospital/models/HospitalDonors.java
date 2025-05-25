package com.donorapi.hospital.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HospitalDonors {
      private Integer id;
      private String name;
      private String email;
      private String phone;
      private String bloodGroup;
      private long donations;
}
