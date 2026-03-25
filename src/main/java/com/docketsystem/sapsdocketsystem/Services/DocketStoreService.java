package com.docketsystem.sapsdocketsystem.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.docketsystem.sapsdocketsystem.Models.DocketStore;
import com.docketsystem.sapsdocketsystem.Repositories.DocketStoreRepository;

public class DocketStoreService {
    @Autowired
    private DocketStoreRepository docketStoreRepository;

    public DocketStore saveDocketStore(DocketStore docketStore) {
        return docketStoreRepository.save(docketStore);
    }

    public Optional<DocketStore> getDocketStoreById(Long id) {
        return docketStoreRepository.findById(id);
    }

    public List<DocketStore> getAllDocketStores() {
        return docketStoreRepository.findAll();
    }

    public void deleteDocketStore(Long id) {
        docketStoreRepository.deleteById(id);
    }
}

