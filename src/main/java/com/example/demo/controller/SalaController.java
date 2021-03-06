package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SalaDTO;
import com.example.demo.dto.TerminDTO;
import com.example.demo.model.AdministratorKlinike;
import com.example.demo.model.Klinika;
import com.example.demo.model.Korisnik;
import com.example.demo.model.LogedUser;
import com.example.demo.model.Operacija;
import com.example.demo.model.Pregled;
import com.example.demo.model.Sala;
import com.example.demo.model.Termin;

import com.example.demo.model.UlogaKorisnika;

import com.example.demo.service.AdministratorKlinikeService;
import com.example.demo.service.KlinikaService;
import com.example.demo.service.KorisnikService;
import com.example.demo.service.OperacijaService;
import com.example.demo.service.PregledService;
import com.example.demo.service.SalaService;
import com.example.demo.service.TerminService;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
@RequestMapping(value = "sala")
public class SalaController {

	@Autowired
	SalaService salaService;
	
	@Autowired
	KlinikaService klinikaService;
	
	@Autowired
	PregledService pregledService;

	@Autowired
	OperacijaService operacijaService;
	
	@Autowired
	AdministratorKlinikeService administratorService;
	
	@Autowired
	TerminService terminService;


	
	@Autowired
	KorisnikService korisnikService;


