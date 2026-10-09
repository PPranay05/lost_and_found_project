package com.example.lostfound.repository;

import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    Optional<QrCode> findByQrCodeId(String qrCodeId);
    Optional<QrCode> findByFoundItem(FoundItem foundItem);
    Optional<QrCode> findByFoundItemId(Long foundItemId);
}
