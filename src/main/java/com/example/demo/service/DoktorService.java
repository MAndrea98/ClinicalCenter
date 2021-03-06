package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Doktor;
import com.example.demo.model.Klinika;
import com.example.demo.repository.DoktorRespository;


@Service
@Transactional(readOnly = true)
public class DoktorService {

	@Autowired
	DoktorRespository doktorRespository;
	
	public Doktor findOne(Long id) {
		return doktorRespository.findById(id).orElseGet(null);
	}
	
	public List<Doktor> findAll() {
		return doktorRespository.findAll();
	}

	public Doktor findByIdKorisnik(Long id) {
		return doktorRespository.findByIdKorisnik(id);
	}
	
	public List<Doktor> findAllByKlinika(Klinika k){
		return doktorRespository.findAllByKlinika(k);
	}

	public Optional<Doktor> findById(Long id) {
		return doktorRespository.findById(id);
	}
	
	@Transactional(readOnly = false)
	public Doktor save(Doktor doktor) {
		return doktorRespository.save(doktor);
	}
	
	public List<Doktor> findByDatumPregledaISpecIKlinika(LocalDate datum, String specijalizacija, Long id_klinika) {
		return doktorRespository.findByDatumPregledaISpecIKlinika(datum, specijalizacija, id_klinika);
	}
}
