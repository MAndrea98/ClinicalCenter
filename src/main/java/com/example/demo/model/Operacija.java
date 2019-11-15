/*
 * author: Andrea Mendrei
 */
package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

enum StatusOperacije {
	ZAKAZAN, ZAVRSEN, OTKAZAN
}

@Entity
public class Operacija {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Pacijent pacijent;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Sala sala;
	
	@Column(name = "status", nullable = false)
	private StatusOperacije status;
	
	@Column(name = "datumIVremeOperacije", nullable = false)
	private LocalDateTime datumIVremeOperacije;
	
	@Column(name = "trajanje")
	private int trajanje;
	
	@ManyToMany
	@JoinTable(name = "operacije_doktori", joinColumns = @JoinColumn(name = "operacija_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "doktor_id", referencedColumnName = "id"))
	private Set<Doktor> doktori = new HashSet<Doktor>();
	
	public Operacija() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}

	public StatusOperacije getStatus() {
		return status;
	}

	public void setStatus(StatusOperacije status) {
		this.status = status;
	}

	public LocalDateTime getDatumIVremeOperacije() {
		return datumIVremeOperacije;
	}

	public void setDatumIVremeOperacije(LocalDateTime datumIVremeOperacije) {
		this.datumIVremeOperacije = datumIVremeOperacije;
	}

	public int getTrajanje() {
		return trajanje;
	}

	public void setTrajanje(int trajanje) {
		this.trajanje = trajanje;
	}

	public Pacijent getPacijent() {
		return pacijent;
	}

	public void setPacijent(Pacijent pacijent) {
		this.pacijent = pacijent;
	}

	public Set<Doktor> getDoktori() {
		return doktori;
	}

	public void setDoktori(Set<Doktor> doktori) {
		this.doktori = doktori;
	}

	@Override
	public String toString() {
		return "Operacija [id=" + id + ", pacijent=" + pacijent + ", sala=" + sala + ", status=" + status
				+ ", datumIVremeOperacije=" + datumIVremeOperacije + ", trajanje=" + trajanje + ", doktori=" + doktori
				+ "]";
	}
	
	
}