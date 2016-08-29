package com.mysql.entity;

public class Car {
	private String id;
	private String brand;
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Car [id=" + id + ", brand=" + brand + ", type=" + type + "]";
	}
	public Car(){};
	public Car(String id, String brand, String type) {
		super();
		this.id = id;
		this.brand = brand;
		this.type = type;
	}
	
	
}
