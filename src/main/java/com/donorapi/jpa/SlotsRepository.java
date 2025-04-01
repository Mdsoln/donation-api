package com.donorapi.jpa;

import com.donorapi.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotsRepository extends JpaRepository<Slot, Integer> {

    @Query("SELECT s FROM Slot s WHERE s.hospital.hospitalId = :hospitalId")
    List<Slot> findSlotsByHospitalId(@Param("hospitalId") Integer hospitalId);


}