	@RequestMapping(value = "/sveSale/{val}", method=RequestMethod.GET)
	public ResponseEntity<List<SalaDTO>> getSveSale(@PathVariable String val){
		List<Sala> sala = salaService.findAll();
		List<SalaDTO> sale = new ArrayList<SalaDTO>();
		Klinika klinika = klinikaService.findByName(val);
		for (Sala s : sala) {
			if(s.getKlinika().getId() == klinika.getId())
				sale.add(new SalaDTO(s));
		}
		
		return new ResponseEntity<>(sale, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/sve_sale/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<SalaDTO>> getSaleKlinika(@PathVariable String id){
		List<Sala> sale = salaService.findAll();
		List<SalaDTO> retVal = new ArrayList<SalaDTO>();
		String[] parts = id.split("\\.");
		Long idL = Long.parseLong(parts[0]);
	
		for(Sala s : sale) {
			if(s.getKlinika().getId() == idL) {
				retVal.add(new SalaDTO(s));
			}
		}
		
		return new ResponseEntity<List<SalaDTO>>(retVal, HttpStatus.OK);
	}
	@RequestMapping(value = "/termini/{val}", method = RequestMethod.GET)
	public ResponseEntity<List<TerminDTO>> getSviTermini(@PathVariable String val){
		List<TerminDTO> termini = new ArrayList<TerminDTO>();
		Long salaID = Long.parseLong(val);
		Optional<Sala> oSala = salaService.findById(salaID);
		Sala sala = oSala.get();
		
		for(Termin t : sala.getSlobodniTermini()) {
			TerminDTO addT = new TerminDTO(t);
			termini.add(addT);
		}
		
		return new ResponseEntity<>(termini, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/nova_sala")
	public ResponseEntity<Object> novaSala(@RequestBody String txt){
		String[] selRoomData = null;
		String selRoom = null;
		
		String[] selClinicData = null;
		String selClinic = null;
		
		String[] nameData = null;
		String name = null;
		
		String[] descData = null;
		String desc = null;
		
		String[] data = txt.split("&");
		
	
		if((data[0].charAt(data[0].length()-1)) != '=') {
			selRoomData = data[0].split("=");
			selRoom = selRoomData[1];
			
		}
		
		Long selClinicID = 0L;
		if((data[1].charAt(data[1].length()-1)) != '=') {
			selClinicData = data[1].split("=");
			selClinic = selClinicData[1];
			System.out.println(selClinic);
			String selClinicIDS = selClinic.split("\\.")[0];
			System.out.println(selClinicIDS);
			if(!selClinicIDS.equals("New"))
				selClinicID = Long.parseLong(selClinicIDS);
		}
		
		if((data[2].charAt(data[2].length()-1)) != '=') {
			nameData = data[2].split("=");
			name = nameData[1];
			if(name.contains("\\+"))
				name.replaceAll("\\+", " ");
	
		}
		

		if((data[3].charAt(data[3].length()-1)) != '=') {
			descData = data[3].split("=");
			desc = descData[1];
			if(desc.contains("\\+"))
				desc.replaceAll("\\+", " ");
		}
		
		Sala s;
		
		if(selRoom.equals("New")) {
			s = new Sala();
			Klinika k = klinikaService.findOne(selClinicID);
			if(k == null) {
				return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
			}
			s.setKlinika(k);
			s.setIme(name);
			s.setOpis(desc);
		}else {
			String selRoomIDS = selRoom.split("\\.")[0];
			Long selRoomID = Long.parseLong(selRoomIDS);
			s = salaService.findOne(selRoomID);
			if(name != null) {
				s.setIme(name);
			}
			if(desc != null) {
				s.setOpis(desc);
			}
			salaService.save(s);
		}
		return new ResponseEntity<Object>(null, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/nova_sala/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> novaSalaID(@PathVariable("id") Long id, HttpEntity<String> json) throws ParseException{
		String jString = json.getBody();
		JSONParser parser = new JSONParser();
		JSONObject jObj = (JSONObject)parser.parse(jString);
		
		String name = (String) jObj.get("name");
		String description = (String) jObj.get("desc");
		
		Optional<AdministratorKlinike> oak = administratorService.findByIdKorisnik(id);
		AdministratorKlinike ak = oak.get();
		
		Sala s = new Sala();
		s.setIme(name);
		s.setOpis(description);
		s.setKlinika(ak.getKlinika());
		
		salaService.save(s);
		return new ResponseEntity<String>("Dodata sala", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/search/{val}", method = RequestMethod.GET)
	public ResponseEntity<List<SalaDTO>> getSale(@PathVariable String val){
		List<SalaDTO> sale = new ArrayList<SalaDTO>();
		
		if(val.equals(":"))
			return new ResponseEntity<>(null, HttpStatus.OK);
		
		boolean num = false;
		System.out.println(val);
		String[] parts = val.split(":");
		String name = parts[0];
		String date = parts[1];
		String klinika = parts[2];
		
	
		for(int i = 0; i < name.length(); i++) {
			if(!Character.isDigit(name.charAt(i))) {
				num = false;
				break;
			}
			num = true;
		}
		
		
		Long sId = 0L;
		if(num) {

			 sId = Long.parseLong(name);
			 Optional<Sala> s = salaService.findById(sId);
			 
			 if(s == null) {
				 System.out.println("Not found");
				 return new ResponseEntity<>(sale, HttpStatus.NOT_FOUND);
				
			 }
			 Sala foundS = s.get();
		
			 if(foundS.getKlinika().getIme().equals(klinika))
				 sale.add(new SalaDTO(foundS));
		}else {
			List<Sala> allS = salaService.findAll();
			
			for(Sala s: allS) {
				if(s.getIme().toLowerCase().contains(name.toLowerCase()) || s.getIme().toLowerCase().equals(name.toLowerCase())) {
					if(s.getKlinika().getIme().equals(klinika))
						sale.add(new SalaDTO(s));
				}
			}
		}
		
		
		return new ResponseEntity<>(sale, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/slobodni_termini/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Sala>> slobodniTermini(@PathVariable("id") Long identifikacija) {
		if(!LogedUser.getInstance().getUserRole().equals(UlogaKorisnika.ADMIN_KLINIKE)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Operacija operacija = operacijaService.findOne(identifikacija);
		AdministratorKlinike admin = administratorService.findByIdKorisnik(LogedUser.getInstance().getUserId().toString());
		Klinika klinika = klinikaService.findOne(admin.getKlinika().getId());
		List<Sala> sveSale = salaService.findByKlinika(klinika);
		System.out.println("##########sve_sale" + sveSale.size());
		List<Sala> slobodneSale = new ArrayList<Sala>();
		for (Sala sala : sveSale) {
			for (Termin termin : sala.getSlobodniTermini()) {
				if (termin.isSlobodan() && termin.getDatum().equals(operacija.getDatumIVremeOperacije())) {
					slobodneSale.add(sala);
				}
			}
		}
		System.out.println("##########################" + slobodneSale.size());
		return new ResponseEntity<List<Sala>>(slobodneSale, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/drugi_slobodni_termini/{id}")
	public ResponseEntity<Sala> prviSlobodan(@PathVariable("id") Long identifikacija) {
		if(!LogedUser.getInstance().getUserRole().equals(UlogaKorisnika.ADMIN_KLINIKE)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		System.out.println("##############drugi slobodni termini");
		Operacija operacija = operacijaService.findOne(identifikacija);
		AdministratorKlinike admin = administratorService.findByIdKorisnik(LogedUser.getInstance().getUserId().toString());
		Klinika klinika = klinikaService.findOne(admin.getKlinika().getId());
		List<Sala> sveSale = salaService.findByKlinika(klinika);
		Sala rezervisanaSala = sveSale.get(0);
		long min = new Long(Long.MAX_VALUE);
		Termin slobodanTermin = new Termin();
		System.out.println("############# sve sale size" + sveSale.size());
		try {
			for (Sala sala : sveSale) {
				for (Termin termin : sala.getSlobodniTermini()) {
					if (termin.isSlobodan()) {
						slobodanTermin = termin;
						break;
					}
				}
			}
	
			for (Sala sala : sveSale) {
				for (Termin termin : sala.getSlobodniTermini()) {
					if (termin.isSlobodan()) {
						long end = termin.getDatum().getTimeInMillis();
					    long start = operacija.getDatumIVremeOperacije().getTimeInMillis();
					    if (min > Math.abs(end - start)) {
					    	min = Math.abs(end - start);
					    	slobodanTermin = termin;
					    }
					}
				}
			}
		
		
			for (Sala sala : sveSale) {
				if (slobodanTermin.getSala().getId().equals(sala.getId())) {
					rezervisanaSala = sala;
				}
			}
		}
		catch (Exception e) {
			System.out.println("########" + e);
			return new ResponseEntity<Sala>(HttpStatus.NOT_FOUND);
		} 

		operacija.setDatumIVremeOperacije(slobodanTermin.getDatum());
		operacijaService.save(operacija);
		return new ResponseEntity<Sala>(rezervisanaSala, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/termini_pregled/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Sala>> slobodniTerminiPregled(@PathVariable("id") Long identifikacija){
		Pregled pregled = pregledService.findOne(identifikacija);
		Klinika klinika = klinikaService.findOne((long)2);
		List<Sala> sveSale = salaService.findByKlinika(klinika);
		System.out.println("##########sve_sale" + sveSale.size());
		List<Sala> slobodneSale = new ArrayList<Sala>();
		for(Sala sala : sveSale) {
			for(Termin termin : sala.getSlobodniTermini()) {
				if(termin.isSlobodan() && termin.getDatum().equals(pregled.getDatumIVremePregleda())) {
					slobodneSale.add(sala);
				}
			}
		}
		System.out.println("##########################" + slobodneSale.size());
		return new ResponseEntity<List<Sala>>(slobodneSale, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/termini_pregled/{id}/{admin}", method = RequestMethod.GET)
	public ResponseEntity<List<Sala>> slobodniTerminiPregledId(@PathVariable("id") Long identifikacija, @PathVariable("id") Long admin){
		Pregled pregled = pregledService.findOne(identifikacija);
		System.out.println(admin);
		Optional<AdministratorKlinike> oak = administratorService.findByIdKorisnik(admin);
		AdministratorKlinike ak = oak.get();
		Optional<Klinika> ok = klinikaService.findById(ak.getKlinika().getId());
		Klinika klinika = ok.get();
		List<Sala> sveSale = salaService.findByKlinika(klinika);
		System.out.println("##########sve_sale" + sveSale.size());
		List<Sala> slobodneSale = new ArrayList<Sala>();
		for(Sala sala : sveSale) {
			for(Termin termin : sala.getSlobodniTermini()) {
				if(termin.isSlobodan() && termin.getDatum().equals(pregled.getDatumIVremePregleda())) {
					slobodneSale.add(sala);
				}
			}
		}
		System.out.println("##########################" + slobodneSale.size());
		return new ResponseEntity<List<Sala>>(slobodneSale, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/drugi_termini_pregled/{id}")
	public ResponseEntity<Sala> prviSlobodanP(@PathVariable("id") Long identifikacija) {
		System.out.println("##############drugi slobodni termini");
		Pregled pregled = pregledService.findOne(identifikacija);
		Klinika klinika = klinikaService.findOne((long) 2);
		List<Sala> sveSale = salaService.findByKlinika(klinika);
		Sala rezervisanaSala = sveSale.get(0);
		long min = new Long(Long.MAX_VALUE);
		Termin slobodanTermin = new Termin();
		System.out.println("############# sve sale size" + sveSale.size());
		try {
			for (Sala sala : sveSale) {
				for (Termin termin : sala.getSlobodniTermini()) {
					if (termin.isSlobodan()) {
						slobodanTermin = termin;
						break;
					}
				}
			}
	
			for (Sala sala : sveSale) {
				for (Termin termin : sala.getSlobodniTermini()) {
					if (termin.isSlobodan()) {
						long end = termin.getDatum().getTimeInMillis();
					    long start = pregled.getDatumIVremePregleda().getTimeInMillis();
					    if (min > Math.abs(end - start)) {
					    	min = Math.abs(end - start);
					    	slobodanTermin = termin;
					    }
					}
				}
			}
		
		
			for (Sala sala : sveSale) {
				if (slobodanTermin.getSala().getId().equals(sala.getId())) {
					rezervisanaSala = sala;
				}
			}
		}
		catch (Exception e) {
			System.out.println("########" + e);
			return new ResponseEntity<Sala>(HttpStatus.NOT_FOUND);
		} 

		pregled.setDatumIVremePregleda(slobodanTermin.getDatum());
		pregledService.save(pregled);
		return new ResponseEntity<Sala>(rezervisanaSala, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/sve_sale_klinika/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<SalaDTO>> sveSaleKlinika(@PathVariable("id") Long id){
		List<SalaDTO> retVal = new ArrayList<SalaDTO>();
		System.out.println(id);
		Optional<AdministratorKlinike> oak = administratorService.findByIdKorisnik(id);
		AdministratorKlinike ak = oak.get();
		
		Optional<Klinika> ok = klinikaService.findById(ak.getKlinika().getId());
		Klinika k = ok.get();
		
		for(Sala s : k.getSale()) {
			retVal.add(new SalaDTO(s));
		}
		
		return new ResponseEntity<List<SalaDTO>>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/sve_sale_klinika_termini/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<TerminDTO>> sviTerminiSala(@PathVariable("id") Long id){
	
		List<TerminDTO> retVal = new ArrayList<TerminDTO>();
		
		Sala s = salaService.findOne(id);
		
		for(Termin t : s.getSlobodniTermini()) {
			if(t.isSlobodan()) {
				retVal.add(new TerminDTO(t));
			}
		}
		return new ResponseEntity<List<TerminDTO>>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/obrisi_salu/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Integer> obrisiSalu(@PathVariable("id") Long id){
		Integer retVal = new Integer(0);
		Sala s = salaService.findOne(id);
		List<Termin> termini = terminService.findAll();
		for(Termin t : termini) {
			if(t.getSala().getId() == s.getId()) {
				if(!t.isSlobodan()) {
					return new ResponseEntity<Integer>(retVal, HttpStatus.OK);
				}
			}
		}
		s.setKlinika(null);
		salaService.save(s);
		retVal = new Integer(1);
		return new ResponseEntity<Integer>(retVal, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/izmena_sale/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> izmenaSale(@PathVariable("id") Long id, HttpEntity<String> json) throws ParseException{
		
		Optional<AdministratorKlinike> oak = administratorService.findByIdKorisnik(id);
		AdministratorKlinike ak = oak.get();
		
		String jString = json.getBody();
		JSONParser parser = new JSONParser();
		JSONObject jObj = (JSONObject)parser.parse(jString);
		String name = (String)jObj.get("name");
		String desc = (String)jObj.get("desc");
		String salaStr = ((Integer)jObj.get("id")).toString();
		Optional<Klinika> ok = klinikaService.findById(ak.getKlinika().getId());
		Klinika k = ok.get();
		Optional<Sala> os = salaService.findById(Long.parseLong(salaStr));
		Sala s = os.get();
		s.setIme(name);
		s.setOpis(desc);
		salaService.save(s);
		
		return new ResponseEntity<String>("Izmenjena sala", HttpStatus.OK);
	}

	@RequestMapping(value = "/novi_termin/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> noviTermin(@PathVariable("id") Long id, HttpEntity<String> json) throws ParseException{
		String jString = json.getBody();
		JSONParser parser = new JSONParser();
		JSONObject jObj = (JSONObject)parser.parse(jString);
		String date = (String)jObj.get("date");
		String time = (String)jObj.get("time");
		
		String[] dateParts = date.split("-");
		int year = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]);
		int day = Integer.parseInt(dateParts[2]);
		
		String[] timeParts = time.split(":");
		int hour = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);
		
		Optional<Sala> os = salaService.findById(id);
		Sala s = os.get();
		
		Termin t = new Termin();
		t.setCena(0);
		t.setDoktor(null);
		t.setSala(s);
		t.setSlobodan(true);
		t.setTip("");
		t.setTrajanje(1);
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, minute);
		t.setDatum(c);
		s.getSlobodniTermini().add(t);
		terminService.save(t);
		salaService.save(s);
		
		return new ResponseEntity<String>("Dodat termin", HttpStatus.OK);
	}
}
