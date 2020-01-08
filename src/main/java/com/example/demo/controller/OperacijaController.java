package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Doktor;
import com.example.demo.model.Operacija;
import com.example.demo.model.Sala;
import com.example.demo.model.StatusOperacije;
import com.example.demo.model.Termin;
import com.example.demo.service.DoktorService;
import com.example.demo.service.OperacijaService;
import com.example.demo.service.SalaService;
import com.example.demo.service.TerminService;


@RestController
@RequestMapping(value = "operacija")
public class OperacijaController {

	@Autowired
	private OperacijaService operacijaService;
	
	@Autowired
	private SalaService salaService;
	
	@Autowired
	private DoktorService doktorService;
	
	@Autowired
	private TerminService terminService;
	
	
	@RequestMapping(value = "/zahtevi", method=RequestMethod.GET) 
	public ResponseEntity<List<Operacija>> zahtevi() {
		List<Operacija> operacije = operacijaService.findByStatus(StatusOperacije.NEPOTVRĐEN);
		return new ResponseEntity<List<Operacija>>(operacije, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/rezervisi/{sala}/{operacija}/{doktori}", method = RequestMethod.GET)
	public ResponseEntity<String> rezervisi(@PathVariable("sala") Long sala_id, @PathVariable("operacija") Long operacija_id, @PathVariable("doktori") String text) {
		System.out.println("sala" + sala_id + "operacija" + operacija_id + "doktori" + text);
		Sala sala = salaService.findOne(sala_id);
		Operacija operacija = operacijaService.findOne(operacija_id);
		operacija.setStatus(StatusOperacije.ZAKAZAN);
		operacija.setSala(sala);
		String[] splitter = text.split("~");
		for (String s : splitter) {
			if (!s.equals("")) {
				Doktor doktor = doktorService.findByIdKorisnik(Long.parseLong(s));
				doktor.getOperacije().add(operacija);
				doktorService.save(doktor);
			}
		}
		List<Termin> termini = terminService.findAll();
		for (Termin termin : termini) {
			if (termin.getSala().getId().equals(sala.getId())) {
				termin.setSlobodan(false);
			}
		}
		operacijaService.save(operacija);
		
		return new ResponseEntity<String>("Uspesno rezervisana sala", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/preuzmi/{id}", method = RequestMethod.GET)
	public ResponseEntity<Operacija> preuzmi(@PathVariable("id") Long identifikacija) {
		Operacija operacija = operacijaService.findOne(identifikacija);
		return new ResponseEntity<Operacija>(operacija, HttpStatus.OK);
	}
	
}
