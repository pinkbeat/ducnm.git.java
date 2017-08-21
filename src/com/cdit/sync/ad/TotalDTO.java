package com.cdit.sync.ad;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TotalDTO {
	String objKhoi;
	String objBan;
	String tenKhoi;
	String dnKhoi;
	String dnBan;
	String tenBan;
	List danhSachBan;
	List danhSachKhoi;
	
	public TotalDTO(){}

	public TotalDTO(String tenKhoi, String tenBan, List danhSachBan) {
		super();
		this.tenKhoi = tenKhoi;
		this.tenBan = tenBan;
		this.danhSachBan = danhSachBan;
	}
	
	public TotalDTO(String tenKhoi, String tenBan, List danhSachBan, String objKhoi, String objBan) {
		super();
		this.tenKhoi = tenKhoi;
		this.tenBan = tenBan;
		this.danhSachBan = danhSachBan;
		this.objKhoi = objKhoi;
		this.objBan = objBan;
	}

	public List getDanhSachBan() {
		return danhSachBan;
	}

	public void setDanhSachBan(List danhSachBan) {
		this.danhSachBan = danhSachBan;
	}

	public String getTenBan() {
		return tenBan;
	}

	public void setTenBan(String tenBan) {
		this.tenBan = tenBan;
	}

	public String getTenKhoi() {
		return tenKhoi;
	}

	public void setTenKhoi(String tenKhoi) {
		this.tenKhoi = tenKhoi;
	}

	public String getObjKhoi() {
		return objKhoi;
	}

	public void setObjKhoi(String objKhoi) {
		this.objKhoi = objKhoi;
	}

	public String getObjBan() {
		return objBan;
	}

	public void setObjBan(String objBan) {
		this.objBan = objBan;
	}

	public String getDnKhoi() {
		return dnKhoi;
	}

	public void setDnKhoi(String dnKhoi) {
		this.dnKhoi = dnKhoi;
	}

	public String getDnBan() {
		return dnBan;
	}

	public void setDnBan(String dnBan) {
		this.dnBan = dnBan;
	}

	public List getDanhSachKhoi() {
		return danhSachKhoi;
	}

	public void setDanhSachKhoi(List danhSachKhoi) {
		this.danhSachKhoi = danhSachKhoi;
	}


	
}
